package org.jline.terminal.impl;

import java.io.IOException;
import java.io.Writer;

public abstract class AbstractWindowsConsoleWriter extends Writer {
  protected abstract void writeConsole(char[] paramArrayOfchar, int paramInt) throws IOException;
  
  public void write(char[] cbuf, int off, int len) throws IOException {
    char[] text = cbuf;
    if (off != 0) {
      text = new char[len];
      System.arraycopy(cbuf, off, text, 0, len);
    } 
    synchronized (this.lock) {
      writeConsole(text, len);
    } 
  }
  
  public void flush() {}
  
  public void close() {}
}
