package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NonBlockingPumpReader extends NonBlockingReader {
  private static final int DEFAULT_BUFFER_SIZE = 4096;
  
  private final char[] buffer;
  
  private int read;
  
  private int write;
  
  private int count;
  
  final ReentrantLock lock;
  
  private final Condition notEmpty;
  
  private final Condition notFull;
  
  private final Writer writer;
  
  private boolean closed;
  
  public NonBlockingPumpReader() {
    this(4096);
  }
  
  public NonBlockingPumpReader(int bufferSize) {
    this.buffer = new char[bufferSize];
    this.writer = new NbpWriter();
    this.lock = new ReentrantLock();
    this.notEmpty = this.lock.newCondition();
    this.notFull = this.lock.newCondition();
  }
  
  public Writer getWriter() {
    return this.writer;
  }
  
  public boolean ready() {
    return (available() > 0);
  }
  
  public int available() {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return this.count;
    } finally {
      lock.unlock();
    } 
  }
  
  protected int read(long timeout, boolean isPeek) throws IOException {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      if (!this.closed && this.count == 0)
        try {
          if (timeout > 0L) {
            this.notEmpty.await(timeout, TimeUnit.MILLISECONDS);
          } else {
            this.notEmpty.await();
          } 
        } catch (InterruptedException e) {
          throw (IOException)(new InterruptedIOException()).initCause(e);
        }  
      if (this.closed)
        return -1; 
      if (this.count == 0)
        return -2; 
      if (isPeek)
        return this.buffer[this.read]; 
      int res = this.buffer[this.read];
      if (++this.read == this.buffer.length)
        this.read = 0; 
      this.count--;
      this.notFull.signal();
      return res;
    } finally {
      lock.unlock();
    } 
  }
  
  public int readBuffered(char[] b) throws IOException {
    if (b == null)
      throw new NullPointerException(); 
    if (b.length == 0)
      return 0; 
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      if (!this.closed && this.count == 0)
        try {
          this.notEmpty.await();
        } catch (InterruptedException e) {
          throw (IOException)(new InterruptedIOException()).initCause(e);
        }  
      if (this.closed)
        return -1; 
      if (this.count == 0)
        return -2; 
      int r = Math.min(b.length, this.count);
      int i;
      for (i = 0; i < r; i++) {
        b[i] = this.buffer[this.read++];
        if (this.read == this.buffer.length)
          this.read = 0; 
      } 
      this.count -= r;
      this.notFull.signal();
      i = r;
      return i;
    } finally {
      lock.unlock();
    } 
  }
  
  void write(char[] cbuf, int off, int len) throws IOException {
    if (len > 0) {
      ReentrantLock lock = this.lock;
      lock.lock();
      try {
        while (len > 0) {
          if (!this.closed && this.count == this.buffer.length)
            try {
              this.notFull.await();
            } catch (InterruptedException e) {
              throw (IOException)(new InterruptedIOException()).initCause(e);
            }  
          if (this.closed)
            throw new IOException("Closed"); 
          while (len > 0 && this.count < this.buffer.length) {
            this.buffer[this.write++] = cbuf[off++];
            this.count++;
            len--;
            if (this.write == this.buffer.length)
              this.write = 0; 
          } 
          this.notEmpty.signal();
        } 
      } finally {
        lock.unlock();
      } 
    } 
  }
  
  public void close() throws IOException {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      this.closed = true;
      this.notEmpty.signalAll();
      this.notFull.signalAll();
    } finally {
      lock.unlock();
    } 
  }
  
  private class NbpWriter extends Writer {
    private NbpWriter() {}
    
    public void write(char[] cbuf, int off, int len) throws IOException {
      NonBlockingPumpReader.this.write(cbuf, off, len);
    }
    
    public void flush() throws IOException {}
    
    public void close() throws IOException {
      NonBlockingPumpReader.this.close();
    }
  }
}
