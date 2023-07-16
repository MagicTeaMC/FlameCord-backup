package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class StringInputStream extends InputStream {
  private StringReader in;
  
  public StringInputStream(String source) {
    this.in = new StringReader(source);
  }
  
  public int read() throws IOException {
    return this.in.read();
  }
  
  public void close() throws IOException {
    this.in.close();
  }
  
  public synchronized void mark(int limit) {
    try {
      this.in.mark(limit);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe.getMessage());
    } 
  }
  
  public synchronized void reset() throws IOException {
    this.in.reset();
  }
  
  public boolean markSupported() {
    return this.in.markSupported();
  }
}
