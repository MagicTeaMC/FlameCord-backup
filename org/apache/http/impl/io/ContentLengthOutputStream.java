package org.apache.http.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.util.Args;

public class ContentLengthOutputStream extends OutputStream {
  private final SessionOutputBuffer out;
  
  private final long contentLength;
  
  private long total;
  
  private boolean closed;
  
  public ContentLengthOutputStream(SessionOutputBuffer out, long contentLength) {
    this.out = (SessionOutputBuffer)Args.notNull(out, "Session output buffer");
    this.contentLength = Args.notNegative(contentLength, "Content length");
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      this.closed = true;
      this.out.flush();
    } 
  }
  
  public void flush() throws IOException {
    this.out.flush();
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    if (this.closed)
      throw new IOException("Attempted write to closed stream."); 
    if (this.total < this.contentLength) {
      long max = this.contentLength - this.total;
      int chunk = len;
      if (chunk > max)
        chunk = (int)max; 
      this.out.write(b, off, chunk);
      this.total += chunk;
    } 
  }
  
  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }
  
  public void write(int b) throws IOException {
    if (this.closed)
      throw new IOException("Attempted write to closed stream."); 
    if (this.total < this.contentLength) {
      this.out.write(b);
      this.total++;
    } 
  }
}
