package org.apache.logging.log4j.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalFilterOutputStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerFilterOutputStream extends FilterOutputStream {
  private static final String FQCN = LoggerFilterOutputStream.class.getName();
  
  private final InternalFilterOutputStream logger;
  
  protected LoggerFilterOutputStream(OutputStream out, Charset charset, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(out);
    this.logger = new InternalFilterOutputStream(out, charset, logger, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  public void close() throws IOException {
    this.logger.close();
  }
  
  public void flush() throws IOException {
    this.logger.flush();
  }
  
  public String toString() {
    return LoggerFilterOutputStream.class.getSimpleName() + this.logger.toString();
  }
  
  public void write(byte[] b) throws IOException {
    this.logger.write(b);
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    this.logger.write(b, off, len);
  }
  
  public void write(int b) throws IOException {
    this.logger.write(b);
  }
}
