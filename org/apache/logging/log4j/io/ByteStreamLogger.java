package org.apache.logging.log4j.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class ByteStreamLogger {
  private static final int BUFFER_SIZE = 1024;
  
  private final ExtendedLogger logger;
  
  private final Level level;
  
  private final Marker marker;
  
  private final InputStreamReader reader;
  
  private class ByteBufferInputStream extends InputStream {
    private ByteBufferInputStream() {}
    
    public int read() throws IOException {
      ByteStreamLogger.this.buf.flip();
      int result = -1;
      if (ByteStreamLogger.this.buf.limit() > 0)
        result = ByteStreamLogger.this.buf.get() & 0xFF; 
      ByteStreamLogger.this.buf.compact();
      return result;
    }
    
    public int read(byte[] bytes, int off, int len) throws IOException {
      ByteStreamLogger.this.buf.flip();
      int result = -1;
      if (ByteStreamLogger.this.buf.limit() > 0) {
        result = Math.min(len, ByteStreamLogger.this.buf.limit());
        ByteStreamLogger.this.buf.get(bytes, off, result);
      } 
      ByteStreamLogger.this.buf.compact();
      return result;
    }
  }
  
  private final char[] msgBuf = new char[1024];
  
  private final StringBuilder msg = new StringBuilder();
  
  private boolean closed;
  
  private final ByteBuffer buf = ByteBuffer.allocate(1024);
  
  public ByteStreamLogger(ExtendedLogger logger, Level level, Marker marker, Charset charset) {
    this.logger = logger;
    this.level = (level == null) ? logger.getLevel() : level;
    this.marker = marker;
    this
      .reader = new InputStreamReader(new ByteBufferInputStream(), (charset == null) ? Charset.defaultCharset() : charset);
  }
  
  public void close(String fqcn) {
    synchronized (this.msg) {
      this.closed = true;
      logEnd(fqcn);
    } 
  }
  
  private void extractMessages(String fqcn) throws IOException {
    if (this.closed)
      return; 
    int read = this.reader.read(this.msgBuf);
    while (read > 0) {
      int off = 0;
      for (int pos = 0; pos < read; pos++) {
        switch (this.msgBuf[pos]) {
          case '\r':
            this.msg.append(this.msgBuf, off, pos - off);
            off = pos + 1;
            break;
          case '\n':
            this.msg.append(this.msgBuf, off, pos - off);
            off = pos + 1;
            log(fqcn);
            break;
        } 
      } 
      this.msg.append(this.msgBuf, off, read - off);
      read = this.reader.read(this.msgBuf);
    } 
  }
  
  private void log(String fqcn) {
    this.logger.logIfEnabled(fqcn, this.level, this.marker, this.msg.toString());
    this.msg.setLength(0);
  }
  
  private void logEnd(String fqcn) {
    if (this.msg.length() > 0)
      log(fqcn); 
  }
  
  public void put(String fqcn, byte[] b, int off, int len) throws IOException {
    int curOff = off;
    int curLen = len;
    if (curLen >= 0) {
      synchronized (this.msg) {
        while (curLen > this.buf.remaining()) {
          int remaining = this.buf.remaining();
          this.buf.put(b, curOff, remaining);
          curLen -= remaining;
          curOff += remaining;
          extractMessages(fqcn);
        } 
        this.buf.put(b, curOff, curLen);
        extractMessages(fqcn);
      } 
    } else {
      logEnd(fqcn);
    } 
  }
  
  public void put(String fqcn, int b) throws IOException {
    if (b >= 0) {
      synchronized (this.msg) {
        this.buf.put((byte)(b & 0xFF));
        extractMessages(fqcn);
      } 
    } else {
      logEnd(fqcn);
    } 
  }
}
