package com.mysql.cj.protocol;

import com.mysql.cj.Messages;
import com.mysql.cj.Session;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.log.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class NamedPipeSocketFactory implements SocketFactory {
  private static final int DEFAULT_TIMEOUT = 100;
  
  private Socket namedPipeSocket;
  
  class NamedPipeSocket extends Socket {
    private boolean isClosed = false;
    
    private RandomAccessFile namedPipeFile;
    
    NamedPipeSocket(String filePath, int timeout) throws IOException {
      if (filePath == null || filePath.length() == 0)
        throw new IOException(Messages.getString("NamedPipeSocketFactory.4")); 
      int timeoutCountdown = (timeout == 0) ? 100 : timeout;
      long startTime = System.currentTimeMillis();
      while (true) {
        try {
          this.namedPipeFile = new RandomAccessFile(filePath, "rw");
          break;
        } catch (FileNotFoundException e) {
          if (timeout == 0)
            throw new IOException("Named pipe busy error (ERROR_PIPE_BUSY).\nConsider setting a value for 'connectTimeout' or DriverManager.setLoginTimeout(int) to repeatedly try opening the named pipe before failing.", e); 
          if (System.currentTimeMillis() - startTime > timeoutCountdown)
            throw e; 
          try {
            TimeUnit.MILLISECONDS.sleep(10L);
          } catch (InterruptedException interruptedException) {
            throw new IOException(interruptedException);
          } 
        } 
      } 
    }
    
    public synchronized void close() throws IOException {
      this.namedPipeFile.close();
      this.isClosed = true;
    }
    
    public InputStream getInputStream() throws IOException {
      return new NamedPipeSocketFactory.RandomAccessFileInputStream(this.namedPipeFile);
    }
    
    public OutputStream getOutputStream() throws IOException {
      return new NamedPipeSocketFactory.RandomAccessFileOutputStream(this.namedPipeFile);
    }
    
    public boolean isClosed() {
      return this.isClosed;
    }
    
    public void shutdownInput() throws IOException {}
  }
  
  class RandomAccessFileInputStream extends InputStream {
    RandomAccessFile raFile;
    
    RandomAccessFileInputStream(RandomAccessFile file) {
      this.raFile = file;
    }
    
    public int available() throws IOException {
      return -1;
    }
    
    public void close() throws IOException {
      this.raFile.close();
    }
    
    public int read() throws IOException {
      return this.raFile.read();
    }
    
    public int read(byte[] b) throws IOException {
      return this.raFile.read(b);
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
      return this.raFile.read(b, off, len);
    }
  }
  
  class RandomAccessFileOutputStream extends OutputStream {
    RandomAccessFile raFile;
    
    RandomAccessFileOutputStream(RandomAccessFile file) {
      this.raFile = file;
    }
    
    public void close() throws IOException {
      this.raFile.close();
    }
    
    public void write(byte[] b) throws IOException {
      this.raFile.write(b);
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
      this.raFile.write(b, off, len);
    }
    
    public void write(int b) throws IOException {}
  }
  
  public <T extends java.io.Closeable> T performTlsHandshake(SocketConnection socketConnection, ServerSession serverSession) throws IOException {
    return performTlsHandshake(socketConnection, serverSession, null);
  }
  
  public <T extends java.io.Closeable> T performTlsHandshake(SocketConnection socketConnection, ServerSession serverSession, Log log) throws IOException {
    return (T)this.namedPipeSocket;
  }
  
  public <T extends java.io.Closeable> T connect(String host, int portNumber, PropertySet props, int loginTimeout) throws IOException {
    String namedPipePath = null;
    RuntimeProperty<String> path = props.getStringProperty(PropertyKey.PATH);
    if (path != null)
      namedPipePath = (String)path.getValue(); 
    if (namedPipePath == null) {
      namedPipePath = "\\\\.\\pipe\\MySQL";
    } else if (namedPipePath.length() == 0) {
      throw new SocketException(
          Messages.getString("NamedPipeSocketFactory.2") + PropertyKey.PATH.getCcAlias() + Messages.getString("NamedPipeSocketFactory.3"));
    } 
    int connectTimeout = ((Integer)props.getIntegerProperty(PropertyKey.connectTimeout.getKeyName()).getValue()).intValue();
    int timeout = (connectTimeout > 0 && loginTimeout > 0) ? Math.min(connectTimeout, loginTimeout) : (connectTimeout + loginTimeout);
    this.namedPipeSocket = new NamedPipeSocket(namedPipePath, timeout);
    return (T)this.namedPipeSocket;
  }
  
  public boolean isLocallyConnected(Session sess) {
    return true;
  }
}
