package org.apache.logging.log4j.io.internal;

import java.io.IOException;
import java.io.Writer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.CharStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalWriter extends Writer {
  private final CharStreamLogger logger;
  
  private final String fqcn;
  
  public InternalWriter(ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    this.logger = new CharStreamLogger(logger, level, marker);
    this.fqcn = fqcn;
  }
  
  public void close() throws IOException {
    this.logger.close(this.fqcn);
  }
  
  public void flush() throws IOException {}
  
  public String toString() {
    return getClass().getSimpleName() + "[fqcn=" + this.fqcn + ", logger=" + this.logger + "]";
  }
  
  public void write(char[] cbuf) throws IOException {
    this.logger.put(this.fqcn, cbuf, 0, cbuf.length);
  }
  
  public void write(char[] cbuf, int off, int len) throws IOException {
    this.logger.put(this.fqcn, cbuf, off, len);
  }
  
  public void write(int c) throws IOException {
    this.logger.put(this.fqcn, (char)c);
  }
  
  public void write(String str) throws IOException {
    this.logger.put(this.fqcn, str, 0, str.length());
  }
  
  public void write(String str, int off, int len) throws IOException {
    this.logger.put(this.fqcn, str, off, len);
  }
}
