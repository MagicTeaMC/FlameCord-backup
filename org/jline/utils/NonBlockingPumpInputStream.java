package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class NonBlockingPumpInputStream extends NonBlockingInputStream {
  private static final int DEFAULT_BUFFER_SIZE = 4096;
  
  private final ByteBuffer readBuffer;
  
  private final ByteBuffer writeBuffer;
  
  private final OutputStream output;
  
  private boolean closed;
  
  private IOException ioException;
  
  public NonBlockingPumpInputStream() {
    this(4096);
  }
  
  public NonBlockingPumpInputStream(int bufferSize) {
    byte[] buf = new byte[bufferSize];
    this.readBuffer = ByteBuffer.wrap(buf);
    this.writeBuffer = ByteBuffer.wrap(buf);
    this.output = new NbpOutputStream();
    this.readBuffer.limit(0);
  }
  
  public OutputStream getOutputStream() {
    return this.output;
  }
  
  private int wait(ByteBuffer buffer, long timeout) throws IOException {
    boolean isInfinite = (timeout <= 0L);
    long end = 0L;
    if (!isInfinite)
      end = System.currentTimeMillis() + timeout; 
    while (!this.closed && !buffer.hasRemaining() && (isInfinite || timeout > 0L)) {
      notifyAll();
      try {
        wait(timeout);
        checkIoException();
      } catch (InterruptedException e) {
        checkIoException();
        throw new InterruptedIOException();
      } 
      if (!isInfinite)
        timeout = end - System.currentTimeMillis(); 
    } 
    return buffer.hasRemaining() ? 
      0 : (
      this.closed ? 
      -1 : 
      -2);
  }
  
  private static boolean rewind(ByteBuffer buffer, ByteBuffer other) {
    if (buffer.position() > other.position())
      other.limit(buffer.position()); 
    if (buffer.position() == buffer.capacity()) {
      buffer.rewind();
      buffer.limit(other.position());
      return true;
    } 
    return false;
  }
  
  public synchronized int available() {
    int count = this.readBuffer.remaining();
    if (this.writeBuffer.position() < this.readBuffer.position())
      count += this.writeBuffer.position(); 
    return count;
  }
  
  public synchronized int read(long timeout, boolean isPeek) throws IOException {
    checkIoException();
    int res = wait(this.readBuffer, timeout);
    if (res >= 0)
      res = this.readBuffer.get() & 0xFF; 
    rewind(this.readBuffer, this.writeBuffer);
    return res;
  }
  
  public synchronized int readBuffered(byte[] b) throws IOException {
    checkIoException();
    int res = wait(this.readBuffer, 0L);
    if (res >= 0) {
      res = 0;
      while (res < b.length && this.readBuffer.hasRemaining())
        b[res++] = (byte)(this.readBuffer.get() & 0xFF); 
    } 
    rewind(this.readBuffer, this.writeBuffer);
    return res;
  }
  
  public synchronized void setIoException(IOException exception) {
    this.ioException = exception;
    notifyAll();
  }
  
  protected synchronized void checkIoException() throws IOException {
    if (this.ioException != null)
      throw this.ioException; 
  }
  
  synchronized void write(byte[] cbuf, int off, int len) throws IOException {
    while (len > 0) {
      if (wait(this.writeBuffer, 0L) == -1)
        throw new ClosedException(); 
      int count = Math.min(len, this.writeBuffer.remaining());
      this.writeBuffer.put(cbuf, off, count);
      off += count;
      len -= count;
      rewind(this.writeBuffer, this.readBuffer);
    } 
  }
  
  synchronized void flush() {
    if (this.readBuffer.hasRemaining())
      notifyAll(); 
  }
  
  public synchronized void close() throws IOException {
    this.closed = true;
    notifyAll();
  }
  
  private class NbpOutputStream extends OutputStream {
    private NbpOutputStream() {}
    
    public void write(int b) throws IOException {
      NonBlockingPumpInputStream.this.write(new byte[] { (byte)b }, 0, 1);
    }
    
    public void write(byte[] cbuf, int off, int len) throws IOException {
      NonBlockingPumpInputStream.this.write(cbuf, off, len);
    }
    
    public void flush() throws IOException {
      NonBlockingPumpInputStream.this.flush();
    }
    
    public void close() throws IOException {
      NonBlockingPumpInputStream.this.close();
    }
  }
}
