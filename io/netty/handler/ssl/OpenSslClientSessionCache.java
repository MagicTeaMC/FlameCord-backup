package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.util.AsciiString;
import java.util.HashMap;
import java.util.Map;

final class OpenSslClientSessionCache extends OpenSslSessionCache {
  private final Map<HostPort, OpenSslSessionCache.NativeSslSession> sessions = new HashMap<HostPort, OpenSslSessionCache.NativeSslSession>();
  
  OpenSslClientSessionCache(OpenSslEngineMap engineMap) {
    super(engineMap);
  }
  
  protected boolean sessionCreated(OpenSslSessionCache.NativeSslSession session) {
    assert Thread.holdsLock(this);
    HostPort hostPort = keyFor(session.getPeerHost(), session.getPeerPort());
    if (hostPort == null || this.sessions.containsKey(hostPort))
      return false; 
    this.sessions.put(hostPort, session);
    return true;
  }
  
  protected void sessionRemoved(OpenSslSessionCache.NativeSslSession session) {
    assert Thread.holdsLock(this);
    HostPort hostPort = keyFor(session.getPeerHost(), session.getPeerPort());
    if (hostPort == null)
      return; 
    this.sessions.remove(hostPort);
  }
  
  void setSession(long ssl, String host, int port) {
    OpenSslSessionCache.NativeSslSession session;
    boolean reused;
    HostPort hostPort = keyFor(host, port);
    if (hostPort == null)
      return; 
    synchronized (this) {
      session = this.sessions.get(hostPort);
      if (session == null)
        return; 
      if (!session.isValid()) {
        removeSessionWithId(session.sessionId());
        return;
      } 
      reused = SSL.setSession(ssl, session.session());
    } 
    if (reused) {
      if (session.shouldBeSingleUse())
        session.invalidate(); 
      session.updateLastAccessedTime();
    } 
  }
  
  private static HostPort keyFor(String host, int port) {
    if (host == null && port < 1)
      return null; 
    return new HostPort(host, port);
  }
  
  synchronized void clear() {
    super.clear();
    this.sessions.clear();
  }
  
  private static final class HostPort {
    private final int hash;
    
    private final String host;
    
    private final int port;
    
    HostPort(String host, int port) {
      this.host = host;
      this.port = port;
      this.hash = 31 * AsciiString.hashCode(host) + port;
    }
    
    public int hashCode() {
      return this.hash;
    }
    
    public boolean equals(Object obj) {
      if (!(obj instanceof HostPort))
        return false; 
      HostPort other = (HostPort)obj;
      return (this.port == other.port && this.host.equalsIgnoreCase(other.host));
    }
    
    public String toString() {
      return "HostPort{host='" + this.host + '\'' + ", port=" + this.port + '}';
    }
  }
}
