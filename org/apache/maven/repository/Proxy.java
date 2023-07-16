package org.apache.maven.repository;

public class Proxy {
  public static final String PROXY_SOCKS5 = "SOCKS_5";
  
  public static final String PROXY_SOCKS4 = "SOCKS4";
  
  public static final String PROXY_HTTP = "HTTP";
  
  private String host;
  
  private String userName;
  
  private String password;
  
  private int port;
  
  private String protocol;
  
  private String nonProxyHosts;
  
  private String ntlmHost;
  
  private String ntlmDomain;
  
  public String getHost() {
    return this.host;
  }
  
  public void setHost(String host) {
    this.host = host;
  }
  
  public String getPassword() {
    return this.password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public void setPort(int port) {
    this.port = port;
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public String getProtocol() {
    return this.protocol;
  }
  
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }
  
  public String getNonProxyHosts() {
    return this.nonProxyHosts;
  }
  
  public void setNonProxyHosts(String nonProxyHosts) {
    this.nonProxyHosts = nonProxyHosts;
  }
  
  public String getNtlmHost() {
    return this.ntlmHost;
  }
  
  public void setNtlmHost(String ntlmHost) {
    this.ntlmHost = ntlmHost;
  }
  
  public void setNtlmDomain(String ntlmDomain) {
    this.ntlmDomain = ntlmDomain;
  }
  
  public String getNtlmDomain() {
    return this.ntlmDomain;
  }
}
