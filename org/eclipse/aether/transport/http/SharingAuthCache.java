package org.eclipse.aether.transport.http;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.client.AuthCache;

final class SharingAuthCache implements AuthCache {
  private final LocalState state;
  
  private final Map<HttpHost, AuthScheme> authSchemes;
  
  SharingAuthCache(LocalState state) {
    this.state = state;
    this.authSchemes = new HashMap<>();
  }
  
  private static HttpHost toKey(HttpHost host) {
    if (host.getPort() <= 0) {
      int port = host.getSchemeName().equalsIgnoreCase("https") ? 443 : 80;
      return new HttpHost(host.getHostName(), port, host.getSchemeName());
    } 
    return host;
  }
  
  public AuthScheme get(HttpHost host) {
    host = toKey(host);
    AuthScheme authScheme = this.authSchemes.get(host);
    if (authScheme == null) {
      authScheme = this.state.getAuthScheme(host);
      this.authSchemes.put(host, authScheme);
    } 
    return authScheme;
  }
  
  public void put(HttpHost host, AuthScheme authScheme) {
    if (authScheme != null) {
      this.authSchemes.put(toKey(host), authScheme);
    } else {
      remove(host);
    } 
  }
  
  public void remove(HttpHost host) {
    this.authSchemes.remove(toKey(host));
  }
  
  public void clear() {
    share();
    this.authSchemes.clear();
  }
  
  private void share() {
    for (Map.Entry<HttpHost, AuthScheme> entry : this.authSchemes.entrySet())
      this.state.setAuthScheme(entry.getKey(), entry.getValue()); 
  }
  
  public String toString() {
    return this.authSchemes.toString();
  }
}
