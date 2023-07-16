package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.SuppressJava6Requirement;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.X509Certificate;

@SuppressJava6Requirement(reason = "Usage guarded by java version check")
abstract class ExtendedOpenSslSession extends ExtendedSSLSession implements OpenSslSession {
  private static final String[] LOCAL_SUPPORTED_SIGNATURE_ALGORITHMS = new String[] { 
      "SHA512withRSA", "SHA512withECDSA", "SHA384withRSA", "SHA384withECDSA", "SHA256withRSA", "SHA256withECDSA", "SHA224withRSA", "SHA224withECDSA", "SHA1withRSA", "SHA1withECDSA", 
      "RSASSA-PSS" };
  
  private final OpenSslSession wrapped;
  
  ExtendedOpenSslSession(OpenSslSession wrapped) {
    this.wrapped = wrapped;
  }
  
  public List<byte[]> getStatusResponses() {
    return (List)Collections.emptyList();
  }
  
  public OpenSslSessionId sessionId() {
    return this.wrapped.sessionId();
  }
  
  public void setSessionId(OpenSslSessionId id) {
    this.wrapped.setSessionId(id);
  }
  
  public final void setLocalCertificate(Certificate[] localCertificate) {
    this.wrapped.setLocalCertificate(localCertificate);
  }
  
  public String[] getPeerSupportedSignatureAlgorithms() {
    return EmptyArrays.EMPTY_STRINGS;
  }
  
  public final void tryExpandApplicationBufferSize(int packetLengthDataOnly) {
    this.wrapped.tryExpandApplicationBufferSize(packetLengthDataOnly);
  }
  
  public final String[] getLocalSupportedSignatureAlgorithms() {
    return (String[])LOCAL_SUPPORTED_SIGNATURE_ALGORITHMS.clone();
  }
  
  public final byte[] getId() {
    return this.wrapped.getId();
  }
  
  public final OpenSslSessionContext getSessionContext() {
    return this.wrapped.getSessionContext();
  }
  
  public final long getCreationTime() {
    return this.wrapped.getCreationTime();
  }
  
  public final long getLastAccessedTime() {
    return this.wrapped.getLastAccessedTime();
  }
  
  public final void invalidate() {
    this.wrapped.invalidate();
  }
  
  public final boolean isValid() {
    return this.wrapped.isValid();
  }
  
  public final void putValue(String name, Object value) {
    if (value instanceof SSLSessionBindingListener)
      value = new SSLSessionBindingListenerDecorator((SSLSessionBindingListener)value); 
    this.wrapped.putValue(name, value);
  }
  
  public final Object getValue(String s) {
    Object value = this.wrapped.getValue(s);
    if (value instanceof SSLSessionBindingListenerDecorator)
      return ((SSLSessionBindingListenerDecorator)value).delegate; 
    return value;
  }
  
  public final void removeValue(String s) {
    this.wrapped.removeValue(s);
  }
  
  public final String[] getValueNames() {
    return this.wrapped.getValueNames();
  }
  
  public final Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
    return this.wrapped.getPeerCertificates();
  }
  
  public final Certificate[] getLocalCertificates() {
    return this.wrapped.getLocalCertificates();
  }
  
  public final X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
    return this.wrapped.getPeerCertificateChain();
  }
  
  public final Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
    return this.wrapped.getPeerPrincipal();
  }
  
  public final Principal getLocalPrincipal() {
    return this.wrapped.getLocalPrincipal();
  }
  
  public final String getCipherSuite() {
    return this.wrapped.getCipherSuite();
  }
  
  public String getProtocol() {
    return this.wrapped.getProtocol();
  }
  
  public final String getPeerHost() {
    return this.wrapped.getPeerHost();
  }
  
  public final int getPeerPort() {
    return this.wrapped.getPeerPort();
  }
  
  public final int getPacketBufferSize() {
    return this.wrapped.getPacketBufferSize();
  }
  
  public final int getApplicationBufferSize() {
    return this.wrapped.getApplicationBufferSize();
  }
  
  private final class SSLSessionBindingListenerDecorator implements SSLSessionBindingListener {
    final SSLSessionBindingListener delegate;
    
    SSLSessionBindingListenerDecorator(SSLSessionBindingListener delegate) {
      this.delegate = delegate;
    }
    
    public void valueBound(SSLSessionBindingEvent event) {
      this.delegate.valueBound(new SSLSessionBindingEvent(ExtendedOpenSslSession.this, event.getName()));
    }
    
    public void valueUnbound(SSLSessionBindingEvent event) {
      this.delegate.valueUnbound(new SSLSessionBindingEvent(ExtendedOpenSslSession.this, event.getName()));
    }
  }
  
  public void handshakeFinished(byte[] id, String cipher, String protocol, byte[] peerCertificate, byte[][] peerCertificateChain, long creationTime, long timeout) throws SSLException {
    this.wrapped.handshakeFinished(id, cipher, protocol, peerCertificate, peerCertificateChain, creationTime, timeout);
  }
  
  public String toString() {
    return "ExtendedOpenSslSession{wrapped=" + this.wrapped + '}';
  }
  
  public abstract List getRequestedServerNames();
}
