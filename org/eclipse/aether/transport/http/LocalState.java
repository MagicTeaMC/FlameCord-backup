package org.eclipse.aether.transport.http;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.conn.HttpClientConnectionManager;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

final class LocalState implements Closeable {
  private final GlobalState global;
  
  private final HttpClientConnectionManager connMgr;
  
  private final GlobalState.CompoundKey userTokenKey;
  
  private volatile Object userToken;
  
  private final GlobalState.CompoundKey expectContinueKey;
  
  private volatile Boolean expectContinue;
  
  private volatile Boolean webDav;
  
  private final ConcurrentMap<HttpHost, AuthSchemePool> authSchemePools;
  
  LocalState(RepositorySystemSession session, RemoteRepository repo, SslConfig sslConfig) {
    this.global = GlobalState.get(session);
    this.userToken = this;
    if (this.global == null) {
      this.connMgr = GlobalState.newConnectionManager(sslConfig);
      this.userTokenKey = null;
      this.expectContinueKey = null;
      this.authSchemePools = new ConcurrentHashMap<>();
    } else {
      this.connMgr = this.global.getConnectionManager(sslConfig);
      this.userTokenKey = new GlobalState.CompoundKey(new Object[] { repo.getId(), repo.getUrl(), repo.getAuthentication(), repo.getProxy() });
      this.expectContinueKey = new GlobalState.CompoundKey(new Object[] { repo.getUrl(), repo.getProxy() });
      this.authSchemePools = this.global.getAuthSchemePools();
    } 
  }
  
  public HttpClientConnectionManager getConnectionManager() {
    return this.connMgr;
  }
  
  public Object getUserToken() {
    if (this.userToken == this)
      this.userToken = (this.global != null) ? this.global.getUserToken(this.userTokenKey) : null; 
    return this.userToken;
  }
  
  public void setUserToken(Object userToken) {
    this.userToken = userToken;
    if (this.global != null)
      this.global.setUserToken(this.userTokenKey, userToken); 
  }
  
  public boolean isExpectContinue() {
    if (this.expectContinue == null)
      this
        .expectContinue = Boolean.valueOf(!Boolean.FALSE.equals((this.global != null) ? this.global.getExpectContinue(this.expectContinueKey) : null)); 
    return this.expectContinue.booleanValue();
  }
  
  public void setExpectContinue(boolean enabled) {
    this.expectContinue = Boolean.valueOf(enabled);
    if (this.global != null)
      this.global.setExpectContinue(this.expectContinueKey, enabled); 
  }
  
  public Boolean getWebDav() {
    return this.webDav;
  }
  
  public void setWebDav(boolean webDav) {
    this.webDav = Boolean.valueOf(webDav);
  }
  
  public AuthScheme getAuthScheme(HttpHost host) {
    AuthSchemePool pool = this.authSchemePools.get(host);
    if (pool != null)
      return pool.get(); 
    return null;
  }
  
  public void setAuthScheme(HttpHost host, AuthScheme authScheme) {
    AuthSchemePool pool = this.authSchemePools.get(host);
    if (pool == null) {
      AuthSchemePool p = new AuthSchemePool();
      pool = this.authSchemePools.putIfAbsent(host, p);
      if (pool == null)
        pool = p; 
    } 
    pool.put(authScheme);
  }
  
  public void close() {
    if (this.global == null)
      this.connMgr.shutdown(); 
  }
}
