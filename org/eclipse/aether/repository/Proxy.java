package org.eclipse.aether.repository;

import java.util.Objects;

public final class Proxy {
  public static final String TYPE_HTTP = "http";
  
  public static final String TYPE_HTTPS = "https";
  
  private final String type;
  
  private final String host;
  
  private final int port;
  
  private final Authentication auth;
  
  public Proxy(String type, String host, int port) {
    this(type, host, port, null);
  }
  
  public Proxy(String type, String host, int port, Authentication auth) {
    this.type = (type != null) ? type : "";
    this.host = (host != null) ? host : "";
    this.port = port;
    this.auth = auth;
  }
  
  public String getType() {
    return this.type;
  }
  
  public String getHost() {
    return this.host;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public Authentication getAuthentication() {
    return this.auth;
  }
  
  public String toString() {
    return getHost() + ':' + getPort();
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    Proxy that = (Proxy)obj;
    return (Objects.equals(this.type, that.type) && 
      Objects.equals(this.host, that.host) && this.port == that.port && 
      Objects.equals(this.auth, that.auth));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + hash(this.host);
    hash = hash * 31 + hash(this.type);
    hash = hash * 31 + this.port;
    hash = hash * 31 + hash(this.auth);
    return hash;
  }
  
  private static int hash(Object obj) {
    return (obj != null) ? obj.hashCode() : 0;
  }
}
