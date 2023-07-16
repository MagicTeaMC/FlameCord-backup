package org.eclipse.aether.transport.http;

import java.io.Closeable;
import org.apache.http.protocol.BasicHttpContext;

final class SharingHttpContext extends BasicHttpContext implements Closeable {
  private final LocalState state;
  
  private final SharingAuthCache authCache;
  
  SharingHttpContext(LocalState state) {
    this.state = state;
    this.authCache = new SharingAuthCache(state);
    super.setAttribute("http.auth.auth-cache", this.authCache);
  }
  
  public Object getAttribute(String id) {
    if ("http.user-token".equals(id))
      return this.state.getUserToken(); 
    return super.getAttribute(id);
  }
  
  public void setAttribute(String id, Object obj) {
    if ("http.user-token".equals(id)) {
      this.state.setUserToken(obj);
    } else {
      super.setAttribute(id, obj);
    } 
  }
  
  public Object removeAttribute(String id) {
    if ("http.user-token".equals(id)) {
      this.state.setUserToken(null);
      return null;
    } 
    return super.removeAttribute(id);
  }
  
  public void close() {
    this.authCache.clear();
  }
}
