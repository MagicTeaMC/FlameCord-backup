package org.apache.http.message;

import java.io.Serializable;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicRequestLine implements RequestLine, Cloneable, Serializable {
  private static final long serialVersionUID = 2810581718468737193L;
  
  private final ProtocolVersion protoversion;
  
  private final String method;
  
  private final String uri;
  
  public BasicRequestLine(String method, String uri, ProtocolVersion version) {
    this.method = (String)Args.notNull(method, "Method");
    this.uri = (String)Args.notNull(uri, "URI");
    this.protoversion = (ProtocolVersion)Args.notNull(version, "Version");
  }
  
  public String getMethod() {
    return this.method;
  }
  
  public ProtocolVersion getProtocolVersion() {
    return this.protoversion;
  }
  
  public String getUri() {
    return this.uri;
  }
  
  public String toString() {
    return BasicLineFormatter.INSTANCE.formatRequestLine((CharArrayBuffer)null, this).toString();
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
