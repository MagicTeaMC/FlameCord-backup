package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.entity.HttpEntityWrapper;

@Deprecated
public class EntityEnclosingRequestWrapper extends RequestWrapper implements HttpEntityEnclosingRequest {
  private HttpEntity entity;
  
  private boolean consumed;
  
  public EntityEnclosingRequestWrapper(HttpEntityEnclosingRequest request) throws ProtocolException {
    super((HttpRequest)request);
    setEntity(request.getEntity());
  }
  
  public HttpEntity getEntity() {
    return this.entity;
  }
  
  public void setEntity(HttpEntity entity) {
    this.entity = (entity != null) ? (HttpEntity)new EntityWrapper(entity) : null;
    this.consumed = false;
  }
  
  public boolean expectContinue() {
    Header expect = getFirstHeader("Expect");
    return (expect != null && "100-continue".equalsIgnoreCase(expect.getValue()));
  }
  
  public boolean isRepeatable() {
    return (this.entity == null || this.entity.isRepeatable() || !this.consumed);
  }
  
  class EntityWrapper extends HttpEntityWrapper {
    EntityWrapper(HttpEntity entity) {
      super(entity);
    }
    
    public void consumeContent() throws IOException {
      EntityEnclosingRequestWrapper.this.consumed = true;
      super.consumeContent();
    }
    
    public InputStream getContent() throws IOException {
      EntityEnclosingRequestWrapper.this.consumed = true;
      return super.getContent();
    }
    
    public void writeTo(OutputStream outStream) throws IOException {
      EntityEnclosingRequestWrapper.this.consumed = true;
      super.writeTo(outStream);
    }
  }
}
