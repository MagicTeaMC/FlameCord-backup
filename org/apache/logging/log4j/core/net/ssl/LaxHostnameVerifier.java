package org.apache.logging.log4j.core.net.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public final class LaxHostnameVerifier implements HostnameVerifier {
  public static final HostnameVerifier INSTANCE = new LaxHostnameVerifier();
  
  public boolean verify(String s, SSLSession sslSession) {
    return true;
  }
}
