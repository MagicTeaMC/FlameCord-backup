package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSLSession;
import io.netty.internal.tcnative.SSLSessionCache;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.SystemPropertyUtil;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.X509Certificate;

class OpenSslSessionCache implements SSLSessionCache {
  private static final OpenSslSession[] EMPTY_SESSIONS = new OpenSslSession[0];
  
  private static final int DEFAULT_CACHE_SIZE;
  
  private final OpenSslEngineMap engineMap;
  
  static {
    int cacheSize = SystemPropertyUtil.getInt("javax.net.ssl.sessionCacheSize", 20480);
    if (cacheSize >= 0) {
      DEFAULT_CACHE_SIZE = cacheSize;
    } else {
      DEFAULT_CACHE_SIZE = 20480;
    } 
  }
  
  private final Map<OpenSslSessionId, NativeSslSession> sessions = new LinkedHashMap<OpenSslSessionId, NativeSslSession>() {
      private static final long serialVersionUID = -7773696788135734448L;
      
      protected boolean removeEldestEntry(Map.Entry<OpenSslSessionId, OpenSslSessionCache.NativeSslSession> eldest) {
        int maxSize = OpenSslSessionCache.this.maximumCacheSize.get();
        if (maxSize >= 0 && size() > maxSize)
          OpenSslSessionCache.this.removeSessionWithId(eldest.getKey()); 
        return false;
      }
    };
  
  private final AtomicInteger maximumCacheSize = new AtomicInteger(DEFAULT_CACHE_SIZE);
  
  private final AtomicInteger sessionTimeout = new AtomicInteger(300);
  
  private int sessionCounter;
  
  OpenSslSessionCache(OpenSslEngineMap engineMap) {
    this.engineMap = engineMap;
  }
  
  final void setSessionTimeout(int seconds) {
    int oldTimeout = this.sessionTimeout.getAndSet(seconds);
    if (oldTimeout > seconds)
      clear(); 
  }
  
  final int getSessionTimeout() {
    return this.sessionTimeout.get();
  }
  
  protected boolean sessionCreated(NativeSslSession session) {
    return true;
  }
  
  protected void sessionRemoved(NativeSslSession session) {}
  
  final void setSessionCacheSize(int size) {
    long oldSize = this.maximumCacheSize.getAndSet(size);
    if (oldSize > size || size == 0)
      clear(); 
  }
  
  final int getSessionCacheSize() {
    return this.maximumCacheSize.get();
  }
  
  private void expungeInvalidSessions() {
    if (this.sessions.isEmpty())
      return; 
    long now = System.currentTimeMillis();
    Iterator<Map.Entry<OpenSslSessionId, NativeSslSession>> iterator = this.sessions.entrySet().iterator();
    while (iterator.hasNext()) {
      NativeSslSession session = (NativeSslSession)((Map.Entry)iterator.next()).getValue();
      if (session.isValid(now))
        break; 
      iterator.remove();
      notifyRemovalAndFree(session);
    } 
  }
  
  public final boolean sessionCreated(long ssl, long sslSession) {
    ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
    if (engine == null)
      return false; 
    NativeSslSession session = new NativeSslSession(sslSession, engine.getPeerHost(), engine.getPeerPort(), getSessionTimeout() * 1000L);
    engine.setSessionId(session.sessionId());
    synchronized (this) {
      if (++this.sessionCounter == 255) {
        this.sessionCounter = 0;
        expungeInvalidSessions();
      } 
      if (!sessionCreated(session)) {
        session.close();
        return false;
      } 
      NativeSslSession old = this.sessions.put(session.sessionId(), session);
      if (old != null)
        notifyRemovalAndFree(old); 
    } 
    return true;
  }
  
  public final long getSession(long ssl, byte[] sessionId) {
    NativeSslSession session;
    OpenSslSessionId id = new OpenSslSessionId(sessionId);
    synchronized (this) {
      session = this.sessions.get(id);
      if (session == null)
        return -1L; 
      if (!session.isValid() || 
        
        !session.upRef()) {
        removeSessionWithId(session.sessionId());
        return -1L;
      } 
      if (session.shouldBeSingleUse())
        removeSessionWithId(session.sessionId()); 
    } 
    session.updateLastAccessedTime();
    return session.session();
  }
  
  void setSession(long ssl, String host, int port) {}
  
  final synchronized void removeSessionWithId(OpenSslSessionId id) {
    NativeSslSession sslSession = this.sessions.remove(id);
    if (sslSession != null)
      notifyRemovalAndFree(sslSession); 
  }
  
  final synchronized boolean containsSessionWithId(OpenSslSessionId id) {
    return this.sessions.containsKey(id);
  }
  
  private void notifyRemovalAndFree(NativeSslSession session) {
    sessionRemoved(session);
    session.free();
  }
  
  final synchronized OpenSslSession getSession(OpenSslSessionId id) {
    NativeSslSession session = this.sessions.get(id);
    if (session != null && !session.isValid()) {
      removeSessionWithId(session.sessionId());
      return null;
    } 
    return session;
  }
  
  final List<OpenSslSessionId> getIds() {
    OpenSslSession[] sessionsArray;
    synchronized (this) {
      sessionsArray = (OpenSslSession[])this.sessions.values().toArray((Object[])EMPTY_SESSIONS);
    } 
    List<OpenSslSessionId> ids = new ArrayList<OpenSslSessionId>(sessionsArray.length);
    for (OpenSslSession session : sessionsArray) {
      if (session.isValid())
        ids.add(session.sessionId()); 
    } 
    return ids;
  }
  
  synchronized void clear() {
    Iterator<Map.Entry<OpenSslSessionId, NativeSslSession>> iterator = this.sessions.entrySet().iterator();
    while (iterator.hasNext()) {
      NativeSslSession session = (NativeSslSession)((Map.Entry)iterator.next()).getValue();
      iterator.remove();
      notifyRemovalAndFree(session);
    } 
  }
  
  static final class NativeSslSession implements OpenSslSession {
    static final ResourceLeakDetector<NativeSslSession> LEAK_DETECTOR = ResourceLeakDetectorFactory.instance()
      .newResourceLeakDetector(NativeSslSession.class);
    
    private final ResourceLeakTracker<NativeSslSession> leakTracker;
    
    private final long session;
    
    private final String peerHost;
    
    private final int peerPort;
    
    private final OpenSslSessionId id;
    
    private final long timeout;
    
    private final long creationTime = System.currentTimeMillis();
    
    private volatile long lastAccessedTime = this.creationTime;
    
    private volatile boolean valid = true;
    
    private boolean freed;
    
    NativeSslSession(long session, String peerHost, int peerPort, long timeout) {
      this.session = session;
      this.peerHost = peerHost;
      this.peerPort = peerPort;
      this.timeout = timeout;
      this.id = new OpenSslSessionId(SSLSession.getSessionId(session));
      this.leakTracker = LEAK_DETECTOR.track(this);
    }
    
    public void setSessionId(OpenSslSessionId id) {
      throw new UnsupportedOperationException();
    }
    
    boolean shouldBeSingleUse() {
      assert !this.freed;
      return SSLSession.shouldBeSingleUse(this.session);
    }
    
    long session() {
      assert !this.freed;
      return this.session;
    }
    
    boolean upRef() {
      assert !this.freed;
      return SSLSession.upRef(this.session);
    }
    
    synchronized void free() {
      close();
      SSLSession.free(this.session);
    }
    
    void close() {
      assert !this.freed;
      this.freed = true;
      invalidate();
      if (this.leakTracker != null)
        this.leakTracker.close(this); 
    }
    
    public OpenSslSessionId sessionId() {
      return this.id;
    }
    
    boolean isValid(long now) {
      return (this.creationTime + this.timeout >= now && this.valid);
    }
    
    public void setLocalCertificate(Certificate[] localCertificate) {
      throw new UnsupportedOperationException();
    }
    
    public OpenSslSessionContext getSessionContext() {
      return null;
    }
    
    public void tryExpandApplicationBufferSize(int packetLengthDataOnly) {
      throw new UnsupportedOperationException();
    }
    
    public void handshakeFinished(byte[] id, String cipher, String protocol, byte[] peerCertificate, byte[][] peerCertificateChain, long creationTime, long timeout) {
      throw new UnsupportedOperationException();
    }
    
    public byte[] getId() {
      return this.id.cloneBytes();
    }
    
    public long getCreationTime() {
      return this.creationTime;
    }
    
    void updateLastAccessedTime() {
      this.lastAccessedTime = System.currentTimeMillis();
    }
    
    public long getLastAccessedTime() {
      return this.lastAccessedTime;
    }
    
    public void invalidate() {
      this.valid = false;
    }
    
    public boolean isValid() {
      return isValid(System.currentTimeMillis());
    }
    
    public void putValue(String name, Object value) {
      throw new UnsupportedOperationException();
    }
    
    public Object getValue(String name) {
      return null;
    }
    
    public void removeValue(String name) {}
    
    public String[] getValueNames() {
      return EmptyArrays.EMPTY_STRINGS;
    }
    
    public Certificate[] getPeerCertificates() {
      throw new UnsupportedOperationException();
    }
    
    public Certificate[] getLocalCertificates() {
      throw new UnsupportedOperationException();
    }
    
    public X509Certificate[] getPeerCertificateChain() {
      throw new UnsupportedOperationException();
    }
    
    public Principal getPeerPrincipal() {
      throw new UnsupportedOperationException();
    }
    
    public Principal getLocalPrincipal() {
      throw new UnsupportedOperationException();
    }
    
    public String getCipherSuite() {
      return null;
    }
    
    public String getProtocol() {
      return null;
    }
    
    public String getPeerHost() {
      return this.peerHost;
    }
    
    public int getPeerPort() {
      return this.peerPort;
    }
    
    public int getPacketBufferSize() {
      return ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE;
    }
    
    public int getApplicationBufferSize() {
      return ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
    }
    
    public int hashCode() {
      return this.id.hashCode();
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (!(o instanceof OpenSslSession))
        return false; 
      OpenSslSession session1 = (OpenSslSession)o;
      return this.id.equals(session1.sessionId());
    }
  }
}
