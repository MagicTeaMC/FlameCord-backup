package com.mysql.cj.protocol.x;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

public class ContinuousInputStream extends FilterInputStream {
  private Queue<InputStream> inputStreams = new LinkedList<>();
  
  private boolean closed = false;
  
  protected ContinuousInputStream(InputStream in) {
    super(in);
  }
  
  public int available() throws IOException {
    ensureOpen();
    int available = super.available();
    if (available == 0 && nextInLine())
      return available(); 
    return available;
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      this.closed = true;
      super.close();
      for (InputStream is : this.inputStreams)
        is.close(); 
    } 
  }
  
  public int read() throws IOException {
    ensureOpen();
    int read = super.read();
    if (read >= 0)
      return read; 
    if (nextInLine())
      return read(); 
    return read;
  }
  
  public int read(byte[] b) throws IOException {
    ensureOpen();
    return read(b, 0, b.length);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    ensureOpen();
    int toRead = Math.min(len, available());
    int read = super.read(b, off, toRead);
    if (read > 0)
      return read; 
    if (nextInLine())
      return read(b, off, len); 
    return read;
  }
  
  protected boolean addInputStream(InputStream newIn) {
    return this.inputStreams.offer(newIn);
  }
  
  private boolean nextInLine() throws IOException {
    InputStream nextInputStream = this.inputStreams.poll();
    if (nextInputStream != null) {
      super.close();
      this.in = nextInputStream;
      return true;
    } 
    return false;
  }
  
  private void ensureOpen() throws IOException {
    if (this.closed)
      throw new IOException("Stream closed"); 
  }
}
