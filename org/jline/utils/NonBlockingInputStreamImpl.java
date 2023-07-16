package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class NonBlockingInputStreamImpl extends NonBlockingInputStream {
  private InputStream in;
  
  private int b = -2;
  
  private String name;
  
  private boolean threadIsReading = false;
  
  private IOException exception = null;
  
  private long threadDelay = 60000L;
  
  private Thread thread;
  
  public NonBlockingInputStreamImpl(String name, InputStream in) {
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
  
  public synchronized int read(long timeout, boolean isPeek) throws IOException {
    if (this.exception != null) {
      assert this.b == -2;
      IOException toBeThrown = this.exception;
      if (!isPeek)
        this.exception = null; 
      throw toBeThrown;
    } 
    if (this.b >= -1) {
      assert this.exception == null;
    } else if (!isPeek && timeout <= 0L && !this.threadIsReading) {
      this.b = this.in.read();
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
          assert this.b == -2;
          IOException toBeThrown = this.exception;
          if (!isPeek)
            this.exception = null; 
          throw toBeThrown;
        } 
        if (this.b >= -1) {
          assert this.exception == null;
          break;
        } 
        if (!isInfinite)
          timeout -= System.currentTimeMillis() - start; 
      } 
    } 
    int ret = this.b;
    if (!isPeek)
      this.b = -2; 
    return ret;
  }
  
  private void run() {
    Log.debug(new Object[] { "NonBlockingInputStream start" });
    try {
      int byteRead;
      do {
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
        byteRead = -2;
        IOException failure = null;
        try {
          byteRead = this.in.read();
        } catch (IOException e) {
          failure = e;
        } 
        synchronized (this) {
          this.exception = failure;
          this.b = byteRead;
          this.threadIsReading = false;
          notify();
        } 
      } while (byteRead >= 0);
      return;
    } catch (Throwable t) {
      Log.warn(new Object[] { "Error in NonBlockingInputStream thread", t });
    } finally {
      Log.debug(new Object[] { "NonBlockingInputStream shutdown" });
      synchronized (this) {
        this.thread = null;
        this.threadIsReading = false;
      } 
    } 
  }
}
