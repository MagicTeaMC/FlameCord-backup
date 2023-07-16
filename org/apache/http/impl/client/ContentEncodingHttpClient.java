package org.apache.http.impl.client;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class ContentEncodingHttpClient extends DefaultHttpClient {
  public ContentEncodingHttpClient(ClientConnectionManager conman, HttpParams params) {
    super(conman, params);
  }
  
  public ContentEncodingHttpClient(HttpParams params) {
    this(null, params);
  }
  
  public ContentEncodingHttpClient() {
    this(null);
  }
  
  protected BasicHttpProcessor createHttpProcessor() {
    BasicHttpProcessor result = super.createHttpProcessor();
    result.addRequestInterceptor((HttpRequestInterceptor)new RequestAcceptEncoding());
    result.addResponseInterceptor((HttpResponseInterceptor)new ResponseContentEncoding());
    return result;
  }
}
