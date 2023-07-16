package org.apache.logging.log4j.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalInputStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerInputStream extends FilterInputStream {
  private static final String FQCN = LoggerInputStream.class.getName();
  
  private final InternalInputStream logger;
  
  protected LoggerInputStream(InputStream in, Charset charset, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(in);
    this.logger = new InternalInputStream(in, charset, logger, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  public void close() throws IOException {
    this.logger.close();
  }
  
  public int read() throws IOException {
    return this.logger.read();
  }
  
  public int read(byte[] b) throws IOException {
    return this.logger.read(b);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    return this.logger.read(b, off, len);
  }
  
  public String toString() {
    return LoggerInputStream.class.getSimpleName() + this.logger.toString();
  }
}
