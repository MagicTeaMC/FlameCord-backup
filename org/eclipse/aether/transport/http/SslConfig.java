package org.eclipse.aether.transport.http;

import java.util.Arrays;
import java.util.Objects;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.util.ConfigUtils;

final class SslConfig {
  private static final String CIPHER_SUITES = "https.cipherSuites";
  
  private static final String PROTOCOLS = "https.protocols";
  
  final SSLContext context;
  
  final HostnameVerifier verifier;
  
  final String[] cipherSuites;
  
  final String[] protocols;
  
  SslConfig(RepositorySystemSession session, AuthenticationContext authContext) {
    this
      .context = (authContext != null) ? (SSLContext)authContext.get("ssl.context", SSLContext.class) : null;
    this
      .verifier = (authContext != null) ? (HostnameVerifier)authContext.get("ssl.hostnameVerifier", HostnameVerifier.class) : null;
    this.cipherSuites = split(get(session, "https.cipherSuites"));
    this.protocols = split(get(session, "https.protocols"));
  }
  
  private static String get(RepositorySystemSession session, String key) {
    String value = ConfigUtils.getString(session, null, new String[] { "aether.connector." + key, key });
    if (value == null)
      value = System.getProperty(key); 
    return value;
  }
  
  private static String[] split(String value) {
    if (value == null || value.isEmpty())
      return null; 
    return value.split(",+");
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    SslConfig that = (SslConfig)obj;
    return (Objects.equals(this.context, that.context) && 
      Objects.equals(this.verifier, that.verifier) && 
      Arrays.equals((Object[])this.cipherSuites, (Object[])that.cipherSuites) && 
      Arrays.equals((Object[])this.protocols, (Object[])that.protocols));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + hash(this.context);
    hash = hash * 31 + hash(this.verifier);
    hash = hash * 31 + Arrays.hashCode((Object[])this.cipherSuites);
    hash = hash * 31 + Arrays.hashCode((Object[])this.protocols);
    return hash;
  }
  
  private static int hash(Object obj) {
    return (obj != null) ? obj.hashCode() : 0;
  }
}
