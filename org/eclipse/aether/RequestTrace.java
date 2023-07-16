package org.eclipse.aether;

public class RequestTrace {
  private final RequestTrace parent;
  
  private final Object data;
  
  public static RequestTrace newChild(RequestTrace parent, Object data) {
    if (parent == null)
      return new RequestTrace(data); 
    return parent.newChild(data);
  }
  
  public RequestTrace(Object data) {
    this(null, data);
  }
  
  protected RequestTrace(RequestTrace parent, Object data) {
    this.parent = parent;
    this.data = data;
  }
  
  public final Object getData() {
    return this.data;
  }
  
  public final RequestTrace getParent() {
    return this.parent;
  }
  
  public RequestTrace newChild(Object data) {
    return new RequestTrace(this, data);
  }
  
  public String toString() {
    return String.valueOf(getData());
  }
}
