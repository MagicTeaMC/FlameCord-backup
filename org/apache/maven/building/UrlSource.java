package org.apache.maven.building;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class UrlSource implements Source {
  private URL url;
  
  public UrlSource(URL url) {
    this.url = Objects.<URL>requireNonNull(url, "url cannot be null");
  }
  
  public InputStream getInputStream() throws IOException {
    return this.url.openStream();
  }
  
  public String getLocation() {
    return this.url.toString();
  }
  
  public URL getUrl() {
    return this.url;
  }
  
  public String toString() {
    return getLocation();
  }
}
