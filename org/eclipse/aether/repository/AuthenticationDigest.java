package org.eclipse.aether.repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.eclipse.aether.RepositorySystemSession;

public final class AuthenticationDigest {
  private final MessageDigest digest;
  
  private final RepositorySystemSession session;
  
  private final RemoteRepository repository;
  
  private final Proxy proxy;
  
  public static String forRepository(RepositorySystemSession session, RemoteRepository repository) {
    String digest = "";
    Authentication auth = repository.getAuthentication();
    if (auth != null) {
      AuthenticationDigest authDigest = new AuthenticationDigest(session, repository, null);
      auth.digest(authDigest);
      digest = authDigest.digest();
    } 
    return digest;
  }
  
  public static String forProxy(RepositorySystemSession session, RemoteRepository repository) {
    String digest = "";
    Proxy proxy = repository.getProxy();
    if (proxy != null) {
      Authentication auth = proxy.getAuthentication();
      if (auth != null) {
        AuthenticationDigest authDigest = new AuthenticationDigest(session, repository, proxy);
        auth.digest(authDigest);
        digest = authDigest.digest();
      } 
    } 
    return digest;
  }
  
  private AuthenticationDigest(RepositorySystemSession session, RemoteRepository repository, Proxy proxy) {
    this.session = session;
    this.repository = repository;
    this.proxy = proxy;
    this.digest = newDigest();
  }
  
  private static MessageDigest newDigest() {
    try {
      return MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      try {
        return MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException ne) {
        throw new IllegalStateException(ne);
      } 
    } 
  }
  
  public RepositorySystemSession getSession() {
    return this.session;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public Proxy getProxy() {
    return this.proxy;
  }
  
  public void update(String... strings) {
    if (strings != null)
      for (String string : strings) {
        if (string != null)
          this.digest.update(string.getBytes(StandardCharsets.UTF_8)); 
      }  
  }
  
  public void update(char... chars) {
    if (chars != null)
      for (char c : chars) {
        this.digest.update((byte)(c >> 8));
        this.digest.update((byte)(c & 0xFF));
      }  
  }
  
  public void update(byte... bytes) {
    if (bytes != null)
      this.digest.update(bytes); 
  }
  
  private String digest() {
    byte[] bytes = this.digest.digest();
    StringBuilder buffer = new StringBuilder(bytes.length * 2);
    for (byte aByte : bytes) {
      int b = aByte & 0xFF;
      if (b < 16)
        buffer.append('0'); 
      buffer.append(Integer.toHexString(b));
    } 
    return buffer.toString();
  }
}
