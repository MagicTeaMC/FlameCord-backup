package org.apache.logging.log4j.io.internal;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.ByteStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalFilterOutputStream extends FilterOutputStream {
  private final ByteStreamLogger logger;
  
  private final String fqcn;
  
  public InternalFilterOutputStream(OutputStream out, Charset charset, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(out);
    this.logger = new ByteStreamLogger(logger, level, marker, charset);
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
    return "{stream=" + this.out + '}';
  }
  
  public void write(byte[] b) throws IOException {
    this.out.write(b);
    this.logger.put(this.fqcn, b, 0, b.length);
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    this.out.write(b, off, len);
    this.logger.put(this.fqcn, b, off, len);
  }
  
  public void write(int b) throws IOException {
    this.out.write(b);
    this.logger.put(this.fqcn, (byte)(b & 0xFF));
  }
}
