package org.apache.logging.log4j.io.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalBufferedReader extends BufferedReader {
  private static final String FQCN = InternalBufferedReader.class.getName();
  
  public InternalBufferedReader(Reader reader, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(new InternalLoggerReader(reader, logger, (fqcn == null) ? FQCN : fqcn, level, marker));
  }
  
  public InternalBufferedReader(Reader reader, int size, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(new InternalLoggerReader(reader, logger, (fqcn == null) ? FQCN : fqcn, level, marker), size);
  }
  
  public void close() throws IOException {
    super.close();
  }
  
  public int read() throws IOException {
    return super.read();
  }
  
  public int read(char[] cbuf) throws IOException {
    return super.read(cbuf, 0, cbuf.length);
  }
  
  public int read(char[] cbuf, int off, int len) throws IOException {
    return super.read(cbuf, off, len);
  }
  
  public int read(CharBuffer target) throws IOException {
    int len = target.remaining();
    char[] cbuf = new char[len];
    int charsRead = read(cbuf, 0, len);
    if (charsRead > 0)
      target.put(cbuf, 0, charsRead); 
    return charsRead;
  }
  
  public String readLine() throws IOException {
    return super.readLine();
  }
}
