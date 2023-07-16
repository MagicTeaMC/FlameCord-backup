package com.mysql.jdbc;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.protocol.SocketConnection;
import com.mysql.cj.protocol.SocketFactory;
import com.mysql.cj.protocol.StandardSocketFactory;
import java.io.IOException;

public class SocketFactoryWrapper extends StandardSocketFactory implements SocketFactory {
  SocketFactory socketFactory;
  
  public SocketFactoryWrapper(SocketFactory legacyFactory) {
    this.socketFactory = legacyFactory;
  }
  
  public <T extends java.io.Closeable> T connect(String hostname, int portNumber, PropertySet pset, int loginTimeout) throws IOException {
    this.rawSocket = this.socketFactory.connect(hostname, portNumber, pset.exposeAsProperties());
    return (T)this.rawSocket;
  }
  
  public <T extends java.io.Closeable> T performTlsHandshake(SocketConnection socketConnection, ServerSession serverSession, Log log) throws IOException {
    return (T)super.performTlsHandshake(socketConnection, serverSession, log);
  }
  
  public void beforeHandshake() throws IOException {
    this.socketFactory.beforeHandshake();
  }
  
  public void afterHandshake() throws IOException {
    this.socketFactory.afterHandshake();
  }
}
