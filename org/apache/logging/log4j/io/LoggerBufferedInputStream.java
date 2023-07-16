package org.apache.logging.log4j.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalBufferedInputStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerBufferedInputStream extends BufferedInputStream {
  private static final String FQCN = LoggerBufferedInputStream.class.getName();
  
  private InternalBufferedInputStream stream;
  
  protected LoggerBufferedInputStream(InputStream in, Charset charset, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(in);
    this.stream = new InternalBufferedInputStream(in, charset, logger, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  protected LoggerBufferedInputStream(InputStream in, Charset charset, int size, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(in);
    this.stream = new InternalBufferedInputStream(in, charset, size, logger, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  public void close() throws IOException {
    this.stream.close();
  }
  
  public synchronized int read() throws IOException {
    return this.stream.read();
  }
  
  public int read(byte[] b) throws IOException {
    return this.stream.read(b);
  }
  
  public synchronized int read(byte[] b, int off, int len) throws IOException {
    return this.stream.read(b, off, len);
  }
  
  public String toString() {
    return LoggerBufferedInputStream.class.getSimpleName() + this.stream.toString();
  }
}
