package org.eclipse.aether.transport.http;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpHost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.eclipse.aether.RepositoryCache;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.util.ConfigUtils;

final class GlobalState implements Closeable {
  static class CompoundKey {
    private final Object[] keys;
    
    CompoundKey(Object... keys) {
      this.keys = keys;
    }
    
    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (obj == null || !getClass().equals(obj.getClass()))
        return false; 
      CompoundKey that = (CompoundKey)obj;
      return Arrays.equals(this.keys, that.keys);
    }
    
    public int hashCode() {
      int hash = 17;
      hash = hash * 31 + Arrays.hashCode(this.keys);
      return hash;
    }
    
    public String toString() {
      return Arrays.toString(this.keys);
    }
  }
  
  private static final String KEY = GlobalState.class.getName();
  
  private static final String CONFIG_PROP_CACHE_STATE = "aether.connector.http.cacheState";
  
  private final ConcurrentMap<SslConfig, HttpClientConnectionManager> connectionManagers;
  
  private final ConcurrentMap<CompoundKey, Object> userTokens;
  
  private final ConcurrentMap<HttpHost, AuthSchemePool> authSchemePools;
  
  private final ConcurrentMap<CompoundKey, Boolean> expectContinues;
  
  public static GlobalState get(RepositorySystemSession session) {
    GlobalState cache;
    RepositoryCache repoCache = session.getCache();
    if (repoCache == null || !ConfigUtils.getBoolean(session, true, new String[] { "aether.connector.http.cacheState" })) {
      cache = null;
    } else {
      Object tmp = repoCache.get(session, KEY);
      if (tmp instanceof GlobalState) {
        cache = (GlobalState)tmp;
      } else {
        synchronized (GlobalState.class) {
          tmp = repoCache.get(session, KEY);
          if (tmp instanceof GlobalState) {
            cache = (GlobalState)tmp;
          } else {
            cache = new GlobalState();
            repoCache.put(session, KEY, cache);
          } 
        } 
      } 
    } 
    return cache;
  }
  
  private GlobalState() {
    this.connectionManagers = new ConcurrentHashMap<>();
    this.userTokens = new ConcurrentHashMap<>();
    this.authSchemePools = new ConcurrentHashMap<>();
    this.expectContinues = new ConcurrentHashMap<>();
  }
  
  public void close() {
    Iterator<Map.Entry<SslConfig, HttpClientConnectionManager>> it = this.connectionManagers.entrySet().iterator();
    while (it.hasNext()) {
      HttpClientConnectionManager connMgr = (HttpClientConnectionManager)((Map.Entry)it.next()).getValue();
      it.remove();
      connMgr.shutdown();
    } 
  }
  
  public HttpClientConnectionManager getConnectionManager(SslConfig config) {
    HttpClientConnectionManager manager = this.connectionManagers.get(config);
    if (manager == null) {
      HttpClientConnectionManager connMgr = newConnectionManager(config);
      manager = this.connectionManagers.putIfAbsent(config, connMgr);
      if (manager != null) {
        connMgr.shutdown();
      } else {
        manager = connMgr;
      } 
    } 
    return manager;
  }
  
  public static HttpClientConnectionManager newConnectionManager(SslConfig sslConfig) {
    RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory());
    if (sslConfig == null) {
      registryBuilder.register("https", SSLConnectionSocketFactory.getSystemSocketFactory());
    } else {
      SSLSocketFactory sslSocketFactory = (sslConfig.context != null) ? sslConfig.context.getSocketFactory() : (SSLSocketFactory)SSLSocketFactory.getDefault();
      HostnameVerifier hostnameVerifier = (sslConfig.verifier != null) ? sslConfig.verifier : SSLConnectionSocketFactory.getDefaultHostnameVerifier();
      registryBuilder.register("https", new SSLConnectionSocketFactory(sslSocketFactory, sslConfig.protocols, sslConfig.cipherSuites, hostnameVerifier));
    } 
    PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(registryBuilder.build());
    connMgr.setMaxTotal(100);
    connMgr.setDefaultMaxPerRoute(50);
    return (HttpClientConnectionManager)connMgr;
  }
  
  public Object getUserToken(CompoundKey key) {
    return this.userTokens.get(key);
  }
  
  public void setUserToken(CompoundKey key, Object userToken) {
    if (userToken != null) {
      this.userTokens.put(key, userToken);
    } else {
      this.userTokens.remove(key);
    } 
  }
  
  public ConcurrentMap<HttpHost, AuthSchemePool> getAuthSchemePools() {
    return this.authSchemePools;
  }
  
  public Boolean getExpectContinue(CompoundKey key) {
    return this.expectContinues.get(key);
  }
  
  public void setExpectContinue(CompoundKey key, boolean enabled) {
    this.expectContinues.put(key, Boolean.valueOf(enabled));
  }
}
