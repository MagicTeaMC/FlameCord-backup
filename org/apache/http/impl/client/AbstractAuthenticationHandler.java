package org.apache.http.impl.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;
import org.apache.http.util.CharArrayBuffer;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public abstract class AbstractAuthenticationHandler implements AuthenticationHandler {
  private final Log log = LogFactory.getLog(getClass());
  
  private static final List<String> DEFAULT_SCHEME_PRIORITY = Collections.unmodifiableList(Arrays.asList(new String[] { "Negotiate", "NTLM", "Digest", "Basic" }));
  
  protected Map<String, Header> parseChallenges(Header[] headers) throws MalformedChallengeException {
    Map<String, Header> map = new HashMap<String, Header>(headers.length);
    for (Header header : headers) {
      CharArrayBuffer buffer;
      int pos;
      if (header instanceof FormattedHeader) {
        buffer = ((FormattedHeader)header).getBuffer();
        pos = ((FormattedHeader)header).getValuePos();
      } else {
        String str = header.getValue();
        if (str == null)
          throw new MalformedChallengeException("Header value is null"); 
        buffer = new CharArrayBuffer(str.length());
        buffer.append(str);
        pos = 0;
      } 
      while (pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos)))
        pos++; 
      int beginIndex = pos;
      while (pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos)))
        pos++; 
      int endIndex = pos;
      String s = buffer.substring(beginIndex, endIndex);
      map.put(s.toLowerCase(Locale.ROOT), header);
    } 
    return map;
  }
  
  protected List<String> getAuthPreferences() {
    return DEFAULT_SCHEME_PRIORITY;
  }
  
  protected List<String> getAuthPreferences(HttpResponse response, HttpContext context) {
    return getAuthPreferences();
  }
  
  public AuthScheme selectScheme(Map<String, Header> challenges, HttpResponse response, HttpContext context) throws AuthenticationException {
    AuthSchemeRegistry registry = (AuthSchemeRegistry)context.getAttribute("http.authscheme-registry");
    Asserts.notNull(registry, "AuthScheme registry");
    Collection<String> authPrefs = getAuthPreferences(response, context);
    if (authPrefs == null)
      authPrefs = DEFAULT_SCHEME_PRIORITY; 
    if (this.log.isDebugEnabled())
      this.log.debug("Authentication schemes in the order of preference: " + authPrefs); 
    AuthScheme authScheme = null;
    for (String id : authPrefs) {
      Header challenge = challenges.get(id.toLowerCase(Locale.ENGLISH));
      if (challenge != null) {
        if (this.log.isDebugEnabled())
          this.log.debug(id + " authentication scheme selected"); 
        try {
          authScheme = registry.getAuthScheme(id, response.getParams());
          break;
        } catch (IllegalStateException e) {
          if (this.log.isWarnEnabled())
            this.log.warn("Authentication scheme " + id + " not supported"); 
          continue;
        } 
      } 
      if (this.log.isDebugEnabled())
        this.log.debug("Challenge for " + id + " authentication scheme not available"); 
    } 
    if (authScheme == null)
      throw new AuthenticationException("Unable to respond to any of these challenges: " + challenges); 
    return authScheme;
  }
}
