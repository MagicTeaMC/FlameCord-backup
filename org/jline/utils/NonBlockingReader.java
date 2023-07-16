package org.jline.utils;

import java.io.IOException;
import java.io.Reader;

public abstract class NonBlockingReader extends Reader {
  public static final int EOF = -1;
  
  public static final int READ_EXPIRED = -2;
  
  public void shutdown() {}
  
  public int read() throws IOException {
    return read(0L, false);
  }
  
  public int peek(long timeout) throws IOException {
    return read(timeout, true);
  }
  
  public int read(long timeout) throws IOException {
    return read(timeout, false);
  }
  
  public int read(char[] b, int off, int len) throws IOException {
    if (b == null)
      throw new NullPointerException(); 
    if (off < 0 || len < 0 || len > b.length - off)
      throw new IndexOutOfBoundsException(); 
    if (len == 0)
      return 0; 
    int c = read(0L);
    if (c == -1)
      return -1; 
    b[off] = (char)c;
    return 1;
  }
  
  public abstract int readBuffered(char[] paramArrayOfchar) throws IOException;
  
  public int available() {
    return 0;
  }
  
  protected abstract int read(long paramLong, boolean paramBoolean) throws IOException;
}
