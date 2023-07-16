package org.apache.logging.log4j.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalOutputStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerOutputStream extends OutputStream {
  private static final String FQCN = LoggerOutputStream.class.getName();
  
  private final InternalOutputStream logger;
  
  protected LoggerOutputStream(ExtendedLogger logger, Level level, Marker marker, Charset charset, String fqcn) {
    this.logger = new InternalOutputStream(logger, level, marker, charset, (fqcn == null) ? FQCN : fqcn);
  }
  
  public void close() throws IOException {
    this.logger.close();
  }
  
  public void flush() throws IOException {}
  
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
