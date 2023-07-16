package org.apache.logging.log4j.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalBufferedReader;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerBufferedReader extends BufferedReader {
  private static final String FQCN = LoggerBufferedReader.class.getName();
  
  private final InternalBufferedReader reader;
  
  protected LoggerBufferedReader(Reader reader, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(reader);
    this.reader = new InternalBufferedReader(reader, logger, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  protected LoggerBufferedReader(Reader reader, int size, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(reader);
    this.reader = new InternalBufferedReader(reader, size, logger, (fqcn == null) ? FQCN : fqcn, level, marker);
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
  
  public String readLine() throws IOException {
    return this.reader.readLine();
  }
}
