package org.apache.logging.log4j.io;

import java.nio.CharBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class CharStreamLogger {
  private final ExtendedLogger logger;
  
  private final Level level;
  
  private final Marker marker;
  
  private final StringBuilder msg = new StringBuilder();
  
  private boolean closed = false;
  
  public CharStreamLogger(ExtendedLogger logger, Level level, Marker marker) {
    this.logger = logger;
    this.level = (level == null) ? logger.getLevel() : level;
    this.marker = marker;
  }
  
  public void close(String fqcn) {
    synchronized (this.msg) {
      this.closed = true;
      logEnd(fqcn);
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
  
  public void put(String fqcn, char[] cbuf, int off, int len) {
    put(fqcn, CharBuffer.wrap(cbuf), off, len);
  }
  
  public void put(String fqcn, CharSequence str, int off, int len) {
    if (len >= 0) {
      synchronized (this.msg) {
        if (this.closed)
          return; 
        int start = off;
        int end = off + len;
        for (int pos = off; pos < end; pos++) {
          char c = str.charAt(pos);
          switch (c) {
            case '\n':
            case '\r':
              this.msg.append(str, start, pos);
              start = pos + 1;
              if (c == '\n')
                log(fqcn); 
              break;
          } 
        } 
        this.msg.append(str, start, end);
      } 
    } else {
      logEnd(fqcn);
    } 
  }
  
  public void put(String fqcn, int c) {
    if (c >= 0) {
      synchronized (this.msg) {
        if (this.closed)
          return; 
        switch (c) {
          case 10:
            log(fqcn);
            break;
          case 13:
            break;
          default:
            this.msg.append((char)c);
            break;
        } 
      } 
    } else {
      logEnd(fqcn);
    } 
  }
}
