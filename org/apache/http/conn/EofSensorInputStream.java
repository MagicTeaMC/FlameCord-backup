package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.util.Args;

public class EofSensorInputStream extends InputStream implements ConnectionReleaseTrigger {
  protected InputStream wrappedStream;
  
  private boolean selfClosed;
  
  private final EofSensorWatcher eofWatcher;
  
  public EofSensorInputStream(InputStream in, EofSensorWatcher watcher) {
    Args.notNull(in, "Wrapped stream");
    this.wrappedStream = in;
    this.selfClosed = false;
    this.eofWatcher = watcher;
  }
  
  boolean isSelfClosed() {
    return this.selfClosed;
  }
  
  InputStream getWrappedStream() {
    return this.wrappedStream;
  }
  
  protected boolean isReadAllowed() throws IOException {
    if (this.selfClosed)
      throw new IOException("Attempted read on closed stream."); 
    return (this.wrappedStream != null);
  }
  
  public int read() throws IOException {
    int readLen = -1;
    if (isReadAllowed())
      try {
        readLen = this.wrappedStream.read();
        checkEOF(readLen);
      } catch (IOException ex) {
        checkAbort();
        throw ex;
      }  
    return readLen;
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    int readLen = -1;
    if (isReadAllowed())
      try {
        readLen = this.wrappedStream.read(b, off, len);
        checkEOF(readLen);
      } catch (IOException ex) {
        checkAbort();
        throw ex;
      }  
    return readLen;
  }
  
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }
  
  public int available() throws IOException {
    int a = 0;
    if (isReadAllowed())
      try {
        a = this.wrappedStream.available();
      } catch (IOException ex) {
        checkAbort();
        throw ex;
      }  
    return a;
  }
  
  public void close() throws IOException {
    this.selfClosed = true;
    checkClose();
  }
  
  protected void checkEOF(int eof) throws IOException {
    InputStream toCheckStream = this.wrappedStream;
    if (toCheckStream != null && eof < 0)
      try {
        boolean scws = true;
        if (this.eofWatcher != null)
          scws = this.eofWatcher.eofDetected(toCheckStream); 
        if (scws)
          toCheckStream.close(); 
      } finally {
        this.wrappedStream = null;
      }  
  }
  
  protected void checkClose() throws IOException {
    InputStream toCloseStream = this.wrappedStream;
    if (toCloseStream != null)
      try {
        boolean scws = true;
        if (this.eofWatcher != null)
          scws = this.eofWatcher.streamClosed(toCloseStream); 
        if (scws)
          toCloseStream.close(); 
      } finally {
        this.wrappedStream = null;
      }  
  }
  
  protected void checkAbort() throws IOException {
    InputStream toAbortStream = this.wrappedStream;
    if (toAbortStream != null)
      try {
        boolean scws = true;
        if (this.eofWatcher != null)
          scws = this.eofWatcher.streamAbort(toAbortStream); 
        if (scws)
          toAbortStream.close(); 
      } finally {
        this.wrappedStream = null;
      }  
  }
  
  public void releaseConnection() throws IOException {
    close();
  }
  
  public void abortConnection() throws IOException {
    this.selfClosed = true;
    checkAbort();
  }
}
