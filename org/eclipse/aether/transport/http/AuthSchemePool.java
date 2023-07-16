package org.eclipse.aether.transport.http;

import java.util.LinkedList;
import org.apache.http.auth.AuthScheme;
import org.apache.http.impl.auth.BasicScheme;

final class AuthSchemePool {
  private final LinkedList<AuthScheme> authSchemes = new LinkedList<>();
  
  private String schemeName;
  
  public synchronized AuthScheme get() {
    BasicScheme basicScheme;
    AuthScheme authScheme = null;
    if (!this.authSchemes.isEmpty()) {
      authScheme = this.authSchemes.removeLast();
    } else if ("Basic".equalsIgnoreCase(this.schemeName)) {
      basicScheme = new BasicScheme();
    } 
    return (AuthScheme)basicScheme;
  }
  
  public synchronized void put(AuthScheme authScheme) {
    if (authScheme == null)
      return; 
    if (!authScheme.getSchemeName().equals(this.schemeName)) {
      this.schemeName = authScheme.getSchemeName();
      this.authSchemes.clear();
    } 
    this.authSchemes.add(authScheme);
  }
}
