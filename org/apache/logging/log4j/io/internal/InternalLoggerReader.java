package org.apache.logging.log4j.io.internal;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.CharStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalLoggerReader extends FilterReader {
  private final CharStreamLogger logger;
  
  private final String fqcn;
  
  public InternalLoggerReader(Reader reader, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(reader);
    this.logger = new CharStreamLogger(logger, level, marker);
    this.fqcn = fqcn;
  }
  
  public void close() throws IOException {
    super.close();
    this.logger.close(this.fqcn);
  }
  
  public int read() throws IOException {
    int c = super.read();
    this.logger.put(this.fqcn, c);
    return c;
  }
  
  public int read(char[] cbuf) throws IOException {
    return read(cbuf, 0, cbuf.length);
  }
  
  public int read(char[] cbuf, int off, int len) throws IOException {
    int charsRead = super.read(cbuf, off, len);
    this.logger.put(this.fqcn, cbuf, off, charsRead);
    return charsRead;
  }
  
  public int read(CharBuffer target) throws IOException {
    int len = target.remaining();
    char[] cbuf = new char[len];
    int charsRead = read(cbuf, 0, len);
    if (charsRead > 0)
      target.put(cbuf, 0, charsRead); 
    return charsRead;
  }
  
  public String toString() {
    return "{stream=" + this.in + '}';
  }
}
