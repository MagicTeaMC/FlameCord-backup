package org.jline.terminal.impl;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import org.jline.terminal.Attributes;
import org.jline.terminal.spi.Pty;
import org.jline.utils.NonBlockingInputStream;

public abstract class AbstractPty implements Pty {
  private Attributes current;
  
  public void setAttr(Attributes attr) throws IOException {
    this.current = new Attributes(attr);
    doSetAttr(attr);
  }
  
  public InputStream getSlaveInput() throws IOException {
    InputStream si = doGetSlaveInput();
    if (Boolean.parseBoolean(System.getProperty("org.jline.terminal.pty.nonBlockingReads", "true")))
      return (InputStream)new PtyInputStream(si); 
    return si;
  }
  
  protected void checkInterrupted() throws InterruptedIOException {
    if (Thread.interrupted())
      throw new InterruptedIOException(); 
  }
  
  protected abstract void doSetAttr(Attributes paramAttributes) throws IOException;
  
  protected abstract InputStream doGetSlaveInput() throws IOException;
  
  class PtyInputStream extends NonBlockingInputStream {
    final InputStream in;
    
    int c = 0;
    
    PtyInputStream(InputStream in) {
      this.in = in;
    }
    
    public int read(long timeout, boolean isPeek) throws IOException {
      AbstractPty.this.checkInterrupted();
      if (this.c != 0) {
        int r = this.c;
        if (!isPeek)
          this.c = 0; 
        return r;
      } 
      setNonBlocking();
      long start = System.currentTimeMillis();
      while (true) {
        int r = this.in.read();
        if (r >= 0) {
          if (isPeek)
            this.c = r; 
          return r;
        } 
        AbstractPty.this.checkInterrupted();
        long cur = System.currentTimeMillis();
        if (timeout > 0L && cur - start > timeout)
          return -2; 
      } 
    }
    
    public int readBuffered(byte[] b) throws IOException {
      return this.in.read(b);
    }
    
    private void setNonBlocking() {
      if (AbstractPty.this.current == null || AbstractPty.this
        .current.getControlChar(Attributes.ControlChar.VMIN) != 0 || AbstractPty.this
        .current.getControlChar(Attributes.ControlChar.VTIME) != 1)
        try {
          Attributes attr = AbstractPty.this.getAttr();
          attr.setControlChar(Attributes.ControlChar.VMIN, 0);
          attr.setControlChar(Attributes.ControlChar.VTIME, 1);
          AbstractPty.this.setAttr(attr);
        } catch (IOException e) {
          throw new IOError(e);
        }  
    }
  }
}
