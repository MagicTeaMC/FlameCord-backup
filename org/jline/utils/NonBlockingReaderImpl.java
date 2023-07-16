package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;

public class NonBlockingReaderImpl extends NonBlockingReader {
  public static final int READ_EXPIRED = -2;
  
  private Reader in;
  
  private int ch = -2;
  
  private String name;
  
  private boolean threadIsReading = false;
  
  private IOException exception = null;
  
  private long threadDelay = 60000L;
  
  private Thread thread;
  
  public NonBlockingReaderImpl(String name, Reader in) {
    this.in = in;
    this.name = name;
  }
  
  private synchronized void startReadingThreadIfNeeded() {
    if (this.thread == null) {
      this.thread = new Thread(this::run);
      this.thread.setName(this.name + " non blocking reader thread");
      this.thread.setDaemon(true);
      this.thread.start();
    } 
  }
  
  public synchronized void shutdown() {
    if (this.thread != null)
      notify(); 
  }
  
  public void close() throws IOException {
    this.in.close();
    shutdown();
  }
  
  public synchronized boolean ready() throws IOException {
    return (this.ch >= 0 || this.in.ready());
  }
  
  public int readBuffered(char[] b) throws IOException {
    if (b == null)
      throw new NullPointerException(); 
    if (b.length == 0)
      return 0; 
    if (this.exception != null) {
      assert this.ch == -2;
      IOException toBeThrown = this.exception;
      this.exception = null;
      throw toBeThrown;
    } 
    if (this.ch >= -1) {
      b[0] = (char)this.ch;
      this.ch = -2;
      return 1;
    } 
    if (!this.threadIsReading)
      return this.in.read(b); 
    int c = read(-1L, false);
    if (c >= 0) {
      b[0] = (char)c;
      return 1;
    } 
    return -1;
  }
  
  protected synchronized int read(long timeout, boolean isPeek) throws IOException {
    if (this.exception != null) {
      assert this.ch == -2;
      IOException toBeThrown = this.exception;
      if (!isPeek)
        this.exception = null; 
      throw toBeThrown;
    } 
    if (this.ch >= -1) {
      assert this.exception == null;
    } else if (!isPeek && timeout <= 0L && !this.threadIsReading) {
      this.ch = this.in.read();
    } else {
      if (!this.threadIsReading) {
        this.threadIsReading = true;
        startReadingThreadIfNeeded();
        notifyAll();
      } 
      boolean isInfinite = (timeout <= 0L);
      while (isInfinite || timeout > 0L) {
        long start = System.currentTimeMillis();
        try {
          if (Thread.interrupted())
            throw new InterruptedException(); 
          wait(timeout);
        } catch (InterruptedException e) {
          this.exception = (IOException)(new InterruptedIOException()).initCause(e);
        } 
        if (this.exception != null) {
          assert this.ch == -2;
          IOException toBeThrown = this.exception;
          if (!isPeek)
            this.exception = null; 
          throw toBeThrown;
        } 
        if (this.ch >= -1) {
          assert this.exception == null;
          break;
        } 
        if (!isInfinite)
          timeout -= System.currentTimeMillis() - start; 
      } 
    } 
    int ret = this.ch;
    if (!isPeek)
      this.ch = -2; 
    return ret;
  }
  
  private void run() {
    Log.debug(new Object[] { "NonBlockingReader start" });
    try {
      while (true) {
        synchronized (this) {
          boolean needToRead = this.threadIsReading;
          try {
            if (!needToRead)
              wait(this.threadDelay); 
          } catch (InterruptedException interruptedException) {}
          needToRead = this.threadIsReading;
          if (!needToRead)
            return; 
        } 
        int charRead = -2;
        IOException failure = null;
        try {
          charRead = this.in.read();
        } catch (IOException e) {
          failure = e;
        } 
        synchronized (this) {
          this.exception = failure;
          this.ch = charRead;
          this.threadIsReading = false;
          notify();
        } 
      } 
    } catch (Throwable t) {
      Log.warn(new Object[] { "Error in NonBlockingReader thread", t });
    } finally {
      Log.debug(new Object[] { "NonBlockingReader shutdown" });
      synchronized (this) {
        this.thread = null;
        this.threadIsReading = false;
      } 
    } 
  }
  
  public synchronized void clear() throws IOException {
    while (ready())
      read(); 
  }
}
