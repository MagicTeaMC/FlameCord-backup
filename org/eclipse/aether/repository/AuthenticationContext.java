package org.eclipse.aether.repository;

import java.io.Closeable;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.RepositorySystemSession;

public final class AuthenticationContext implements Closeable {
  public static final String USERNAME = "username";
  
  public static final String PASSWORD = "password";
  
  public static final String NTLM_DOMAIN = "ntlm.domain";
  
  public static final String NTLM_WORKSTATION = "ntlm.workstation";
  
  public static final String PRIVATE_KEY_PATH = "privateKey.path";
  
  public static final String PRIVATE_KEY_PASSPHRASE = "privateKey.passphrase";
  
  public static final String HOST_KEY_ACCEPTANCE = "hostKey.acceptance";
  
  public static final String HOST_KEY_REMOTE = "hostKey.remote";
  
  public static final String HOST_KEY_LOCAL = "hostKey.local";
  
  public static final String SSL_CONTEXT = "ssl.context";
  
  public static final String SSL_HOSTNAME_VERIFIER = "ssl.hostnameVerifier";
  
  private final RepositorySystemSession session;
  
  private final RemoteRepository repository;
  
  private final Proxy proxy;
  
  private final Authentication auth;
  
  private final Map<String, Object> authData;
  
  private boolean fillingAuthData;
  
  public static AuthenticationContext forRepository(RepositorySystemSession session, RemoteRepository repository) {
    return newInstance(session, repository, null, repository.getAuthentication());
  }
  
  public static AuthenticationContext forProxy(RepositorySystemSession session, RemoteRepository repository) {
    Proxy proxy = repository.getProxy();
    return newInstance(session, repository, proxy, (proxy != null) ? proxy.getAuthentication() : null);
  }
  
  private static AuthenticationContext newInstance(RepositorySystemSession session, RemoteRepository repository, Proxy proxy, Authentication auth) {
    if (auth == null)
      return null; 
    return new AuthenticationContext(session, repository, proxy, auth);
  }
  
  private AuthenticationContext(RepositorySystemSession session, RemoteRepository repository, Proxy proxy, Authentication auth) {
    this.session = Objects.<RepositorySystemSession>requireNonNull(session, "repository system session cannot be null");
    this.repository = repository;
    this.proxy = proxy;
    this.auth = auth;
    this.authData = new HashMap<>();
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
  
  public String get(String key) {
    return get(key, null, String.class);
  }
  
  public <T> T get(String key, Class<T> type) {
    return get(key, null, type);
  }
  
  public <T> T get(String key, Map<String, String> data, Class<T> type) {
    Object value;
    Objects.requireNonNull(key, "authentication key cannot be null");
    if (key.length() == 0)
      throw new IllegalArgumentException("authentication key cannot be empty"); 
    synchronized (this.authData) {
      value = this.authData.get(key);
      if (value == null && !this.authData.containsKey(key) && !this.fillingAuthData) {
        if (this.auth != null) {
          try {
            this.fillingAuthData = true;
            this.auth.fill(this, key, data);
          } finally {
            this.fillingAuthData = false;
          } 
          value = this.authData.get(key);
        } 
        if (value == null)
          this.authData.put(key, value); 
      } 
    } 
    return convert(value, type);
  }
  
  private <T> T convert(Object value, Class<T> type) {
    if (!type.isInstance(value))
      if (String.class.equals(type)) {
        if (value instanceof File) {
          value = ((File)value).getPath();
        } else if (value instanceof char[]) {
          value = new String((char[])value);
        } 
      } else if (File.class.equals(type)) {
        if (value instanceof String)
          value = new File((String)value); 
      } else if (char[].class.equals(type)) {
        if (value instanceof String)
          value = ((String)value).toCharArray(); 
      }  
    if (type.isInstance(value))
      return type.cast(value); 
    return null;
  }
  
  public void put(String key, Object value) {
    Objects.requireNonNull(key, "authentication key cannot be null");
    if (key.length() == 0)
      throw new IllegalArgumentException("authentication key cannot be empty"); 
    synchronized (this.authData) {
      Object oldValue = this.authData.put(key, value);
      if (oldValue instanceof char[])
        Arrays.fill((char[])oldValue, false); 
    } 
  }
  
  public void close() {
    synchronized (this.authData) {
      for (Object value : this.authData.values()) {
        if (value instanceof char[])
          Arrays.fill((char[])value, false); 
      } 
      this.authData.clear();
    } 
  }
  
  public static void close(AuthenticationContext context) {
    if (context != null)
      context.close(); 
  }
}
