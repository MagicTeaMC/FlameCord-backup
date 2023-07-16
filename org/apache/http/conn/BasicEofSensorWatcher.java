package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.util.Args;

@Deprecated
public class BasicEofSensorWatcher implements EofSensorWatcher {
  protected final ManagedClientConnection managedConn;
  
  protected final boolean attemptReuse;
  
  public BasicEofSensorWatcher(ManagedClientConnection conn, boolean reuse) {
    Args.notNull(conn, "Connection");
    this.managedConn = conn;
    this.attemptReuse = reuse;
  }
  
  public boolean eofDetected(InputStream wrapped) throws IOException {
    try {
      if (this.attemptReuse) {
        wrapped.close();
        this.managedConn.markReusable();
      } 
    } finally {
      this.managedConn.releaseConnection();
    } 
    return false;
  }
  
  public boolean streamClosed(InputStream wrapped) throws IOException {
    try {
      if (this.attemptReuse) {
        wrapped.close();
        this.managedConn.markReusable();
      } 
    } finally {
      this.managedConn.releaseConnection();
    } 
    return false;
  }
  
  public boolean streamAbort(InputStream wrapped) throws IOException {
    this.managedConn.abortConnection();
    return false;
  }
}
