package com.mysql.cj.protocol.a;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.FeatureNotAvailableException;
import com.mysql.cj.exceptions.SSLParamsException;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.AbstractSocketConnection;
import com.mysql.cj.protocol.FullReadInputStream;
import com.mysql.cj.protocol.PacketSentTimeHolder;
import com.mysql.cj.protocol.ReadAheadInputStream;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.protocol.SocketConnection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class NativeSocketConnection extends AbstractSocketConnection implements SocketConnection {
  public void connect(String hostName, int portNumber, PropertySet propSet, ExceptionInterceptor excInterceptor, Log log, int loginTimeout) {
    try {
      InputStream rawInputStream;
      this.port = portNumber;
      this.host = hostName;
      this.propertySet = propSet;
      this.exceptionInterceptor = excInterceptor;
      this.socketFactory = createSocketFactory(propSet.getStringProperty(PropertyKey.socketFactory).getStringValue());
      this.mysqlSocket = (Socket)this.socketFactory.connect(this.host, this.port, propSet, loginTimeout);
      int socketTimeout = ((Integer)propSet.getIntegerProperty(PropertyKey.socketTimeout).getValue()).intValue();
      if (socketTimeout != 0)
        try {
          this.mysqlSocket.setSoTimeout(socketTimeout);
        } catch (Exception exception) {} 
      this.socketFactory.beforeHandshake();
      if (((Boolean)propSet.getBooleanProperty(PropertyKey.useReadAheadInput).getValue()).booleanValue()) {
        ReadAheadInputStream readAheadInputStream = new ReadAheadInputStream(this.mysqlSocket.getInputStream(), 16384, ((Boolean)propSet.getBooleanProperty(PropertyKey.traceProtocol).getValue()).booleanValue(), log);
      } else if (((Boolean)propSet.getBooleanProperty(PropertyKey.useUnbufferedInput).getValue()).booleanValue()) {
        rawInputStream = this.mysqlSocket.getInputStream();
      } else {
        rawInputStream = new BufferedInputStream(this.mysqlSocket.getInputStream(), 16384);
      } 
      this.mysqlInput = new FullReadInputStream(rawInputStream);
      this.mysqlOutput = new BufferedOutputStream(this.mysqlSocket.getOutputStream(), 16384);
    } catch (IOException ioEx) {
      throw ExceptionFactory.createCommunicationsException(propSet, null, new PacketSentTimeHolder() {
          
          },  null, ioEx, 
          getExceptionInterceptor());
    } 
  }
  
  public void performTlsHandshake(ServerSession serverSession) throws SSLParamsException, FeatureNotAvailableException, IOException {
    performTlsHandshake(serverSession, (Log)null);
  }
  
  public void performTlsHandshake(ServerSession serverSession, Log log) throws SSLParamsException, FeatureNotAvailableException, IOException {
    this.mysqlSocket = (Socket)this.socketFactory.performTlsHandshake(this, serverSession, log);
    this
      
      .mysqlInput = new FullReadInputStream(((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useUnbufferedInput).getValue()).booleanValue() ? getMysqlSocket().getInputStream() : new BufferedInputStream(getMysqlSocket().getInputStream(), 16384));
    this.mysqlOutput = new BufferedOutputStream(getMysqlSocket().getOutputStream(), 16384);
    this.mysqlOutput.flush();
  }
}
