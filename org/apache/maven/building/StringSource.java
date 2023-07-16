package org.apache.maven.building;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StringSource implements Source {
  private String content;
  
  private String location;
  
  public StringSource(CharSequence content) {
    this(content, null);
  }
  
  public StringSource(CharSequence content, String location) {
    this.content = (content != null) ? content.toString() : "";
    this.location = (location != null) ? location : "(memory)";
  }
  
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(this.content.getBytes(StandardCharsets.UTF_8));
  }
  
  public String getLocation() {
    return this.location;
  }
  
  public String getContent() {
    return this.content;
  }
  
  public String toString() {
    return getLocation();
  }
}
