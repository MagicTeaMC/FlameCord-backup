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
public class RFC2965SpecFactory implements CookieSpecFactory, CookieSpecProvider {
  private final CookieSpec cookieSpec;
  
  public RFC2965SpecFactory(String[] datepatterns, boolean oneHeader) {
    this.cookieSpec = new RFC2965Spec(datepatterns, oneHeader);
  }
  
  public RFC2965SpecFactory() {
    this(null, false);
  }
  
  public CookieSpec newInstance(HttpParams params) {
    if (params != null) {
      String[] patterns = null;
      Collection<?> param = (Collection)params.getParameter("http.protocol.cookie-datepatterns");
      if (param != null) {
        patterns = new String[param.size()];
        patterns = param.<String>toArray(patterns);
      } 
      boolean singleHeader = params.getBooleanParameter("http.protocol.single-cookie-header", false);
      return new RFC2965Spec(patterns, singleHeader);
    } 
    return new RFC2965Spec();
  }
  
  public CookieSpec create(HttpContext context) {
    return this.cookieSpec;
  }
}
