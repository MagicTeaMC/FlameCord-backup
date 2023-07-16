package io.netty.handler.ssl;

import javax.net.ssl.SSLEngine;

public interface OpenSslCertificateCompressionAlgorithm {
  byte[] compress(SSLEngine paramSSLEngine, byte[] paramArrayOfbyte) throws Exception;
  
  byte[] decompress(SSLEngine paramSSLEngine, int paramInt, byte[] paramArrayOfbyte) throws Exception;
  
  int algorithmId();
}
