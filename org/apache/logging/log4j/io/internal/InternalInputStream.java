package org.apache.logging.log4j.io.internal;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.ByteStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalInputStream extends FilterInputStream {
  private final String fqcn;
  
  private final ByteStreamLogger logger;
  
  public InternalInputStream(InputStream in, Charset charset, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(in);
    this.logger = new ByteStreamLogger(logger, level, marker, charset);
    this.fqcn = fqcn;
  }
  
  public void close() throws IOException {
    this.logger.close(this.fqcn);
    super.close();
  }
  
  public int read() throws IOException {
    int b = super.read();
    this.logger.put(this.fqcn, b);
    return b;
  }
  
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    int bytesRead = super.read(b, off, len);
    this.logger.put(this.fqcn, b, off, bytesRead);
    return bytesRead;
  }
  
  public String toString() {
    return "{stream=" + this.in + '}';
  }
}
