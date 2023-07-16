package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.ConnectionClosedException;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.Args;

public class ContentLengthInputStream extends InputStream {
  private static final int BUFFER_SIZE = 2048;
  
  private final long contentLength;
  
  private long pos = 0L;
  
  private boolean closed = false;
  
  private SessionInputBuffer in = null;
  
  public ContentLengthInputStream(SessionInputBuffer in, long contentLength) {
    this.in = (SessionInputBuffer)Args.notNull(in, "Session input buffer");
    this.contentLength = Args.notNegative(contentLength, "Content length");
  }
  
  public void close() throws IOException {
    if (!this.closed)
      try {
        if (this.pos < this.contentLength) {
          byte[] buffer = new byte[2048];
          while (read(buffer) >= 0);
        } 
      } finally {
        this.closed = true;
      }  
  }
  
  public int available() throws IOException {
    if (this.in instanceof BufferInfo) {
      int len = ((BufferInfo)this.in).length();
      return Math.min(len, (int)(this.contentLength - this.pos));
    } 
    return 0;
  }
  
  public int read() throws IOException {
    if (this.closed)
      throw new IOException("Attempted read from closed stream."); 
    if (this.pos >= this.contentLength)
      return -1; 
    int b = this.in.read();
    if (b == -1) {
      if (this.pos < this.contentLength)
        throw new ConnectionClosedException("Premature end of Content-Length delimited message body (expected: %,d; received: %,d)", new Object[] { Long.valueOf(this.contentLength), Long.valueOf(this.pos) }); 
    } else {
      this.pos++;
    } 
    return b;
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    if (this.closed)
      throw new IOException("Attempted read from closed stream."); 
    if (this.pos >= this.contentLength)
      return -1; 
    int chunk = len;
    if (this.pos + len > this.contentLength)
      chunk = (int)(this.contentLength - this.pos); 
    int readLen = this.in.read(b, off, chunk);
    if (readLen == -1 && this.pos < this.contentLength)
      throw new ConnectionClosedException("Premature end of Content-Length delimited message body (expected: %,d; received: %,d)", new Object[] { Long.valueOf(this.contentLength), Long.valueOf(this.pos) }); 
    if (readLen > 0)
      this.pos += readLen; 
    return readLen;
  }
  
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }
  
  public long skip(long n) throws IOException {
    if (n <= 0L)
      return 0L; 
    byte[] buffer = new byte[2048];
    long remaining = Math.min(n, this.contentLength - this.pos);
    long count = 0L;
    while (remaining > 0L) {
      int readLen = read(buffer, 0, (int)Math.min(2048L, remaining));
      if (readLen == -1)
        break; 
      count += readLen;
      remaining -= readLen;
    } 
    return count;
  }
}
