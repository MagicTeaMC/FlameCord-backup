package org.apache.http.client;

import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.protocol.HttpContext;

@Deprecated
public interface RedirectHandler {
  boolean isRedirectRequested(HttpResponse paramHttpResponse, HttpContext paramHttpContext);
  
  URI getLocationURI(HttpResponse paramHttpResponse, HttpContext paramHttpContext) throws ProtocolException;
}
