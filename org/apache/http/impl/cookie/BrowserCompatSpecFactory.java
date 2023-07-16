package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BrowserCompatSpecFactory implements CookieSpecFactory, CookieSpecProvider {
  private final SecurityLevel securityLevel;
  
  private final CookieSpec cookieSpec;
  
  public enum SecurityLevel {
    SECURITYLEVEL_DEFAULT, SECURITYLEVEL_IE_MEDIUM;
  }
  
  public BrowserCompatSpecFactory(String[] datepatterns, SecurityLevel securityLevel) {
    this.securityLevel = securityLevel;
    this.cookieSpec = new BrowserCompatSpec(datepatterns, securityLevel);
  }
  
  public BrowserCompatSpecFactory(String[] datepatterns) {
    this(null, SecurityLevel.SECURITYLEVEL_DEFAULT);
  }
  
  public BrowserCompatSpecFactory() {
    this(null, SecurityLevel.SECURITYLEVEL_DEFAULT);
  }
  
  public CookieSpec newInstance(HttpParams params) {
    if (params != null) {
      String[] patterns = null;
      Collection<?> param = (Collection)params.getParameter("http.protocol.cookie-datepatterns");
      if (param != null) {
        patterns = new String[param.size()];
        patterns = param.<String>toArray(patterns);
      } 
      return new BrowserCompatSpec(patterns, this.securityLevel);
    } 
    return new BrowserCompatSpec(null, this.securityLevel);
  }
  
  public CookieSpec create(HttpContext context) {
    return this.cookieSpec;
  }
}
