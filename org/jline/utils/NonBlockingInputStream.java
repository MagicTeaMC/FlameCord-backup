package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;

public abstract class NonBlockingInputStream extends InputStream {
  public static final int EOF = -1;
  
  public static final int READ_EXPIRED = -2;
  
  public int read() throws IOException {
    return read(0L, false);
  }
  
  public int peek(long timeout) throws IOException {
    return read(timeout, true);
  }
  
  public int read(long timeout) throws IOException {
    return read(timeout, false);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    if (b == null)
      throw new NullPointerException(); 
    if (off < 0 || len < 0 || len > b.length - off)
      throw new IndexOutOfBoundsException(); 
    if (len == 0)
      return 0; 
    int c = read();
    if (c == -1)
      return -1; 
    b[off] = (byte)c;
    return 1;
  }
  
  public int readBuffered(byte[] b) throws IOException {
    if (b == null)
      throw new NullPointerException(); 
    if (b.length == 0)
      return 0; 
    return super.read(b, 0, b.length);
  }
  
  public void shutdown() {}
  
  public abstract int read(long paramLong, boolean paramBoolean) throws IOException;
}
