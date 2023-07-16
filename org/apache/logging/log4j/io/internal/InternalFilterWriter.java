package org.apache.logging.log4j.io.internal;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.CharStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalFilterWriter extends FilterWriter {
  private final CharStreamLogger logger;
  
  private final String fqcn;
  
  public InternalFilterWriter(Writer out, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(out);
    this.logger = new CharStreamLogger(logger, level, marker);
    this.fqcn = fqcn;
  }
  
  public void close() throws IOException {
    this.out.close();
    this.logger.close(this.fqcn);
  }
  
  public void flush() throws IOException {
    this.out.flush();
  }
  
  public String toString() {
    return "{writer=" + this.out + '}';
  }
  
  public void write(char[] cbuf) throws IOException {
    this.out.write(cbuf);
    this.logger.put(this.fqcn, cbuf, 0, cbuf.length);
  }
  
  public void write(char[] cbuf, int off, int len) throws IOException {
    this.out.write(cbuf, off, len);
    this.logger.put(this.fqcn, cbuf, off, len);
  }
  
  public void write(int c) throws IOException {
    this.out.write(c);
    this.logger.put(this.fqcn, (char)c);
  }
  
  public void write(String str) throws IOException {
    this.out.write(str);
    this.logger.put(this.fqcn, str, 0, str.length());
  }
  
  public void write(String str, int off, int len) throws IOException {
    this.out.write(str, off, len);
    this.logger.put(this.fqcn, str, off, len);
  }
}
