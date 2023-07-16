package org.apache.logging.log4j.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalReader;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerReader extends FilterReader {
  private static final String FQCN = LoggerReader.class.getName();
  
  private final InternalReader reader;
  
  protected LoggerReader(Reader reader, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(reader);
    this.reader = new InternalReader(reader, logger, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  public void close() throws IOException {
    this.reader.close();
  }
  
  public int read() throws IOException {
    return this.reader.read();
  }
  
  public int read(char[] cbuf) throws IOException {
    return this.reader.read(cbuf);
  }
  
  public int read(char[] cbuf, int off, int len) throws IOException {
    return this.reader.read(cbuf, off, len);
  }
  
  public int read(CharBuffer target) throws IOException {
    return this.reader.read(target);
  }
  
  public String toString() {
    return LoggerReader.class.getSimpleName() + this.reader.toString();
  }
}
