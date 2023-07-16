package org.eclipse.aether.transport.http;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;

final class DemuxCredentialsProvider implements CredentialsProvider {
  private final CredentialsProvider serverCredentialsProvider;
  
  private final CredentialsProvider proxyCredentialsProvider;
  
  private final HttpHost proxy;
  
  DemuxCredentialsProvider(CredentialsProvider serverCredentialsProvider, CredentialsProvider proxyCredentialsProvider, HttpHost proxy) {
    this.serverCredentialsProvider = serverCredentialsProvider;
    this.proxyCredentialsProvider = proxyCredentialsProvider;
    this.proxy = proxy;
  }
  
  private CredentialsProvider getDelegate(AuthScope authScope) {
    if (this.proxy.getPort() == authScope.getPort() && this.proxy.getHostName().equalsIgnoreCase(authScope.getHost()))
      return this.proxyCredentialsProvider; 
    return this.serverCredentialsProvider;
  }
  
  public Credentials getCredentials(AuthScope authScope) {
    return getDelegate(authScope).getCredentials(authScope);
  }
  
  public void setCredentials(AuthScope authScope, Credentials credentials) {
    getDelegate(authScope).setCredentials(authScope, credentials);
  }
  
  public void clear() {
    this.serverCredentialsProvider.clear();
    this.proxyCredentialsProvider.clear();
  }
}
