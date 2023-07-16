package org.apache.http.impl.io;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.io.EofSensor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Deprecated
public class SocketInputBuffer extends AbstractSessionInputBuffer implements EofSensor {
  private final Socket socket;
  
  private boolean eof;
  
  public SocketInputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
    Args.notNull(socket, "Socket");
    this.socket = socket;
    this.eof = false;
    int n = bufferSize;
    if (n < 0)
      n = socket.getReceiveBufferSize(); 
    if (n < 1024)
      n = 1024; 
    init(socket.getInputStream(), n, params);
  }
  
  protected int fillBuffer() throws IOException {
    int i = super.fillBuffer();
    this.eof = (i == -1);
    return i;
  }
  
  public boolean isDataAvailable(int timeout) throws IOException {
    boolean result = hasBufferedData();
    if (!result) {
      int oldtimeout = this.socket.getSoTimeout();
      try {
        this.socket.setSoTimeout(timeout);
        fillBuffer();
        result = hasBufferedData();
      } finally {
        this.socket.setSoTimeout(oldtimeout);
      } 
    } 
    return result;
  }
  
  public boolean isEof() {
    return this.eof;
  }
}
