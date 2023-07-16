package org.apache.logging.log4j.io;

import java.io.IOException;
import java.io.Writer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalWriter;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerWriter extends Writer {
  private static final String FQCN = LoggerWriter.class.getName();
  
  private final InternalWriter writer;
  
  protected LoggerWriter(ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    this.writer = new InternalWriter(logger, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  public void close() throws IOException {
    this.writer.close();
  }
  
  public void flush() throws IOException {}
  
  public String toString() {
    return getClass().getSimpleName() + "[fqcn=" + this.writer.toString();
  }
  
  public void write(char[] cbuf) throws IOException {
    this.writer.write(cbuf);
  }
  
  public void write(char[] cbuf, int off, int len) throws IOException {
    this.writer.write(cbuf, off, len);
  }
  
  public void write(int c) throws IOException {
    this.writer.write(c);
  }
  
  public void write(String str) throws IOException {
    this.writer.write(str);
  }
  
  public void write(String str, int off, int len) throws IOException {
    this.writer.write(str, off, len);
  }
}
