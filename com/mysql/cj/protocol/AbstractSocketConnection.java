package com.mysql.cj.protocol;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.UnableToConnectException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.util.Util;
import com.mysql.jdbc.SocketFactory;
import com.mysql.jdbc.SocketFactoryWrapper;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class AbstractSocketConnection implements SocketConnection {
  protected String host = null;
  
  protected int port = 3306;
  
  protected SocketFactory socketFactory = null;
  
  protected Socket mysqlSocket = null;
  
  protected FullReadInputStream mysqlInput = null;
  
  protected BufferedOutputStream mysqlOutput = null;
  
  protected ExceptionInterceptor exceptionInterceptor;
  
  protected PropertySet propertySet;
  
  public String getHost() {
    return this.host;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public Socket getMysqlSocket() {
    return this.mysqlSocket;
  }
  
  public FullReadInputStream getMysqlInput() throws IOException {
    if (this.mysqlInput != null)
      return this.mysqlInput; 
    throw new IOException(Messages.getString("SocketConnection.1"));
  }
  
  public void setMysqlInput(FullReadInputStream mysqlInput) {
    this.mysqlInput = mysqlInput;
  }
  
  public BufferedOutputStream getMysqlOutput() throws IOException {
    if (this.mysqlOutput != null)
      return this.mysqlOutput; 
    throw new IOException(Messages.getString("SocketConnection.1"));
  }
  
  public boolean isSSLEstablished() {
    return (ExportControlled.enabled() && ExportControlled.isSSLEstablished(getMysqlSocket()));
  }
  
  public SocketFactory getSocketFactory() {
    return this.socketFactory;
  }
  
  public void setSocketFactory(SocketFactory socketFactory) {
    this.socketFactory = socketFactory;
  }
  
  public void forceClose() {
    try {
      getNetworkResources().forceClose();
    } finally {
      this.mysqlSocket = null;
      this.mysqlInput = null;
      this.mysqlOutput = null;
    } 
  }
  
  public NetworkResources getNetworkResources() {
    return new NetworkResources(this.mysqlSocket, this.mysqlInput, this.mysqlOutput);
  }
  
  public ExceptionInterceptor getExceptionInterceptor() {
    return this.exceptionInterceptor;
  }
  
  public PropertySet getPropertySet() {
    return this.propertySet;
  }
  
  protected SocketFactory createSocketFactory(String socketFactoryClassName) {
    if (socketFactoryClassName == null)
      throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("SocketConnection.0"), getExceptionInterceptor()); 
    try {
      return (SocketFactory)Util.getInstance(SocketFactory.class, socketFactoryClassName, null, null, getExceptionInterceptor());
    } catch (WrongArgumentException e1) {
      if (e1.getCause() == null)
        try {
          return (SocketFactory)new SocketFactoryWrapper(
              (SocketFactory)Util.getInstance(SocketFactory.class, socketFactoryClassName, null, null, getExceptionInterceptor()));
        } catch (Exception e2) {
          throw e1;
        }  
      throw e1;
    } 
  }
}
