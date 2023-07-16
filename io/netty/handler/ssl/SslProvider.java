package io.netty.handler.ssl;

import java.security.Provider;

public enum SslProvider {
  JDK, OPENSSL, OPENSSL_REFCNT;
  
  public static boolean isAlpnSupported(SslProvider provider) {
    switch (provider) {
      case JDK:
        return JdkAlpnApplicationProtocolNegotiator.isAlpnSupported();
      case OPENSSL:
      case OPENSSL_REFCNT:
        return OpenSsl.isAlpnSupported();
    } 
    throw new Error("Unknown SslProvider: " + provider);
  }
  
  public static boolean isTlsv13Supported(SslProvider sslProvider) {
    return isTlsv13Supported(sslProvider, (Provider)null);
  }
  
  public static boolean isTlsv13Supported(SslProvider sslProvider, Provider provider) {
    switch (sslProvider) {
      case JDK:
        return SslUtils.isTLSv13SupportedByJDK(provider);
      case OPENSSL:
      case OPENSSL_REFCNT:
        return OpenSsl.isTlsv13Supported();
    } 
    throw new Error("Unknown SslProvider: " + sslProvider);
  }
  
  public static boolean isOptionSupported(SslProvider sslProvider, SslContextOption<?> option) {
    switch (sslProvider) {
      case JDK:
        return false;
      case OPENSSL:
      case OPENSSL_REFCNT:
        return OpenSsl.isOptionSupported(option);
    } 
    throw new Error("Unknown SslProvider: " + sslProvider);
  }
  
  static boolean isTlsv13EnabledByDefault(SslProvider sslProvider, Provider provider) {
    switch (sslProvider) {
      case JDK:
        return SslUtils.isTLSv13EnabledByJDK(provider);
      case OPENSSL:
      case OPENSSL_REFCNT:
        return OpenSsl.isTlsv13Supported();
    } 
    throw new Error("Unknown SslProvider: " + sslProvider);
  }
}
