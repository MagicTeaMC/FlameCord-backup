package com.mysql.cj.protocol.x;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfinedInputStream extends FilterInputStream {
  private int limit = 0;
  
  private int consumed = 0;
  
  private boolean closed = false;
  
  protected ConfinedInputStream(InputStream in) {
    this(in, 0);
  }
  
  protected ConfinedInputStream(InputStream in, int lim) {
    super(in);
    this.limit = lim;
    this.consumed = 0;
  }
  
  public int available() throws IOException {
    ensureOpen();
    return this.limit - this.consumed;
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      dumpLeftovers();
      this.closed = true;
    } 
  }
  
  public int read() throws IOException {
    ensureOpen();
    int read = super.read();
    if (read >= 0)
      this.consumed++; 
    return read;
  }
  
  public int read(byte[] b) throws IOException {
    ensureOpen();
    return read(b, 0, b.length);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    ensureOpen();
    if (this.consumed >= this.limit)
      return -1; 
    int toRead = Math.min(len, available());
    int read = super.read(b, off, toRead);
    if (read > 0)
      this.consumed += read; 
    return read;
  }
  
  public int resetLimit(int len) {
    int remaining = 0;
    try {
      remaining = available();
    } catch (IOException iOException) {}
    this.limit = len;
    this.consumed = 0;
    return remaining;
  }
  
  protected long dumpLeftovers() throws IOException {
    long skipped = skip(available());
    this.consumed = (int)(this.consumed + skipped);
    return skipped;
  }
  
  private void ensureOpen() throws IOException {
    if (this.closed)
      throw new IOException("Stream closed"); 
  }
}
