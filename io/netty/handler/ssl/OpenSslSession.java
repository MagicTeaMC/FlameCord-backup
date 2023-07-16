package io.netty.handler.ssl;

import java.security.cert.Certificate;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

interface OpenSslSession extends SSLSession {
  OpenSslSessionId sessionId();
  
  void setLocalCertificate(Certificate[] paramArrayOfCertificate);
  
  void setSessionId(OpenSslSessionId paramOpenSslSessionId);
  
  OpenSslSessionContext getSessionContext();
  
  void tryExpandApplicationBufferSize(int paramInt);
  
  void handshakeFinished(byte[] paramArrayOfbyte1, String paramString1, String paramString2, byte[] paramArrayOfbyte2, byte[][] paramArrayOfbyte, long paramLong1, long paramLong2) throws SSLException;
}
