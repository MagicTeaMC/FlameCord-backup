package org.eclipse.aether.transport.http;

import java.net.URI;
import org.apache.http.client.methods.HttpRequestBase;

final class HttpMkCol extends HttpRequestBase {
  HttpMkCol(URI uri) {
    setURI(uri);
  }
  
  public String getMethod() {
    return "MKCOL";
  }
}
