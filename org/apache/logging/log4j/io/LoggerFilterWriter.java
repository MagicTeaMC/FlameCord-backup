package org.apache.logging.log4j.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalFilterWriter;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerFilterWriter extends FilterWriter {
  private static final String FQCN = LoggerFilterWriter.class.getName();
  
  private final InternalFilterWriter logger;
  
  protected LoggerFilterWriter(Writer out, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(out);
    this.logger = new InternalFilterWriter(out, logger, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  public void close() throws IOException {
    this.logger.close();
  }
  
  public void flush() throws IOException {
    this.logger.flush();
  }
  
  public String toString() {
    return LoggerFilterWriter.class.getSimpleName() + this.logger.toString();
  }
  
  public void write(char[] cbuf) throws IOException {
    this.logger.write(cbuf);
  }
  
  public void write(char[] cbuf, int off, int len) throws IOException {
    this.logger.write(cbuf, off, len);
  }
  
  public void write(int c) throws IOException {
    this.logger.write(c);
  }
  
  public void write(String str) throws IOException {
    this.logger.write(str);
  }
  
  public void write(String str, int off, int len) throws IOException {
    this.logger.write(str, off, len);
  }
}
