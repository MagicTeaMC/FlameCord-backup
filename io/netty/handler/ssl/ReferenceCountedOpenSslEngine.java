package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.util.LazyJavaxX509Certificate;
import io.netty.handler.ssl.util.LazyX509Certificate;
import io.netty.internal.tcnative.AsyncTask;
import io.netty.internal.tcnative.Buffer;
import io.netty.internal.tcnative.SSL;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.X509Certificate;

public class ReferenceCountedOpenSslEngine extends SSLEngine implements ReferenceCounted, ApplicationProtocolAccessor {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslEngine.class);
  
  private static final ResourceLeakDetector<ReferenceCountedOpenSslEngine> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslEngine.class);
  
  private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV2 = 0;
  
  private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV3 = 1;
  
  private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1 = 2;
  
  private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_1 = 3;
  
  private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_2 = 4;
  
  private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_3 = 5;
  
  private static final int[] OPENSSL_OP_NO_PROTOCOLS = new int[] { SSL.SSL_OP_NO_SSLv2, SSL.SSL_OP_NO_SSLv3, SSL.SSL_OP_NO_TLSv1, SSL.SSL_OP_NO_TLSv1_1, SSL.SSL_OP_NO_TLSv1_2, SSL.SSL_OP_NO_TLSv1_3 };
  
  static final int MAX_PLAINTEXT_LENGTH = SSL.SSL_MAX_PLAINTEXT_LENGTH;
  
  static final int MAX_RECORD_SIZE = SSL.SSL_MAX_RECORD_LENGTH;
  
  private static final SSLEngineResult NEED_UNWRAP_OK = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
  
  private static final SSLEngineResult NEED_UNWRAP_CLOSED = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
  
  private static final SSLEngineResult NEED_WRAP_OK = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
  
  private static final SSLEngineResult NEED_WRAP_CLOSED = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
  
  private static final SSLEngineResult CLOSED_NOT_HANDSHAKING = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
  
  private long ssl;
  
  private long networkBIO;
  
  private HandshakeState handshakeState;
  
  private boolean receivedShutdown;
  
  private volatile boolean destroyed;
  
  private volatile String applicationProtocol;
  
  private volatile boolean needTask;
  
  private String[] explicitlyEnabledProtocols;
  
  private boolean sessionSet;
  
  private final ResourceLeakTracker<ReferenceCountedOpenSslEngine> leak;
  
  private final AbstractReferenceCounted refCnt;
  
  private volatile ClientAuth clientAuth;
  
  private volatile long lastAccessed;
  
  private String endPointIdentificationAlgorithm;
  
  private Object algorithmConstraints;
  
  private List<String> sniHostNames;
  
  private volatile Collection<?> matchers;
  
  private boolean isInboundDone;
  
  private boolean outboundClosed;
  
  final boolean jdkCompatibilityMode;
  
  private final boolean clientMode;
  
  final ByteBufAllocator alloc;
  
  private final OpenSslEngineMap engineMap;
  
  private final OpenSslApplicationProtocolNegotiator apn;
  
  private final ReferenceCountedOpenSslContext parentContext;
  
  private final OpenSslSession session;
  
  private final ByteBuffer[] singleSrcBuffer;
  
  private final ByteBuffer[] singleDstBuffer;
  
  private final boolean enableOcsp;
  
  private int maxWrapOverhead;
  
  private int maxWrapBufferSize;
  
  private Throwable pendingException;
  
  private enum HandshakeState {
    NOT_STARTED, STARTED_IMPLICITLY, STARTED_EXPLICITLY, FINISHED;
  }
  
  ReferenceCountedOpenSslEngine(ReferenceCountedOpenSslContext context, ByteBufAllocator alloc, String peerHost, int peerPort, boolean jdkCompatibilityMode, boolean leakDetection) {
    super(peerHost, peerPort);
    long finalSsl;
    this.handshakeState = HandshakeState.NOT_STARTED;
    this.refCnt = new AbstractReferenceCounted() {
        public ReferenceCounted touch(Object hint) {
          if (ReferenceCountedOpenSslEngine.this.leak != null)
            ReferenceCountedOpenSslEngine.this.leak.record(hint); 
          return ReferenceCountedOpenSslEngine.this;
        }
        
        protected void deallocate() {
          ReferenceCountedOpenSslEngine.this.shutdown();
          if (ReferenceCountedOpenSslEngine.this.leak != null) {
            boolean closed = ReferenceCountedOpenSslEngine.this.leak.close(ReferenceCountedOpenSslEngine.this);
            assert closed;
          } 
          ReferenceCountedOpenSslEngine.this.parentContext.release();
        }
      };
    this.clientAuth = ClientAuth.NONE;
    this.lastAccessed = -1L;
    this.singleSrcBuffer = new ByteBuffer[1];
    this.singleDstBuffer = new ByteBuffer[1];
    OpenSsl.ensureAvailability();
    this.engineMap = context.engineMap;
    this.enableOcsp = context.enableOcsp;
    this.jdkCompatibilityMode = jdkCompatibilityMode;
    this.alloc = (ByteBufAllocator)ObjectUtil.checkNotNull(alloc, "alloc");
    this.apn = (OpenSslApplicationProtocolNegotiator)context.applicationProtocolNegotiator();
    this.clientMode = context.isClient();
    if (PlatformDependent.javaVersion() >= 7) {
      this.session = new ExtendedOpenSslSession(new DefaultOpenSslSession(context.sessionContext())) {
          private String[] peerSupportedSignatureAlgorithms;
          
          private List requestedServerNames;
          
          public List getRequestedServerNames() {
            if (ReferenceCountedOpenSslEngine.this.clientMode)
              return Java8SslUtils.getSniHostNames(ReferenceCountedOpenSslEngine.this.sniHostNames); 
            synchronized (ReferenceCountedOpenSslEngine.this) {
              if (this.requestedServerNames == null)
                if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                  this.requestedServerNames = Collections.emptyList();
                } else {
                  String name = SSL.getSniHostname(ReferenceCountedOpenSslEngine.this.ssl);
                  if (name == null) {
                    this.requestedServerNames = Collections.emptyList();
                  } else {
                    this
                      .requestedServerNames = Java8SslUtils.getSniHostName(
                        SSL.getSniHostname(ReferenceCountedOpenSslEngine.this.ssl).getBytes(CharsetUtil.UTF_8));
                  } 
                }  
              return this.requestedServerNames;
            } 
          }
          
          public String[] getPeerSupportedSignatureAlgorithms() {
            synchronized (ReferenceCountedOpenSslEngine.this) {
              if (this.peerSupportedSignatureAlgorithms == null)
                if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                  this.peerSupportedSignatureAlgorithms = EmptyArrays.EMPTY_STRINGS;
                } else {
                  String[] algs = SSL.getSigAlgs(ReferenceCountedOpenSslEngine.this.ssl);
                  if (algs == null) {
                    this.peerSupportedSignatureAlgorithms = EmptyArrays.EMPTY_STRINGS;
                  } else {
                    Set<String> algorithmList = new LinkedHashSet<String>(algs.length);
                    for (String alg : algs) {
                      String converted = SignatureAlgorithmConverter.toJavaName(alg);
                      if (converted != null)
                        algorithmList.add(converted); 
                    } 
                    this.peerSupportedSignatureAlgorithms = algorithmList.<String>toArray(new String[0]);
                  } 
                }  
              return (String[])this.peerSupportedSignatureAlgorithms.clone();
            } 
          }
          
          public List<byte[]> getStatusResponses() {
            byte[] ocspResponse = null;
            if (ReferenceCountedOpenSslEngine.this.enableOcsp && ReferenceCountedOpenSslEngine.this.clientMode)
              synchronized (ReferenceCountedOpenSslEngine.this) {
                if (!ReferenceCountedOpenSslEngine.this.isDestroyed())
                  ocspResponse = SSL.getOcspResponse(ReferenceCountedOpenSslEngine.this.ssl); 
              }  
            return (ocspResponse == null) ? 
              (List)Collections.<byte[]>emptyList() : (List)Collections.<byte[]>singletonList(ocspResponse);
          }
        };
    } else {
      this.session = new DefaultOpenSslSession(context.sessionContext());
    } 
    if (!context.sessionContext().useKeyManager())
      this.session.setLocalCertificate(context.keyCertChain); 
    Lock readerLock = context.ctxLock.readLock();
    readerLock.lock();
    try {
      finalSsl = SSL.newSSL(context.ctx, !context.isClient());
    } finally {
      readerLock.unlock();
    } 
    synchronized (this) {
      this.ssl = finalSsl;
      try {
        this.networkBIO = SSL.bioNewByteBuffer(this.ssl, context.getBioNonApplicationBufferSize());
        setClientAuth(this.clientMode ? ClientAuth.NONE : context.clientAuth);
        if (context.protocols != null) {
          setEnabledProtocols0(context.protocols, true);
        } else {
          this.explicitlyEnabledProtocols = getEnabledProtocols();
        } 
        if (this.clientMode && SslUtils.isValidHostNameForSNI(peerHost))
          if (PlatformDependent.javaVersion() >= 8) {
            if (Java8SslUtils.isValidHostNameForSNI(peerHost)) {
              SSL.setTlsExtHostName(this.ssl, peerHost);
              this.sniHostNames = Collections.singletonList(peerHost);
            } 
          } else {
            SSL.setTlsExtHostName(this.ssl, peerHost);
            this.sniHostNames = Collections.singletonList(peerHost);
          }  
        if (this.enableOcsp)
          SSL.enableOcsp(this.ssl); 
        if (!jdkCompatibilityMode)
          SSL.setMode(this.ssl, SSL.getMode(this.ssl) | SSL.SSL_MODE_ENABLE_PARTIAL_WRITE); 
        if (isProtocolEnabled(SSL.getOptions(this.ssl), SSL.SSL_OP_NO_TLSv1_3, "TLSv1.3")) {
          boolean enableTickets = this.clientMode ? ReferenceCountedOpenSslContext.CLIENT_ENABLE_SESSION_TICKET_TLSV13 : ReferenceCountedOpenSslContext.SERVER_ENABLE_SESSION_TICKET_TLSV13;
          if (enableTickets)
            SSL.clearOptions(this.ssl, SSL.SSL_OP_NO_TICKET); 
        } 
        if (OpenSsl.isBoringSSL() && this.clientMode)
          SSL.setRenegotiateMode(this.ssl, SSL.SSL_RENEGOTIATE_ONCE); 
        calculateMaxWrapOverhead();
      } catch (Throwable cause) {
        shutdown();
        PlatformDependent.throwException(cause);
      } 
    } 
    this.parentContext = context;
    this.parentContext.retain();
    this.leak = leakDetection ? leakDetector.track(this) : null;
  }
  
  final synchronized String[] authMethods() {
    if (isDestroyed())
      return EmptyArrays.EMPTY_STRINGS; 
    return SSL.authenticationMethods(this.ssl);
  }
  
  final boolean setKeyMaterial(OpenSslKeyMaterial keyMaterial) throws Exception {
    synchronized (this) {
      if (isDestroyed())
        return false; 
      SSL.setKeyMaterial(this.ssl, keyMaterial.certificateChainAddress(), keyMaterial.privateKeyAddress());
    } 
    this.session.setLocalCertificate((Certificate[])keyMaterial.certificateChain());
    return true;
  }
  
  final synchronized SecretKeySpec masterKey() {
    if (isDestroyed())
      return null; 
    return new SecretKeySpec(SSL.getMasterKey(this.ssl), "AES");
  }
  
  synchronized boolean isSessionReused() {
    if (isDestroyed())
      return false; 
    return SSL.isSessionReused(this.ssl);
  }
  
  public void setOcspResponse(byte[] response) {
    if (!this.enableOcsp)
      throw new IllegalStateException("OCSP stapling is not enabled"); 
    if (this.clientMode)
      throw new IllegalStateException("Not a server SSLEngine"); 
    synchronized (this) {
      if (!isDestroyed())
        SSL.setOcspResponse(this.ssl, response); 
    } 
  }
  
  public byte[] getOcspResponse() {
    if (!this.enableOcsp)
      throw new IllegalStateException("OCSP stapling is not enabled"); 
    if (!this.clientMode)
      throw new IllegalStateException("Not a client SSLEngine"); 
    synchronized (this) {
      if (isDestroyed())
        return EmptyArrays.EMPTY_BYTES; 
      return SSL.getOcspResponse(this.ssl);
    } 
  }
  
  public final int refCnt() {
    return this.refCnt.refCnt();
  }
  
  public final ReferenceCounted retain() {
    this.refCnt.retain();
    return this;
  }
  
  public final ReferenceCounted retain(int increment) {
    this.refCnt.retain(increment);
    return this;
  }
  
  public final ReferenceCounted touch() {
    this.refCnt.touch();
    return this;
  }
  
  public final ReferenceCounted touch(Object hint) {
    this.refCnt.touch(hint);
    return this;
  }
  
  public final boolean release() {
    return this.refCnt.release();
  }
  
  public final boolean release(int decrement) {
    return this.refCnt.release(decrement);
  }
  
  public String getApplicationProtocol() {
    return this.applicationProtocol;
  }
  
  public String getHandshakeApplicationProtocol() {
    return this.applicationProtocol;
  }
  
  public final synchronized SSLSession getHandshakeSession() {
    switch (this.handshakeState) {
      case NONE:
      case ALPN:
        return null;
    } 
    return this.session;
  }
  
  public final synchronized long sslPointer() {
    return this.ssl;
  }
  
  public final synchronized void shutdown() {
    if (!this.destroyed) {
      this.destroyed = true;
      if (this.engineMap != null)
        this.engineMap.remove(this.ssl); 
      SSL.freeSSL(this.ssl);
      this.ssl = this.networkBIO = 0L;
      this.isInboundDone = this.outboundClosed = true;
    } 
    SSL.clearError();
  }
  
  private int writePlaintextData(ByteBuffer src, int len) {
    int sslWrote, pos = src.position();
    int limit = src.limit();
    if (src.isDirect()) {
      sslWrote = SSL.writeToSSL(this.ssl, bufferAddress(src) + pos, len);
      if (sslWrote > 0)
        src.position(pos + sslWrote); 
    } else {
      ByteBuf buf = this.alloc.directBuffer(len);
      try {
        src.limit(pos + len);
        buf.setBytes(0, src);
        src.limit(limit);
        sslWrote = SSL.writeToSSL(this.ssl, OpenSsl.memoryAddress(buf), len);
        if (sslWrote > 0) {
          src.position(pos + sslWrote);
        } else {
          src.position(pos);
        } 
      } finally {
        buf.release();
      } 
    } 
    return sslWrote;
  }
  
  synchronized void bioSetFd(int fd) {
    if (!isDestroyed())
      SSL.bioSetFd(this.ssl, fd); 
  }
  
  private ByteBuf writeEncryptedData(ByteBuffer src, int len) throws SSLException {
    int pos = src.position();
    if (src.isDirect()) {
      SSL.bioSetByteBuffer(this.networkBIO, bufferAddress(src) + pos, len, false);
    } else {
      ByteBuf buf = this.alloc.directBuffer(len);
      try {
        int limit = src.limit();
        src.limit(pos + len);
        buf.writeBytes(src);
        src.position(pos);
        src.limit(limit);
        SSL.bioSetByteBuffer(this.networkBIO, OpenSsl.memoryAddress(buf), len, false);
        return buf;
      } catch (Throwable cause) {
        buf.release();
        PlatformDependent.throwException(cause);
      } 
    } 
    return null;
  }
  
  private int readPlaintextData(ByteBuffer dst) throws SSLException {
    int sslRead, pos = dst.position();
    if (dst.isDirect()) {
      sslRead = SSL.readFromSSL(this.ssl, bufferAddress(dst) + pos, dst.limit() - pos);
      if (sslRead > 0)
        dst.position(pos + sslRead); 
    } else {
      int limit = dst.limit();
      int len = Math.min(maxEncryptedPacketLength0(), limit - pos);
      ByteBuf buf = this.alloc.directBuffer(len);
      try {
        sslRead = SSL.readFromSSL(this.ssl, OpenSsl.memoryAddress(buf), len);
        if (sslRead > 0) {
          dst.limit(pos + sslRead);
          buf.getBytes(buf.readerIndex(), dst);
          dst.limit(limit);
        } 
      } finally {
        buf.release();
      } 
    } 
    return sslRead;
  }
  
  final synchronized int maxWrapOverhead() {
    return this.maxWrapOverhead;
  }
  
  final synchronized int maxEncryptedPacketLength() {
    return maxEncryptedPacketLength0();
  }
  
  final int maxEncryptedPacketLength0() {
    return this.maxWrapOverhead + MAX_PLAINTEXT_LENGTH;
  }
  
  final int calculateMaxLengthForWrap(int plaintextLength, int numComponents) {
    return (int)Math.min(this.maxWrapBufferSize, plaintextLength + this.maxWrapOverhead * numComponents);
  }
  
  final synchronized int sslPending() {
    return sslPending0();
  }
  
  private void calculateMaxWrapOverhead() {
    this.maxWrapOverhead = SSL.getMaxWrapOverhead(this.ssl);
    this.maxWrapBufferSize = this.jdkCompatibilityMode ? maxEncryptedPacketLength0() : (maxEncryptedPacketLength0() << 4);
  }
  
  private int sslPending0() {
    return (this.handshakeState != HandshakeState.FINISHED) ? 0 : SSL.sslPending(this.ssl);
  }
  
  private boolean isBytesAvailableEnoughForWrap(int bytesAvailable, int plaintextLength, int numComponents) {
    return (bytesAvailable - this.maxWrapOverhead * numComponents >= plaintextLength);
  }
  
  public final SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
    ObjectUtil.checkNotNullWithIAE(srcs, "srcs");
    ObjectUtil.checkNotNullWithIAE(dst, "dst");
    if (offset >= srcs.length || offset + length > srcs.length)
      throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))"); 
    if (dst.isReadOnly())
      throw new ReadOnlyBufferException(); 
    synchronized (this) {
      if (isOutboundDone())
        return (isInboundDone() || isDestroyed()) ? CLOSED_NOT_HANDSHAKING : NEED_UNWRAP_CLOSED; 
      int bytesProduced = 0;
      ByteBuf bioReadCopyBuf = null;
      try {
        if (dst.isDirect()) {
          SSL.bioSetByteBuffer(this.networkBIO, bufferAddress(dst) + dst.position(), dst.remaining(), true);
        } else {
          bioReadCopyBuf = this.alloc.directBuffer(dst.remaining());
          SSL.bioSetByteBuffer(this.networkBIO, OpenSsl.memoryAddress(bioReadCopyBuf), bioReadCopyBuf.writableBytes(), true);
        } 
        int bioLengthBefore = SSL.bioLengthByteBuffer(this.networkBIO);
        if (this.outboundClosed) {
          if (!isBytesAvailableEnoughForWrap(dst.remaining(), 2, 1))
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, getHandshakeStatus(), 0, 0); 
          bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
          if (bytesProduced <= 0)
            return newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0); 
          if (!doSSLShutdown())
            return newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, bytesProduced); 
          bytesProduced = bioLengthBefore - SSL.bioLengthByteBuffer(this.networkBIO);
          return newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, bytesProduced);
        } 
        SSLEngineResult.HandshakeStatus status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        HandshakeState oldHandshakeState = this.handshakeState;
        if (this.handshakeState != HandshakeState.FINISHED) {
          if (this.handshakeState != HandshakeState.STARTED_EXPLICITLY)
            this.handshakeState = HandshakeState.STARTED_IMPLICITLY; 
          bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
          if (this.pendingException != null) {
            if (bytesProduced > 0)
              return newResult(SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, bytesProduced); 
            return newResult(handshakeException(), 0, 0);
          } 
          status = handshake();
          bytesProduced = bioLengthBefore - SSL.bioLengthByteBuffer(this.networkBIO);
          if (status == SSLEngineResult.HandshakeStatus.NEED_TASK)
            return newResult(status, 0, bytesProduced); 
          if (bytesProduced > 0)
            return newResult(mayFinishHandshake((status != SSLEngineResult.HandshakeStatus.FINISHED) ? ((bytesProduced == bioLengthBefore) ? SSLEngineResult.HandshakeStatus.NEED_WRAP : 
                  
                  getHandshakeStatus(SSL.bioLengthNonApplication(this.networkBIO))) : SSLEngineResult.HandshakeStatus.FINISHED), 0, bytesProduced); 
          if (status == SSLEngineResult.HandshakeStatus.NEED_UNWRAP)
            return isOutboundDone() ? NEED_UNWRAP_CLOSED : NEED_UNWRAP_OK; 
          if (this.outboundClosed) {
            bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
            return newResultMayFinishHandshake(status, 0, bytesProduced);
          } 
        } 
        int endOffset = offset + length;
        if (this.jdkCompatibilityMode || oldHandshakeState != HandshakeState.FINISHED) {
          int srcsLen = 0;
          for (int i = offset; i < endOffset; i++) {
            ByteBuffer src = srcs[i];
            if (src == null)
              throw new IllegalArgumentException("srcs[" + i + "] is null"); 
            if (srcsLen != MAX_PLAINTEXT_LENGTH) {
              srcsLen += src.remaining();
              if (srcsLen > MAX_PLAINTEXT_LENGTH || srcsLen < 0)
                srcsLen = MAX_PLAINTEXT_LENGTH; 
            } 
          } 
          if (!isBytesAvailableEnoughForWrap(dst.remaining(), srcsLen, 1))
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, getHandshakeStatus(), 0, 0); 
        } 
        int bytesConsumed = 0;
        assert bytesProduced == 0;
        bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
        if (bytesProduced > 0)
          return newResultMayFinishHandshake(status, bytesConsumed, bytesProduced); 
        if (this.pendingException != null) {
          Throwable error = this.pendingException;
          this.pendingException = null;
          shutdown();
          throw new SSLException(error);
        } 
        for (; offset < endOffset; offset++) {
          ByteBuffer src = srcs[offset];
          int remaining = src.remaining();
          if (remaining != 0) {
            int bytesWritten;
            if (this.jdkCompatibilityMode) {
              bytesWritten = writePlaintextData(src, Math.min(remaining, MAX_PLAINTEXT_LENGTH - bytesConsumed));
            } else {
              int availableCapacityForWrap = dst.remaining() - bytesProduced - this.maxWrapOverhead;
              if (availableCapacityForWrap <= 0)
                return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, getHandshakeStatus(), bytesConsumed, bytesProduced); 
              bytesWritten = writePlaintextData(src, Math.min(remaining, availableCapacityForWrap));
            } 
            int pendingNow = SSL.bioLengthByteBuffer(this.networkBIO);
            bytesProduced += bioLengthBefore - pendingNow;
            bioLengthBefore = pendingNow;
            if (bytesWritten > 0) {
              bytesConsumed += bytesWritten;
              if (this.jdkCompatibilityMode || bytesProduced == dst.remaining())
                return newResultMayFinishHandshake(status, bytesConsumed, bytesProduced); 
            } else {
              int sslError = SSL.getError(this.ssl, bytesWritten);
              if (sslError == SSL.SSL_ERROR_ZERO_RETURN) {
                if (!this.receivedShutdown) {
                  closeAll();
                  bytesProduced += bioLengthBefore - SSL.bioLengthByteBuffer(this.networkBIO);
                  SSLEngineResult.HandshakeStatus hs = mayFinishHandshake((status != SSLEngineResult.HandshakeStatus.FINISHED) ? (
                      (bytesProduced == dst.remaining()) ? SSLEngineResult.HandshakeStatus.NEED_WRAP : 
                      getHandshakeStatus(SSL.bioLengthNonApplication(this.networkBIO))) : SSLEngineResult.HandshakeStatus.FINISHED);
                  return newResult(hs, bytesConsumed, bytesProduced);
                } 
                return newResult(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, bytesConsumed, bytesProduced);
              } 
              if (sslError == SSL.SSL_ERROR_WANT_READ)
                return newResult(SSLEngineResult.HandshakeStatus.NEED_UNWRAP, bytesConsumed, bytesProduced); 
              if (sslError == SSL.SSL_ERROR_WANT_WRITE) {
                if (bytesProduced > 0)
                  return newResult(SSLEngineResult.HandshakeStatus.NEED_WRAP, bytesConsumed, bytesProduced); 
                return newResult(SSLEngineResult.Status.BUFFER_OVERFLOW, status, bytesConsumed, bytesProduced);
              } 
              if (sslError == SSL.SSL_ERROR_WANT_X509_LOOKUP || sslError == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY || sslError == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION)
                return newResult(SSLEngineResult.HandshakeStatus.NEED_TASK, bytesConsumed, bytesProduced); 
              throw shutdownWithError("SSL_write", sslError);
            } 
          } 
        } 
        return newResultMayFinishHandshake(status, bytesConsumed, bytesProduced);
      } finally {
        SSL.bioClearByteBuffer(this.networkBIO);
        if (bioReadCopyBuf == null) {
          dst.position(dst.position() + bytesProduced);
        } else {
          assert bioReadCopyBuf.readableBytes() <= dst.remaining() : "The destination buffer " + dst + " didn't have enough remaining space to hold the encrypted content in " + bioReadCopyBuf;
          dst.put(bioReadCopyBuf.internalNioBuffer(bioReadCopyBuf.readerIndex(), bytesProduced));
          bioReadCopyBuf.release();
        } 
      } 
    } 
  }
  
  private SSLEngineResult newResult(SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) {
    return newResult(SSLEngineResult.Status.OK, hs, bytesConsumed, bytesProduced);
  }
  
  private SSLEngineResult newResult(SSLEngineResult.Status status, SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) {
    if (isOutboundDone()) {
      if (isInboundDone()) {
        hs = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        shutdown();
      } 
      return new SSLEngineResult(SSLEngineResult.Status.CLOSED, hs, bytesConsumed, bytesProduced);
    } 
    if (hs == SSLEngineResult.HandshakeStatus.NEED_TASK)
      this.needTask = true; 
    return new SSLEngineResult(status, hs, bytesConsumed, bytesProduced);
  }
  
  private SSLEngineResult newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) throws SSLException {
    return newResult(mayFinishHandshake(hs, bytesConsumed, bytesProduced), bytesConsumed, bytesProduced);
  }
  
  private SSLEngineResult newResultMayFinishHandshake(SSLEngineResult.Status status, SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) throws SSLException {
    return newResult(status, mayFinishHandshake(hs, bytesConsumed, bytesProduced), bytesConsumed, bytesProduced);
  }
  
  private SSLException shutdownWithError(String operations, int sslError) {
    return shutdownWithError(operations, sslError, SSL.getLastErrorNumber());
  }
  
  private SSLException shutdownWithError(String operation, int sslError, int error) {
    if (logger.isDebugEnabled()) {
      String errorString = SSL.getErrorString(error);
      logger.debug("{} failed with {}: OpenSSL error: {} {}", new Object[] { operation, 
            Integer.valueOf(sslError), Integer.valueOf(error), errorString });
    } 
    shutdown();
    SSLException exception = newSSLExceptionForError(error);
    if (this.pendingException != null) {
      exception.initCause(this.pendingException);
      this.pendingException = null;
    } 
    return exception;
  }
  
  private SSLEngineResult handleUnwrapException(int bytesConsumed, int bytesProduced, SSLException e) throws SSLException {
    int lastError = SSL.getLastErrorNumber();
    if (lastError != 0)
      return sslReadErrorResult(SSL.SSL_ERROR_SSL, lastError, bytesConsumed, bytesProduced); 
    throw e;
  }
  
  public final SSLEngineResult unwrap(ByteBuffer[] srcs, int srcsOffset, int srcsLength, ByteBuffer[] dsts, int dstsOffset, int dstsLength) throws SSLException {
    ObjectUtil.checkNotNullWithIAE(srcs, "srcs");
    if (srcsOffset >= srcs.length || srcsOffset + srcsLength > srcs.length)
      throw new IndexOutOfBoundsException("offset: " + srcsOffset + ", length: " + srcsLength + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))"); 
    ObjectUtil.checkNotNullWithIAE(dsts, "dsts");
    if (dstsOffset >= dsts.length || dstsOffset + dstsLength > dsts.length)
      throw new IndexOutOfBoundsException("offset: " + dstsOffset + ", length: " + dstsLength + " (expected: offset <= offset + length <= dsts.length (" + dsts.length + "))"); 
    long capacity = 0L;
    int dstsEndOffset = dstsOffset + dstsLength;
    for (int i = dstsOffset; i < dstsEndOffset; i++) {
      ByteBuffer dst = (ByteBuffer)ObjectUtil.checkNotNullArrayParam(dsts[i], i, "dsts");
      if (dst.isReadOnly())
        throw new ReadOnlyBufferException(); 
      capacity += dst.remaining();
    } 
    int srcsEndOffset = srcsOffset + srcsLength;
    long len = 0L;
    for (int j = srcsOffset; j < srcsEndOffset; j++) {
      ByteBuffer src = (ByteBuffer)ObjectUtil.checkNotNullArrayParam(srcs[j], j, "srcs");
      len += src.remaining();
    } 
    synchronized (this) {
      int packetLength;
      if (isInboundDone())
        return (isOutboundDone() || isDestroyed()) ? CLOSED_NOT_HANDSHAKING : NEED_WRAP_CLOSED; 
      SSLEngineResult.HandshakeStatus status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
      HandshakeState oldHandshakeState = this.handshakeState;
      if (this.handshakeState != HandshakeState.FINISHED) {
        if (this.handshakeState != HandshakeState.STARTED_EXPLICITLY)
          this.handshakeState = HandshakeState.STARTED_IMPLICITLY; 
        status = handshake();
        if (status == SSLEngineResult.HandshakeStatus.NEED_TASK)
          return newResult(status, 0, 0); 
        if (status == SSLEngineResult.HandshakeStatus.NEED_WRAP)
          return NEED_WRAP_OK; 
        if (this.isInboundDone)
          return NEED_WRAP_CLOSED; 
      } 
      int sslPending = sslPending0();
      if (this.jdkCompatibilityMode || oldHandshakeState != HandshakeState.FINISHED) {
        if (len < 5L)
          return newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0); 
        packetLength = SslUtils.getEncryptedPacketLength(srcs, srcsOffset);
        if (packetLength == -2)
          throw new NotSslRecordException("not an SSL/TLS record"); 
        int packetLengthDataOnly = packetLength - 5;
        if (packetLengthDataOnly > capacity) {
          if (packetLengthDataOnly > MAX_RECORD_SIZE)
            throw new SSLException("Illegal packet length: " + packetLengthDataOnly + " > " + this.session
                .getApplicationBufferSize()); 
          this.session.tryExpandApplicationBufferSize(packetLengthDataOnly);
          return newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_OVERFLOW, status, 0, 0);
        } 
        if (len < packetLength)
          return newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0); 
      } else {
        if (len == 0L && sslPending <= 0)
          return newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0); 
        if (capacity == 0L)
          return newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_OVERFLOW, status, 0, 0); 
        packetLength = (int)Math.min(2147483647L, len);
      } 
      assert srcsOffset < srcsEndOffset;
      assert capacity > 0L;
      int bytesProduced = 0;
      int bytesConsumed = 0;
      try {
        while (true) {
          ByteBuf bioWriteCopyBuf;
          int pendingEncryptedBytes;
          ByteBuffer src = srcs[srcsOffset];
          int remaining = src.remaining();
          if (remaining == 0) {
            if (sslPending <= 0) {
              if (++srcsOffset >= srcsEndOffset)
                break; 
              continue;
            } 
            bioWriteCopyBuf = null;
            pendingEncryptedBytes = SSL.bioLengthByteBuffer(this.networkBIO);
          } else {
            pendingEncryptedBytes = Math.min(packetLength, remaining);
            try {
              bioWriteCopyBuf = writeEncryptedData(src, pendingEncryptedBytes);
            } catch (SSLException e) {
              return handleUnwrapException(bytesConsumed, bytesProduced, e);
            } 
          } 
          while (true) {
            int bytesRead;
            ByteBuffer dst = dsts[dstsOffset];
            if (!dst.hasRemaining()) {
              if (++dstsOffset >= dstsEndOffset) {
                if (bioWriteCopyBuf != null)
                  bioWriteCopyBuf.release(); 
                break;
              } 
              continue;
            } 
            try {
              bytesRead = readPlaintextData(dst);
            } catch (SSLException e) {
              SSLEngineResult sSLEngineResult1 = handleUnwrapException(bytesConsumed, bytesProduced, e);
              if (bioWriteCopyBuf != null)
                bioWriteCopyBuf.release(); 
              return sSLEngineResult1;
            } 
            int localBytesConsumed = pendingEncryptedBytes - SSL.bioLengthByteBuffer(this.networkBIO);
            bytesConsumed += localBytesConsumed;
            packetLength -= localBytesConsumed;
            pendingEncryptedBytes -= localBytesConsumed;
            src.position(src.position() + localBytesConsumed);
            if (bytesRead > 0) {
              bytesProduced += bytesRead;
              if (!dst.hasRemaining()) {
                sslPending = sslPending0();
                if (++dstsOffset >= dstsEndOffset) {
                  SSLEngineResult sSLEngineResult1 = (sslPending > 0) ? newResult(SSLEngineResult.Status.BUFFER_OVERFLOW, status, bytesConsumed, bytesProduced) : newResultMayFinishHandshake(isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced);
                  if (bioWriteCopyBuf != null)
                    bioWriteCopyBuf.release(); 
                  return sSLEngineResult1;
                } 
                continue;
              } 
              if (packetLength == 0 || this.jdkCompatibilityMode) {
                if (bioWriteCopyBuf != null)
                  bioWriteCopyBuf.release(); 
                break;
              } 
              continue;
            } 
            int sslError = SSL.getError(this.ssl, bytesRead);
            if (sslError == SSL.SSL_ERROR_WANT_READ || sslError == SSL.SSL_ERROR_WANT_WRITE) {
              if (++srcsOffset >= srcsEndOffset) {
                if (bioWriteCopyBuf != null)
                  bioWriteCopyBuf.release(); 
                break;
              } 
              if (bioWriteCopyBuf != null)
                bioWriteCopyBuf.release(); 
              continue;
            } 
            if (sslError == SSL.SSL_ERROR_ZERO_RETURN) {
              if (!this.receivedShutdown)
                closeAll(); 
              SSLEngineResult sSLEngineResult1 = newResultMayFinishHandshake(isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced);
              if (bioWriteCopyBuf != null)
                bioWriteCopyBuf.release(); 
              return sSLEngineResult1;
            } 
            if (sslError == SSL.SSL_ERROR_WANT_X509_LOOKUP || sslError == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY || sslError == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION) {
              SSLEngineResult sSLEngineResult1 = newResult(isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_TASK, bytesConsumed, bytesProduced);
              if (bioWriteCopyBuf != null)
                bioWriteCopyBuf.release(); 
              return sSLEngineResult1;
            } 
            SSLEngineResult sSLEngineResult = sslReadErrorResult(sslError, SSL.getLastErrorNumber(), bytesConsumed, bytesProduced);
            if (bioWriteCopyBuf != null)
              bioWriteCopyBuf.release(); 
            return sSLEngineResult;
          } 
          break;
        } 
      } finally {
        SSL.bioClearByteBuffer(this.networkBIO);
        rejectRemoteInitiatedRenegotiation();
      } 
      if (!this.receivedShutdown && (SSL.getShutdown(this.ssl) & SSL.SSL_RECEIVED_SHUTDOWN) == SSL.SSL_RECEIVED_SHUTDOWN)
        closeAll(); 
      return newResultMayFinishHandshake(isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced);
    } 
  }
  
  private boolean needWrapAgain(int stackError) {
    if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
      if (this.pendingException == null) {
        this.pendingException = newSSLExceptionForError(stackError);
      } else if (shouldAddSuppressed(this.pendingException, stackError)) {
        ThrowableUtil.addSuppressed(this.pendingException, newSSLExceptionForError(stackError));
      } 
      SSL.clearError();
      return true;
    } 
    return false;
  }
  
  private SSLException newSSLExceptionForError(int stackError) {
    String message = SSL.getErrorString(stackError);
    return (this.handshakeState == HandshakeState.FINISHED) ? new OpenSslException(message, stackError) : new OpenSslHandshakeException(message, stackError);
  }
  
  private static boolean shouldAddSuppressed(Throwable target, int errorCode) {
    for (Throwable suppressed : ThrowableUtil.getSuppressed(target)) {
      if (suppressed instanceof NativeSslException && ((NativeSslException)suppressed)
        .errorCode() == errorCode)
        return false; 
    } 
    return true;
  }
  
  private SSLEngineResult sslReadErrorResult(int error, int stackError, int bytesConsumed, int bytesProduced) throws SSLException {
    if (needWrapAgain(stackError))
      return new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, bytesConsumed, bytesProduced); 
    throw shutdownWithError("SSL_read", error, stackError);
  }
  
  private void closeAll() throws SSLException {
    this.receivedShutdown = true;
    closeOutbound();
    closeInbound();
  }
  
  private void rejectRemoteInitiatedRenegotiation() throws SSLHandshakeException {
    if (!isDestroyed() && ((!this.clientMode && SSL.getHandshakeCount(this.ssl) > 1) || (this.clientMode && 
      
      SSL.getHandshakeCount(this.ssl) > 2)) && 
      
      !"TLSv1.3".equals(this.session.getProtocol()) && this.handshakeState == HandshakeState.FINISHED) {
      shutdown();
      throw new SSLHandshakeException("remote-initiated renegotiation not allowed");
    } 
  }
  
  public final SSLEngineResult unwrap(ByteBuffer[] srcs, ByteBuffer[] dsts) throws SSLException {
    return unwrap(srcs, 0, srcs.length, dsts, 0, dsts.length);
  }
  
  private ByteBuffer[] singleSrcBuffer(ByteBuffer src) {
    this.singleSrcBuffer[0] = src;
    return this.singleSrcBuffer;
  }
  
  private void resetSingleSrcBuffer() {
    this.singleSrcBuffer[0] = null;
  }
  
  private ByteBuffer[] singleDstBuffer(ByteBuffer src) {
    this.singleDstBuffer[0] = src;
    return this.singleDstBuffer;
  }
  
  private void resetSingleDstBuffer() {
    this.singleDstBuffer[0] = null;
  }
  
  public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
    try {
      return unwrap(singleSrcBuffer(src), 0, 1, dsts, offset, length);
    } finally {
      resetSingleSrcBuffer();
    } 
  }
  
  public final synchronized SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
    try {
      return wrap(singleSrcBuffer(src), dst);
    } finally {
      resetSingleSrcBuffer();
    } 
  }
  
  public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
    try {
      return unwrap(singleSrcBuffer(src), singleDstBuffer(dst));
    } finally {
      resetSingleSrcBuffer();
      resetSingleDstBuffer();
    } 
  }
  
  public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
    try {
      return unwrap(singleSrcBuffer(src), dsts);
    } finally {
      resetSingleSrcBuffer();
    } 
  }
  
  private class TaskDecorator<R extends Runnable> implements Runnable {
    protected final R task;
    
    TaskDecorator(R task) {
      this.task = task;
    }
    
    public void run() {
      ReferenceCountedOpenSslEngine.this.runAndResetNeedTask((Runnable)this.task);
    }
  }
  
  private final class AsyncTaskDecorator extends TaskDecorator<AsyncTask> implements AsyncRunnable {
    AsyncTaskDecorator(AsyncTask task) {
      super(task);
    }
    
    public void run(Runnable runnable) {
      if (ReferenceCountedOpenSslEngine.this.isDestroyed())
        return; 
      this.task.runAsync(new ReferenceCountedOpenSslEngine.TaskDecorator<Runnable>(runnable));
    }
  }
  
  private synchronized void runAndResetNeedTask(Runnable task) {
    try {
      if (isDestroyed())
        return; 
      task.run();
    } finally {
      this.needTask = false;
    } 
  }
  
  public final synchronized Runnable getDelegatedTask() {
    if (isDestroyed())
      return null; 
    Runnable task = SSL.getTask(this.ssl);
    if (task == null)
      return null; 
    if (task instanceof AsyncTask)
      return new AsyncTaskDecorator((AsyncTask)task); 
    return new TaskDecorator<Runnable>(task);
  }
  
  public final synchronized void closeInbound() throws SSLException {
    if (this.isInboundDone)
      return; 
    this.isInboundDone = true;
    if (isOutboundDone())
      shutdown(); 
    if (this.handshakeState != HandshakeState.NOT_STARTED && !this.receivedShutdown)
      throw new SSLException("Inbound closed before receiving peer's close_notify: possible truncation attack?"); 
  }
  
  public final synchronized boolean isInboundDone() {
    return this.isInboundDone;
  }
  
  public final synchronized void closeOutbound() {
    if (this.outboundClosed)
      return; 
    this.outboundClosed = true;
    if (this.handshakeState != HandshakeState.NOT_STARTED && !isDestroyed()) {
      int mode = SSL.getShutdown(this.ssl);
      if ((mode & SSL.SSL_SENT_SHUTDOWN) != SSL.SSL_SENT_SHUTDOWN)
        doSSLShutdown(); 
    } else {
      shutdown();
    } 
  }
  
  private boolean doSSLShutdown() {
    if (SSL.isInInit(this.ssl) != 0)
      return false; 
    int err = SSL.shutdownSSL(this.ssl);
    if (err < 0) {
      int sslErr = SSL.getError(this.ssl, err);
      if (sslErr == SSL.SSL_ERROR_SYSCALL || sslErr == SSL.SSL_ERROR_SSL) {
        if (logger.isDebugEnabled()) {
          int error = SSL.getLastErrorNumber();
          logger.debug("SSL_shutdown failed: OpenSSL error: {} {}", Integer.valueOf(error), SSL.getErrorString(error));
        } 
        shutdown();
        return false;
      } 
      SSL.clearError();
    } 
    return true;
  }
  
  public final synchronized boolean isOutboundDone() {
    return (this.outboundClosed && (this.networkBIO == 0L || SSL.bioLengthNonApplication(this.networkBIO) == 0));
  }
  
  public final String[] getSupportedCipherSuites() {
    return OpenSsl.AVAILABLE_CIPHER_SUITES.<String>toArray(new String[0]);
  }
  
  public final String[] getEnabledCipherSuites() {
    String[] extraCiphers, enabled;
    boolean tls13Enabled;
    synchronized (this) {
      if (!isDestroyed()) {
        enabled = SSL.getCiphers(this.ssl);
        int opts = SSL.getOptions(this.ssl);
        if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_3, "TLSv1.3")) {
          extraCiphers = OpenSsl.EXTRA_SUPPORTED_TLS_1_3_CIPHERS;
          tls13Enabled = true;
        } else {
          extraCiphers = EmptyArrays.EMPTY_STRINGS;
          tls13Enabled = false;
        } 
      } else {
        return EmptyArrays.EMPTY_STRINGS;
      } 
    } 
    if (enabled == null)
      return EmptyArrays.EMPTY_STRINGS; 
    Set<String> enabledSet = new LinkedHashSet<String>(enabled.length + extraCiphers.length);
    synchronized (this) {
      for (int i = 0; i < enabled.length; i++) {
        String mapped = toJavaCipherSuite(enabled[i]);
        String cipher = (mapped == null) ? enabled[i] : mapped;
        if ((tls13Enabled && OpenSsl.isTlsv13Supported()) || !SslUtils.isTLSv13Cipher(cipher))
          enabledSet.add(cipher); 
      } 
      Collections.addAll(enabledSet, extraCiphers);
    } 
    return enabledSet.<String>toArray(new String[0]);
  }
  
  public final void setEnabledCipherSuites(String[] cipherSuites) {
    ObjectUtil.checkNotNull(cipherSuites, "cipherSuites");
    StringBuilder buf = new StringBuilder();
    StringBuilder bufTLSv13 = new StringBuilder();
    CipherSuiteConverter.convertToCipherStrings(Arrays.asList(cipherSuites), buf, bufTLSv13, OpenSsl.isBoringSSL());
    String cipherSuiteSpec = buf.toString();
    String cipherSuiteSpecTLSv13 = bufTLSv13.toString();
    if (!OpenSsl.isTlsv13Supported() && !cipherSuiteSpecTLSv13.isEmpty())
      throw new IllegalArgumentException("TLSv1.3 is not supported by this java version."); 
    synchronized (this) {
      if (!isDestroyed()) {
        try {
          SSL.setCipherSuites(this.ssl, cipherSuiteSpec, false);
          if (OpenSsl.isTlsv13Supported())
            SSL.setCipherSuites(this.ssl, OpenSsl.checkTls13Ciphers(logger, cipherSuiteSpecTLSv13), true); 
          Set<String> protocols = new HashSet<String>(this.explicitlyEnabledProtocols.length);
          Collections.addAll(protocols, this.explicitlyEnabledProtocols);
          if (cipherSuiteSpec.isEmpty()) {
            protocols.remove("TLSv1");
            protocols.remove("TLSv1.1");
            protocols.remove("TLSv1.2");
            protocols.remove("SSLv3");
            protocols.remove("SSLv2");
            protocols.remove("SSLv2Hello");
          } 
          if (cipherSuiteSpecTLSv13.isEmpty())
            protocols.remove("TLSv1.3"); 
          setEnabledProtocols0(protocols.<String>toArray(EmptyArrays.EMPTY_STRINGS), false);
        } catch (Exception e) {
          throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec, e);
        } 
      } else {
        throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec);
      } 
    } 
  }
  
  public final String[] getSupportedProtocols() {
    return OpenSsl.SUPPORTED_PROTOCOLS_SET.<String>toArray(new String[0]);
  }
  
  public final String[] getEnabledProtocols() {
    int opts;
    List<String> enabled = new ArrayList<String>(6);
    enabled.add("SSLv2Hello");
    synchronized (this) {
      if (!isDestroyed()) {
        opts = SSL.getOptions(this.ssl);
      } else {
        return enabled.<String>toArray(new String[0]);
      } 
    } 
    if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1, "TLSv1"))
      enabled.add("TLSv1"); 
    if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_1, "TLSv1.1"))
      enabled.add("TLSv1.1"); 
    if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_2, "TLSv1.2"))
      enabled.add("TLSv1.2"); 
    if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_3, "TLSv1.3"))
      enabled.add("TLSv1.3"); 
    if (isProtocolEnabled(opts, SSL.SSL_OP_NO_SSLv2, "SSLv2"))
      enabled.add("SSLv2"); 
    if (isProtocolEnabled(opts, SSL.SSL_OP_NO_SSLv3, "SSLv3"))
      enabled.add("SSLv3"); 
    return enabled.<String>toArray(new String[0]);
  }
  
  private static boolean isProtocolEnabled(int opts, int disableMask, String protocolString) {
    return ((opts & disableMask) == 0 && OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(protocolString));
  }
  
  public final void setEnabledProtocols(String[] protocols) {
    setEnabledProtocols0(protocols, true);
  }
  
  private void setEnabledProtocols0(String[] protocols, boolean cache) {
    ObjectUtil.checkNotNullWithIAE(protocols, "protocols");
    int minProtocolIndex = OPENSSL_OP_NO_PROTOCOLS.length;
    int maxProtocolIndex = 0;
    for (String p : protocols) {
      if (!OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(p))
        throw new IllegalArgumentException("Protocol " + p + " is not supported."); 
      if (p.equals("SSLv2")) {
        if (minProtocolIndex > 0)
          minProtocolIndex = 0; 
        if (maxProtocolIndex < 0)
          maxProtocolIndex = 0; 
      } else if (p.equals("SSLv3")) {
        if (minProtocolIndex > 1)
          minProtocolIndex = 1; 
        if (maxProtocolIndex < 1)
          maxProtocolIndex = 1; 
      } else if (p.equals("TLSv1")) {
        if (minProtocolIndex > 2)
          minProtocolIndex = 2; 
        if (maxProtocolIndex < 2)
          maxProtocolIndex = 2; 
      } else if (p.equals("TLSv1.1")) {
        if (minProtocolIndex > 3)
          minProtocolIndex = 3; 
        if (maxProtocolIndex < 3)
          maxProtocolIndex = 3; 
      } else if (p.equals("TLSv1.2")) {
        if (minProtocolIndex > 4)
          minProtocolIndex = 4; 
        if (maxProtocolIndex < 4)
          maxProtocolIndex = 4; 
      } else if (p.equals("TLSv1.3")) {
        if (minProtocolIndex > 5)
          minProtocolIndex = 5; 
        if (maxProtocolIndex < 5)
          maxProtocolIndex = 5; 
      } 
    } 
    synchronized (this) {
      if (cache)
        this.explicitlyEnabledProtocols = protocols; 
      if (!isDestroyed()) {
        SSL.clearOptions(this.ssl, SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2 | SSL.SSL_OP_NO_TLSv1_3);
        int opts = 0;
        int i;
        for (i = 0; i < minProtocolIndex; i++)
          opts |= OPENSSL_OP_NO_PROTOCOLS[i]; 
        assert maxProtocolIndex != Integer.MAX_VALUE;
        for (i = maxProtocolIndex + 1; i < OPENSSL_OP_NO_PROTOCOLS.length; i++)
          opts |= OPENSSL_OP_NO_PROTOCOLS[i]; 
        SSL.setOptions(this.ssl, opts);
      } else {
        throw new IllegalStateException("failed to enable protocols: " + Arrays.asList(protocols));
      } 
    } 
  }
  
  public final SSLSession getSession() {
    return this.session;
  }
  
  public final synchronized void beginHandshake() throws SSLException {
    switch (this.handshakeState) {
      case NPN:
        checkEngineClosed();
        this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
        calculateMaxWrapOverhead();
      case NPN_AND_ALPN:
        return;
      case ALPN:
        throw new SSLException("renegotiation unsupported");
      case NONE:
        this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
        if (handshake() == SSLEngineResult.HandshakeStatus.NEED_TASK)
          this.needTask = true; 
        calculateMaxWrapOverhead();
    } 
    throw new Error();
  }
  
  private void checkEngineClosed() throws SSLException {
    if (isDestroyed())
      throw new SSLException("engine closed"); 
  }
  
  private static SSLEngineResult.HandshakeStatus pendingStatus(int pendingStatus) {
    return (pendingStatus > 0) ? SSLEngineResult.HandshakeStatus.NEED_WRAP : SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
  }
  
  private static boolean isEmpty(Object[] arr) {
    return (arr == null || arr.length == 0);
  }
  
  private static boolean isEmpty(byte[] cert) {
    return (cert == null || cert.length == 0);
  }
  
  private SSLEngineResult.HandshakeStatus handshakeException() throws SSLException {
    if (SSL.bioLengthNonApplication(this.networkBIO) > 0)
      return SSLEngineResult.HandshakeStatus.NEED_WRAP; 
    Throwable exception = this.pendingException;
    assert exception != null;
    this.pendingException = null;
    shutdown();
    if (exception instanceof SSLHandshakeException)
      throw (SSLHandshakeException)exception; 
    SSLHandshakeException e = new SSLHandshakeException("General OpenSslEngine problem");
    e.initCause(exception);
    throw e;
  }
  
  final void initHandshakeException(Throwable cause) {
    if (this.pendingException == null) {
      this.pendingException = cause;
    } else {
      ThrowableUtil.addSuppressed(this.pendingException, cause);
    } 
  }
  
  private SSLEngineResult.HandshakeStatus handshake() throws SSLException {
    if (this.needTask)
      return SSLEngineResult.HandshakeStatus.NEED_TASK; 
    if (this.handshakeState == HandshakeState.FINISHED)
      return SSLEngineResult.HandshakeStatus.FINISHED; 
    checkEngineClosed();
    if (this.pendingException != null) {
      if (SSL.doHandshake(this.ssl) <= 0)
        SSL.clearError(); 
      return handshakeException();
    } 
    this.engineMap.add(this);
    if (!this.sessionSet) {
      this.parentContext.sessionContext().setSessionFromCache(getPeerHost(), getPeerPort(), this.ssl);
      this.sessionSet = true;
    } 
    if (this.lastAccessed == -1L)
      this.lastAccessed = System.currentTimeMillis(); 
    int code = SSL.doHandshake(this.ssl);
    if (code <= 0) {
      int sslError = SSL.getError(this.ssl, code);
      if (sslError == SSL.SSL_ERROR_WANT_READ || sslError == SSL.SSL_ERROR_WANT_WRITE)
        return pendingStatus(SSL.bioLengthNonApplication(this.networkBIO)); 
      if (sslError == SSL.SSL_ERROR_WANT_X509_LOOKUP || sslError == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY || sslError == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION)
        return SSLEngineResult.HandshakeStatus.NEED_TASK; 
      if (needWrapAgain(SSL.getLastErrorNumber()))
        return SSLEngineResult.HandshakeStatus.NEED_WRAP; 
      if (this.pendingException != null)
        return handshakeException(); 
      throw shutdownWithError("SSL_do_handshake", sslError);
    } 
    if (SSL.bioLengthNonApplication(this.networkBIO) > 0)
      return SSLEngineResult.HandshakeStatus.NEED_WRAP; 
    this.session.handshakeFinished(SSL.getSessionId(this.ssl), SSL.getCipherForSSL(this.ssl), SSL.getVersion(this.ssl), 
        SSL.getPeerCertificate(this.ssl), SSL.getPeerCertChain(this.ssl), 
        SSL.getTime(this.ssl) * 1000L, this.parentContext.sessionTimeout() * 1000L);
    selectApplicationProtocol();
    return SSLEngineResult.HandshakeStatus.FINISHED;
  }
  
  private SSLEngineResult.HandshakeStatus mayFinishHandshake(SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) throws SSLException {
    return ((hs == SSLEngineResult.HandshakeStatus.NEED_UNWRAP && bytesProduced > 0) || (hs == SSLEngineResult.HandshakeStatus.NEED_WRAP && bytesConsumed > 0)) ? 
      handshake() : mayFinishHandshake((hs != SSLEngineResult.HandshakeStatus.FINISHED) ? getHandshakeStatus() : SSLEngineResult.HandshakeStatus.FINISHED);
  }
  
  private SSLEngineResult.HandshakeStatus mayFinishHandshake(SSLEngineResult.HandshakeStatus status) throws SSLException {
    if (status == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
      if (this.handshakeState != HandshakeState.FINISHED)
        return handshake(); 
      if (!isDestroyed() && SSL.bioLengthNonApplication(this.networkBIO) > 0)
        return SSLEngineResult.HandshakeStatus.NEED_WRAP; 
    } 
    return status;
  }
  
  public final synchronized SSLEngineResult.HandshakeStatus getHandshakeStatus() {
    if (needPendingStatus()) {
      if (this.needTask)
        return SSLEngineResult.HandshakeStatus.NEED_TASK; 
      return pendingStatus(SSL.bioLengthNonApplication(this.networkBIO));
    } 
    return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
  }
  
  private SSLEngineResult.HandshakeStatus getHandshakeStatus(int pending) {
    if (needPendingStatus()) {
      if (this.needTask)
        return SSLEngineResult.HandshakeStatus.NEED_TASK; 
      return pendingStatus(pending);
    } 
    return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
  }
  
  private boolean needPendingStatus() {
    return (this.handshakeState != HandshakeState.NOT_STARTED && !isDestroyed() && (this.handshakeState != HandshakeState.FINISHED || 
      isInboundDone() || isOutboundDone()));
  }
  
  private String toJavaCipherSuite(String openSslCipherSuite) {
    if (openSslCipherSuite == null)
      return null; 
    String version = SSL.getVersion(this.ssl);
    String prefix = toJavaCipherSuitePrefix(version);
    return CipherSuiteConverter.toJava(openSslCipherSuite, prefix);
  }
  
  private static String toJavaCipherSuitePrefix(String protocolVersion) {
    char c;
    if (protocolVersion == null || protocolVersion.isEmpty()) {
      c = Character.MIN_VALUE;
    } else {
      c = protocolVersion.charAt(0);
    } 
    switch (c) {
      case 'T':
        return "TLS";
      case 'S':
        return "SSL";
    } 
    return "UNKNOWN";
  }
  
  public final void setUseClientMode(boolean clientMode) {
    if (clientMode != this.clientMode)
      throw new UnsupportedOperationException(); 
  }
  
  public final boolean getUseClientMode() {
    return this.clientMode;
  }
  
  public final void setNeedClientAuth(boolean b) {
    setClientAuth(b ? ClientAuth.REQUIRE : ClientAuth.NONE);
  }
  
  public final boolean getNeedClientAuth() {
    return (this.clientAuth == ClientAuth.REQUIRE);
  }
  
  public final void setWantClientAuth(boolean b) {
    setClientAuth(b ? ClientAuth.OPTIONAL : ClientAuth.NONE);
  }
  
  public final boolean getWantClientAuth() {
    return (this.clientAuth == ClientAuth.OPTIONAL);
  }
  
  public final synchronized void setVerify(int verifyMode, int depth) {
    if (!isDestroyed())
      SSL.setVerify(this.ssl, verifyMode, depth); 
  }
  
  private void setClientAuth(ClientAuth mode) {
    if (this.clientMode)
      return; 
    synchronized (this) {
      if (this.clientAuth == mode)
        return; 
      if (!isDestroyed())
        switch (mode) {
          case NONE:
            SSL.setVerify(this.ssl, 0, 10);
            break;
          case ALPN:
            SSL.setVerify(this.ssl, 2, 10);
            break;
          case NPN:
            SSL.setVerify(this.ssl, 1, 10);
            break;
          default:
            throw new Error(mode.toString());
        }  
      this.clientAuth = mode;
    } 
  }
  
  public final void setEnableSessionCreation(boolean b) {
    if (b)
      throw new UnsupportedOperationException(); 
  }
  
  public final boolean getEnableSessionCreation() {
    return false;
  }
  
  @SuppressJava6Requirement(reason = "Usage guarded by java version check")
  public final synchronized SSLParameters getSSLParameters() {
    SSLParameters sslParameters = super.getSSLParameters();
    int version = PlatformDependent.javaVersion();
    if (version >= 7) {
      sslParameters.setEndpointIdentificationAlgorithm(this.endPointIdentificationAlgorithm);
      Java7SslParametersUtils.setAlgorithmConstraints(sslParameters, this.algorithmConstraints);
      if (version >= 8) {
        if (this.sniHostNames != null)
          Java8SslUtils.setSniHostNames(sslParameters, this.sniHostNames); 
        if (!isDestroyed())
          Java8SslUtils.setUseCipherSuitesOrder(sslParameters, 
              ((SSL.getOptions(this.ssl) & SSL.SSL_OP_CIPHER_SERVER_PREFERENCE) != 0)); 
        Java8SslUtils.setSNIMatchers(sslParameters, this.matchers);
      } 
    } 
    return sslParameters;
  }
  
  @SuppressJava6Requirement(reason = "Usage guarded by java version check")
  public final synchronized void setSSLParameters(SSLParameters sslParameters) {
    int version = PlatformDependent.javaVersion();
    if (version >= 7) {
      if (sslParameters.getAlgorithmConstraints() != null)
        throw new IllegalArgumentException("AlgorithmConstraints are not supported."); 
      boolean isDestroyed = isDestroyed();
      if (version >= 8) {
        if (!isDestroyed) {
          if (this.clientMode) {
            List<String> sniHostNames = Java8SslUtils.getSniHostNames(sslParameters);
            for (String name : sniHostNames)
              SSL.setTlsExtHostName(this.ssl, name); 
            this.sniHostNames = sniHostNames;
          } 
          if (Java8SslUtils.getUseCipherSuitesOrder(sslParameters)) {
            SSL.setOptions(this.ssl, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
          } else {
            SSL.clearOptions(this.ssl, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
          } 
        } 
        this.matchers = sslParameters.getSNIMatchers();
      } 
      String endPointIdentificationAlgorithm = sslParameters.getEndpointIdentificationAlgorithm();
      if (!isDestroyed)
        if (this.clientMode && isEndPointVerificationEnabled(endPointIdentificationAlgorithm))
          SSL.setVerify(this.ssl, 2, -1);  
      this.endPointIdentificationAlgorithm = endPointIdentificationAlgorithm;
      this.algorithmConstraints = sslParameters.getAlgorithmConstraints();
    } 
    super.setSSLParameters(sslParameters);
  }
  
  private static boolean isEndPointVerificationEnabled(String endPointIdentificationAlgorithm) {
    return (endPointIdentificationAlgorithm != null && !endPointIdentificationAlgorithm.isEmpty());
  }
  
  private boolean isDestroyed() {
    return this.destroyed;
  }
  
  final boolean checkSniHostnameMatch(byte[] hostname) {
    return Java8SslUtils.checkSniHostnameMatch(this.matchers, hostname);
  }
  
  public String getNegotiatedApplicationProtocol() {
    return this.applicationProtocol;
  }
  
  private static long bufferAddress(ByteBuffer b) {
    assert b.isDirect();
    if (PlatformDependent.hasUnsafe())
      return PlatformDependent.directBufferAddress(b); 
    return Buffer.address(b);
  }
  
  private void selectApplicationProtocol() throws SSLException {
    String applicationProtocol;
    ApplicationProtocolConfig.SelectedListenerFailureBehavior behavior = this.apn.selectedListenerFailureBehavior();
    List<String> protocols = this.apn.protocols();
    switch (this.apn.protocol()) {
      case NONE:
        return;
      case ALPN:
        applicationProtocol = SSL.getAlpnSelected(this.ssl);
        if (applicationProtocol != null)
          this.applicationProtocol = selectApplicationProtocol(protocols, behavior, applicationProtocol); 
      case NPN:
        applicationProtocol = SSL.getNextProtoNegotiated(this.ssl);
        if (applicationProtocol != null)
          this.applicationProtocol = selectApplicationProtocol(protocols, behavior, applicationProtocol); 
      case NPN_AND_ALPN:
        applicationProtocol = SSL.getAlpnSelected(this.ssl);
        if (applicationProtocol == null)
          applicationProtocol = SSL.getNextProtoNegotiated(this.ssl); 
        if (applicationProtocol != null)
          this.applicationProtocol = selectApplicationProtocol(protocols, behavior, applicationProtocol); 
    } 
    throw new Error();
  }
  
  private String selectApplicationProtocol(List<String> protocols, ApplicationProtocolConfig.SelectedListenerFailureBehavior behavior, String applicationProtocol) throws SSLException {
    if (behavior == ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT)
      return applicationProtocol; 
    int size = protocols.size();
    assert size > 0;
    if (protocols.contains(applicationProtocol))
      return applicationProtocol; 
    if (behavior == ApplicationProtocolConfig.SelectedListenerFailureBehavior.CHOOSE_MY_LAST_PROTOCOL)
      return protocols.get(size - 1); 
    throw new SSLException("unknown protocol " + applicationProtocol);
  }
  
  final void setSessionId(OpenSslSessionId id) {
    this.session.setSessionId(id);
  }
  
  private final class DefaultOpenSslSession implements OpenSslSession {
    private final OpenSslSessionContext sessionContext;
    
    private X509Certificate[] x509PeerCerts;
    
    private Certificate[] peerCerts;
    
    private boolean valid = true;
    
    private String protocol;
    
    private String cipher;
    
    private OpenSslSessionId id = OpenSslSessionId.NULL_ID;
    
    private volatile long creationTime;
    
    private volatile int applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
    
    private volatile Certificate[] localCertificateChain;
    
    private Map<String, Object> values;
    
    DefaultOpenSslSession(OpenSslSessionContext sessionContext) {
      this.sessionContext = sessionContext;
    }
    
    private SSLSessionBindingEvent newSSLSessionBindingEvent(String name) {
      return new SSLSessionBindingEvent(ReferenceCountedOpenSslEngine.this.session, name);
    }
    
    public void setSessionId(OpenSslSessionId sessionId) {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (this.id == OpenSslSessionId.NULL_ID) {
          this.id = sessionId;
          this.creationTime = System.currentTimeMillis();
        } 
      } 
    }
    
    public OpenSslSessionId sessionId() {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (this.id == OpenSslSessionId.NULL_ID && !ReferenceCountedOpenSslEngine.this.isDestroyed()) {
          byte[] sessionId = SSL.getSessionId(ReferenceCountedOpenSslEngine.this.ssl);
          if (sessionId != null)
            this.id = new OpenSslSessionId(sessionId); 
        } 
        return this.id;
      } 
    }
    
    public void setLocalCertificate(Certificate[] localCertificate) {
      this.localCertificateChain = localCertificate;
    }
    
    public byte[] getId() {
      return sessionId().cloneBytes();
    }
    
    public OpenSslSessionContext getSessionContext() {
      return this.sessionContext;
    }
    
    public long getCreationTime() {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        return this.creationTime;
      } 
    }
    
    public long getLastAccessedTime() {
      long lastAccessed = ReferenceCountedOpenSslEngine.this.lastAccessed;
      return (lastAccessed == -1L) ? getCreationTime() : lastAccessed;
    }
    
    public void invalidate() {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        this.valid = false;
        this.sessionContext.removeFromCache(this.id);
      } 
    }
    
    public boolean isValid() {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        return (this.valid || this.sessionContext.isInCache(this.id));
      } 
    }
    
    public void putValue(String name, Object value) {
      Object old;
      ObjectUtil.checkNotNull(name, "name");
      ObjectUtil.checkNotNull(value, "value");
      synchronized (this) {
        Map<String, Object> values = this.values;
        if (values == null)
          values = this.values = new HashMap<String, Object>(2); 
        old = values.put(name, value);
      } 
      if (value instanceof SSLSessionBindingListener)
        ((SSLSessionBindingListener)value).valueBound(newSSLSessionBindingEvent(name)); 
      notifyUnbound(old, name);
    }
    
    public Object getValue(String name) {
      ObjectUtil.checkNotNull(name, "name");
      synchronized (this) {
        if (this.values == null)
          return null; 
        return this.values.get(name);
      } 
    }
    
    public void removeValue(String name) {
      Object old;
      ObjectUtil.checkNotNull(name, "name");
      synchronized (this) {
        Map<String, Object> values = this.values;
        if (values == null)
          return; 
        old = values.remove(name);
      } 
      notifyUnbound(old, name);
    }
    
    public String[] getValueNames() {
      synchronized (this) {
        Map<String, Object> values = this.values;
        if (values == null || values.isEmpty())
          return EmptyArrays.EMPTY_STRINGS; 
        return (String[])values.keySet().toArray((Object[])new String[0]);
      } 
    }
    
    private void notifyUnbound(Object value, String name) {
      if (value instanceof SSLSessionBindingListener)
        ((SSLSessionBindingListener)value).valueUnbound(newSSLSessionBindingEvent(name)); 
    }
    
    public void handshakeFinished(byte[] id, String cipher, String protocol, byte[] peerCertificate, byte[][] peerCertificateChain, long creationTime, long timeout) throws SSLException {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
          this.creationTime = creationTime;
          if (this.id == OpenSslSessionId.NULL_ID)
            this.id = (id == null) ? OpenSslSessionId.NULL_ID : new OpenSslSessionId(id); 
          this.cipher = ReferenceCountedOpenSslEngine.this.toJavaCipherSuite(cipher);
          this.protocol = protocol;
          if (ReferenceCountedOpenSslEngine.this.clientMode) {
            if (ReferenceCountedOpenSslEngine.isEmpty((Object[])peerCertificateChain)) {
              this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
              this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
            } else {
              this.peerCerts = new Certificate[peerCertificateChain.length];
              this.x509PeerCerts = new X509Certificate[peerCertificateChain.length];
              initCerts(peerCertificateChain, 0);
            } 
          } else if (ReferenceCountedOpenSslEngine.isEmpty(peerCertificate)) {
            this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
            this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
          } else if (ReferenceCountedOpenSslEngine.isEmpty((Object[])peerCertificateChain)) {
            this.peerCerts = new Certificate[] { (Certificate)new LazyX509Certificate(peerCertificate) };
            this.x509PeerCerts = new X509Certificate[] { (X509Certificate)new LazyJavaxX509Certificate(peerCertificate) };
          } else {
            this.peerCerts = new Certificate[peerCertificateChain.length + 1];
            this.x509PeerCerts = new X509Certificate[peerCertificateChain.length + 1];
            this.peerCerts[0] = (Certificate)new LazyX509Certificate(peerCertificate);
            this.x509PeerCerts[0] = (X509Certificate)new LazyJavaxX509Certificate(peerCertificate);
            initCerts(peerCertificateChain, 1);
          } 
          ReferenceCountedOpenSslEngine.this.calculateMaxWrapOverhead();
          ReferenceCountedOpenSslEngine.this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.FINISHED;
        } else {
          throw new SSLException("Already closed");
        } 
      } 
    }
    
    private void initCerts(byte[][] chain, int startPos) {
      for (int i = 0; i < chain.length; i++) {
        int certPos = startPos + i;
        this.peerCerts[certPos] = (Certificate)new LazyX509Certificate(chain[i]);
        this.x509PeerCerts[certPos] = (X509Certificate)new LazyJavaxX509Certificate(chain[i]);
      } 
    }
    
    public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (ReferenceCountedOpenSslEngine.isEmpty((Object[])this.peerCerts))
          throw new SSLPeerUnverifiedException("peer not verified"); 
        return (Certificate[])this.peerCerts.clone();
      } 
    }
    
    public Certificate[] getLocalCertificates() {
      Certificate[] localCerts = this.localCertificateChain;
      if (localCerts == null)
        return null; 
      return (Certificate[])localCerts.clone();
    }
    
    public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (ReferenceCountedOpenSslEngine.isEmpty((Object[])this.x509PeerCerts))
          throw new SSLPeerUnverifiedException("peer not verified"); 
        return (X509Certificate[])this.x509PeerCerts.clone();
      } 
    }
    
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
      Certificate[] peer = getPeerCertificates();
      return ((X509Certificate)peer[0]).getSubjectX500Principal();
    }
    
    public Principal getLocalPrincipal() {
      Certificate[] local = this.localCertificateChain;
      if (local == null || local.length == 0)
        return null; 
      return ((X509Certificate)local[0]).getSubjectX500Principal();
    }
    
    public String getCipherSuite() {
      synchronized (ReferenceCountedOpenSslEngine.this) {
        if (this.cipher == null)
          return "SSL_NULL_WITH_NULL_NULL"; 
        return this.cipher;
      } 
    }
    
    public String getProtocol() {
      String protocol = this.protocol;
      if (protocol == null)
        synchronized (ReferenceCountedOpenSslEngine.this) {
          if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
            protocol = SSL.getVersion(ReferenceCountedOpenSslEngine.this.ssl);
          } else {
            protocol = "";
          } 
        }  
      return protocol;
    }
    
    public String getPeerHost() {
      return ReferenceCountedOpenSslEngine.this.getPeerHost();
    }
    
    public int getPeerPort() {
      return ReferenceCountedOpenSslEngine.this.getPeerPort();
    }
    
    public int getPacketBufferSize() {
      return SSL.SSL_MAX_ENCRYPTED_LENGTH;
    }
    
    public int getApplicationBufferSize() {
      return this.applicationBufferSize;
    }
    
    public void tryExpandApplicationBufferSize(int packetLengthDataOnly) {
      if (packetLengthDataOnly > ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH && this.applicationBufferSize != ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE)
        this.applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE; 
    }
    
    public String toString() {
      return "DefaultOpenSslSession{sessionContext=" + this.sessionContext + ", id=" + this.id + '}';
    }
  }
  
  private static final class OpenSslException extends SSLException implements NativeSslException {
    private final int errorCode;
    
    OpenSslException(String reason, int errorCode) {
      super(reason);
      this.errorCode = errorCode;
    }
    
    public int errorCode() {
      return this.errorCode;
    }
  }
  
  private static final class OpenSslHandshakeException extends SSLHandshakeException implements NativeSslException {
    private final int errorCode;
    
    OpenSslHandshakeException(String reason, int errorCode) {
      super(reason);
      this.errorCode = errorCode;
    }
    
    public int errorCode() {
      return this.errorCode;
    }
  }
  
  private static interface NativeSslException {
    int errorCode();
  }
}
