package org.apache.http.auth;

import org.apache.http.Header;
import org.apache.http.HttpRequest;

public interface AuthScheme {
  void processChallenge(Header paramHeader) throws MalformedChallengeException;
  
  String getSchemeName();
  
  String getParameter(String paramString);
  
  String getRealm();
  
  boolean isConnectionBased();
  
  boolean isComplete();
  
  @Deprecated
  Header authenticate(Credentials paramCredentials, HttpRequest paramHttpRequest) throws AuthenticationException;
}
