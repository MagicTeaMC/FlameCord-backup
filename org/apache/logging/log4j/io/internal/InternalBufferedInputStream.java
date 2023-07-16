package org.apache.logging.log4j.io.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalBufferedInputStream extends BufferedInputStream {
  private static final String FQCN = InternalBufferedInputStream.class.getName();
  
  public InternalBufferedInputStream(InputStream in, Charset charset, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(new InternalInputStream(in, charset, logger, (fqcn == null) ? FQCN : fqcn, level, marker));
  }
  
  public InternalBufferedInputStream(InputStream in, Charset charset, int size, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(new InternalInputStream(in, charset, logger, (fqcn == null) ? FQCN : fqcn, level, marker), size);
  }
  
  public void close() throws IOException {
    super.close();
  }
  
  public synchronized int read() throws IOException {
    return super.read();
  }
  
  public int read(byte[] b) throws IOException {
    return super.read(b, 0, b.length);
  }
  
  public synchronized int read(byte[] b, int off, int len) throws IOException {
    return super.read(b, off, len);
  }
  
  public String toString() {
    return "{stream=" + this.in + '}';
  }
}
