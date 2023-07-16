package org.apache.maven.artifact.repository;

public class Authentication {
  private String privateKey;
  
  private String passphrase;
  
  private String username;
  
  private String password;
  
  public Authentication(String userName, String password) {
    this.username = userName;
    this.password = password;
  }
  
  public String getPassword() {
    return this.password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getUsername() {
    return this.username;
  }
  
  public void setUsername(String userName) {
    this.username = userName;
  }
  
  public String getPassphrase() {
    return this.passphrase;
  }
  
  public void setPassphrase(String passphrase) {
    this.passphrase = passphrase;
  }
  
  public String getPrivateKey() {
    return this.privateKey;
  }
  
  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }
}
