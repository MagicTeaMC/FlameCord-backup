package org.apache.http.impl.client;

import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultTargetAuthenticationHandler extends AbstractAuthenticationHandler {
  public boolean isAuthenticationRequested(HttpResponse response, HttpContext context) {
    Args.notNull(response, "HTTP response");
    int status = response.getStatusLine().getStatusCode();
    return (status == 401);
  }
  
  public Map<String, Header> getChallenges(HttpResponse response, HttpContext context) throws MalformedChallengeException {
    Args.notNull(response, "HTTP response");
    Header[] headers = response.getHeaders("WWW-Authenticate");
    return parseChallenges(headers);
  }
  
  protected List<String> getAuthPreferences(HttpResponse response, HttpContext context) {
    List<String> authpref = (List<String>)response.getParams().getParameter("http.auth.target-scheme-pref");
    if (authpref != null)
      return authpref; 
    return super.getAuthPreferences(response, context);
  }
}
