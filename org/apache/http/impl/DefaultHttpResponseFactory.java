package org.apache.http.impl;

import java.util.Locale;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpResponseFactory implements HttpResponseFactory {
  public static final DefaultHttpResponseFactory INSTANCE = new DefaultHttpResponseFactory();
  
  protected final ReasonPhraseCatalog reasonCatalog;
  
  public DefaultHttpResponseFactory(ReasonPhraseCatalog catalog) {
    this.reasonCatalog = (ReasonPhraseCatalog)Args.notNull(catalog, "Reason phrase catalog");
  }
  
  public DefaultHttpResponseFactory() {
    this(EnglishReasonPhraseCatalog.INSTANCE);
  }
  
  public HttpResponse newHttpResponse(ProtocolVersion ver, int status, HttpContext context) {
    Args.notNull(ver, "HTTP version");
    Locale loc = determineLocale(context);
    String reason = this.reasonCatalog.getReason(status, loc);
    BasicStatusLine basicStatusLine = new BasicStatusLine(ver, status, reason);
    return (HttpResponse)new BasicHttpResponse((StatusLine)basicStatusLine, this.reasonCatalog, loc);
  }
  
  public HttpResponse newHttpResponse(StatusLine statusline, HttpContext context) {
    Args.notNull(statusline, "Status line");
    return (HttpResponse)new BasicHttpResponse(statusline, this.reasonCatalog, determineLocale(context));
  }
  
  protected Locale determineLocale(HttpContext context) {
    return Locale.getDefault();
  }
}
