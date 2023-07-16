package org.apache.http.entity;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

public abstract class AbstractHttpEntity implements HttpEntity {
  protected static final int OUTPUT_BUFFER_SIZE = 4096;
  
  protected Header contentType;
  
  protected Header contentEncoding;
  
  protected boolean chunked;
  
  public Header getContentType() {
    return this.contentType;
  }
  
  public Header getContentEncoding() {
    return this.contentEncoding;
  }
  
  public boolean isChunked() {
    return this.chunked;
  }
  
  public void setContentType(Header contentType) {
    this.contentType = contentType;
  }
  
  public void setContentType(String ctString) {
    BasicHeader basicHeader;
    Header h = null;
    if (ctString != null)
      basicHeader = new BasicHeader("Content-Type", ctString); 
    setContentType((Header)basicHeader);
  }
  
  public void setContentEncoding(Header contentEncoding) {
    this.contentEncoding = contentEncoding;
  }
  
  public void setContentEncoding(String ceString) {
    BasicHeader basicHeader;
    Header h = null;
    if (ceString != null)
      basicHeader = new BasicHeader("Content-Encoding", ceString); 
    setContentEncoding((Header)basicHeader);
  }
  
  public void setChunked(boolean b) {
    this.chunked = b;
  }
  
  @Deprecated
  public void consumeContent() throws IOException {}
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    if (this.contentType != null) {
      sb.append("Content-Type: ");
      sb.append(this.contentType.getValue());
      sb.append(',');
    } 
    if (this.contentEncoding != null) {
      sb.append("Content-Encoding: ");
      sb.append(this.contentEncoding.getValue());
      sb.append(',');
    } 
    long len = getContentLength();
    if (len >= 0L) {
      sb.append("Content-Length: ");
      sb.append(len);
      sb.append(',');
    } 
    sb.append("Chunked: ");
    sb.append(this.chunked);
    sb.append(']');
    return sb.toString();
  }
}
