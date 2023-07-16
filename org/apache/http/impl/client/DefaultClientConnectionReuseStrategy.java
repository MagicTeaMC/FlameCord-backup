package org.apache.http.impl.client;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHeaderIterator;
import org.apache.http.message.BasicTokenIterator;
import org.apache.http.protocol.HttpContext;

public class DefaultClientConnectionReuseStrategy extends DefaultConnectionReuseStrategy {
  public static final DefaultClientConnectionReuseStrategy INSTANCE = new DefaultClientConnectionReuseStrategy();
  
  public boolean keepAlive(HttpResponse response, HttpContext context) {
    HttpRequest request = (HttpRequest)context.getAttribute("http.request");
    if (request != null) {
      Header[] connHeaders = request.getHeaders("Connection");
      if (connHeaders.length != 0) {
        BasicTokenIterator basicTokenIterator = new BasicTokenIterator((HeaderIterator)new BasicHeaderIterator(connHeaders, null));
        while (basicTokenIterator.hasNext()) {
          String token = basicTokenIterator.nextToken();
          if ("Close".equalsIgnoreCase(token))
            return false; 
        } 
      } 
    } 
    return super.keepAlive(response, context);
  }
}
