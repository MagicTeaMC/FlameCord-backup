package org.eclipse.aether.transport.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.eclipse.aether.repository.AuthenticationContext;

final class DeferredCredentialsProvider implements CredentialsProvider {
  private final CredentialsProvider delegate = (CredentialsProvider)new BasicCredentialsProvider();
  
  private final Map<AuthScope, Factory> factories = new HashMap<>();
  
  public void setCredentials(AuthScope authScope, Factory factory) {
    this.factories.put(authScope, factory);
  }
  
  public void setCredentials(AuthScope authScope, Credentials credentials) {
    this.delegate.setCredentials(authScope, credentials);
  }
  
  public Credentials getCredentials(AuthScope authScope) {
    synchronized (this.factories) {
      for (Iterator<Map.Entry<AuthScope, Factory>> it = this.factories.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry<AuthScope, Factory> entry = it.next();
        if (authScope.match(entry.getKey()) >= 0) {
          it.remove();
          this.delegate.setCredentials(entry.getKey(), ((Factory)entry.getValue()).newCredentials());
        } 
      } 
    } 
    return this.delegate.getCredentials(authScope);
  }
  
  public void clear() {
    this.delegate.clear();
  }
  
  static interface Factory {
    Credentials newCredentials();
  }
  
  static class BasicFactory implements Factory {
    private final AuthenticationContext authContext;
    
    BasicFactory(AuthenticationContext authContext) {
      this.authContext = authContext;
    }
    
    public Credentials newCredentials() {
      String username = this.authContext.get("username");
      if (username == null)
        return null; 
      String password = this.authContext.get("password");
      return (Credentials)new UsernamePasswordCredentials(username, password);
    }
  }
  
  static class NtlmFactory implements Factory {
    private final AuthenticationContext authContext;
    
    NtlmFactory(AuthenticationContext authContext) {
      this.authContext = authContext;
    }
    
    public Credentials newCredentials() {
      String username = this.authContext.get("username");
      if (username == null)
        return null; 
      String password = this.authContext.get("password");
      String domain = this.authContext.get("ntlm.domain");
      String workstation = this.authContext.get("ntlm.workstation");
      if (domain == null) {
        int backslash = username.indexOf('\\');
        if (backslash < 0) {
          domain = guessDomain();
        } else {
          domain = username.substring(0, backslash);
          username = username.substring(backslash + 1);
        } 
      } 
      if (workstation == null)
        workstation = guessWorkstation(); 
      return (Credentials)new NTCredentials(username, password, workstation, domain);
    }
    
    private static String guessDomain() {
      return safeNtlmString(new String[] { System.getProperty("http.auth.ntlm.domain"), System.getenv("USERDOMAIN") });
    }
    
    private static String guessWorkstation() {
      String localHost = null;
      try {
        localHost = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException unknownHostException) {}
      return safeNtlmString(new String[] { System.getProperty("http.auth.ntlm.host"), System.getenv("COMPUTERNAME"), localHost });
    }
    
    private static String safeNtlmString(String... strings) {
      for (String string : strings) {
        if (string != null)
          return string; 
      } 
      return "";
    }
  }
}
