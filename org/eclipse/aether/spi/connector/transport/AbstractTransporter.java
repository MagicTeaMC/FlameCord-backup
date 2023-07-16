package org.eclipse.aether.spi.connector.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.aether.transfer.TransferCancelledException;

public abstract class AbstractTransporter implements Transporter {
  private final AtomicBoolean closed = new AtomicBoolean();
  
  public void peek(PeekTask task) throws Exception {
    failIfClosed(task);
    implPeek(task);
  }
  
  protected abstract void implPeek(PeekTask paramPeekTask) throws Exception;
  
  public void get(GetTask task) throws Exception {
    failIfClosed(task);
    implGet(task);
  }
  
  protected abstract void implGet(GetTask paramGetTask) throws Exception;
  
  protected void utilGet(GetTask task, InputStream is, boolean close, long length, boolean resume) throws IOException, TransferCancelledException {
    OutputStream os = null;
    try {
      os = task.newOutputStream(resume);
      task.getListener().transportStarted(resume ? task.getResumeOffset() : 0L, length);
      copy(os, is, task.getListener());
      os.close();
      os = null;
      if (close) {
        is.close();
        is = null;
      } 
    } finally {
      try {
        if (os != null)
          os.close(); 
      } catch (IOException iOException) {
        try {
          if (close && is != null)
            is.close(); 
        } catch (IOException iOException1) {}
      } finally {
        try {
          if (close && is != null)
            is.close(); 
        } catch (IOException iOException) {}
      } 
    } 
  }
  
  public void put(PutTask task) throws Exception {
    failIfClosed(task);
    implPut(task);
  }
  
  protected abstract void implPut(PutTask paramPutTask) throws Exception;
  
  protected void utilPut(PutTask task, OutputStream os, boolean close) throws IOException, TransferCancelledException {
    InputStream is = null;
    try {
      task.getListener().transportStarted(0L, task.getDataLength());
      is = task.newInputStream();
      copy(os, is, task.getListener());
      if (close) {
        os.close();
      } else {
        os.flush();
      } 
      os = null;
      is.close();
      is = null;
    } finally {
      try {
        if (close && os != null)
          os.close(); 
      } catch (IOException iOException) {
        try {
          if (is != null)
            is.close(); 
        } catch (IOException iOException1) {}
      } finally {
        try {
          if (is != null)
            is.close(); 
        } catch (IOException iOException) {}
      } 
    } 
  }
  
  public void close() {
    if (this.closed.compareAndSet(false, true))
      implClose(); 
  }
  
  protected abstract void implClose();
  
  private void failIfClosed(TransportTask task) {
    if (this.closed.get())
      throw new IllegalStateException("transporter closed, cannot execute task " + task); 
  }
  
  private static void copy(OutputStream os, InputStream is, TransportListener listener) throws IOException, TransferCancelledException {
    ByteBuffer buffer = ByteBuffer.allocate(32768);
    byte[] array = buffer.array();
    int read;
    for (read = is.read(array); read >= 0; read = is.read(array)) {
      os.write(array, 0, read);
      buffer.rewind();
      buffer.limit(read);
      listener.transportProgressed(buffer);
    } 
  }
}
