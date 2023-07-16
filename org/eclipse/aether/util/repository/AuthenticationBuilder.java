package org.eclipse.aether.util.repository;

import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import org.eclipse.aether.repository.Authentication;

public final class AuthenticationBuilder {
  private final List<Authentication> authentications = new ArrayList<>();
  
  public Authentication build() {
    if (this.authentications.isEmpty())
      return null; 
    if (this.authentications.size() == 1)
      return this.authentications.get(0); 
    return new ChainedAuthentication(this.authentications);
  }
  
  public AuthenticationBuilder addUsername(String username) {
    return addString("username", username);
  }
  
  public AuthenticationBuilder addPassword(String password) {
    return addSecret("password", password);
  }
  
  public AuthenticationBuilder addPassword(char[] password) {
    return addSecret("password", password);
  }
  
  public AuthenticationBuilder addNtlm(String workstation, String domain) {
    addString("ntlm.workstation", workstation);
    return addString("ntlm.domain", domain);
  }
  
  public AuthenticationBuilder addPrivateKey(String pathname, String passphrase) {
    if (pathname != null) {
      addString("privateKey.path", pathname);
      addSecret("privateKey.passphrase", passphrase);
    } 
    return this;
  }
  
  public AuthenticationBuilder addPrivateKey(String pathname, char[] passphrase) {
    if (pathname != null) {
      addString("privateKey.path", pathname);
      addSecret("privateKey.passphrase", passphrase);
    } 
    return this;
  }
  
  public AuthenticationBuilder addHostnameVerifier(HostnameVerifier verifier) {
    if (verifier != null)
      this.authentications.add(new ComponentAuthentication("ssl.hostnameVerifier", verifier)); 
    return this;
  }
  
  public AuthenticationBuilder addString(String key, String value) {
    if (value != null)
      this.authentications.add(new StringAuthentication(key, value)); 
    return this;
  }
  
  public AuthenticationBuilder addSecret(String key, String value) {
    if (value != null)
      this.authentications.add(new SecretAuthentication(key, value)); 
    return this;
  }
  
  public AuthenticationBuilder addSecret(String key, char[] value) {
    if (value != null)
      this.authentications.add(new SecretAuthentication(key, value)); 
    return this;
  }
  
  public AuthenticationBuilder addCustom(Authentication authentication) {
    if (authentication != null)
      this.authentications.add(authentication); 
    return this;
  }
}
