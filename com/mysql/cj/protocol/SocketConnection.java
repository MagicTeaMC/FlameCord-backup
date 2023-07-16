package com.mysql.cj.protocol;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.FeatureNotAvailableException;
import com.mysql.cj.exceptions.SSLParamsException;
import com.mysql.cj.log.Log;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public interface SocketConnection {
  void connect(String paramString, int paramInt1, PropertySet paramPropertySet, ExceptionInterceptor paramExceptionInterceptor, Log paramLog, int paramInt2);
  
  void performTlsHandshake(ServerSession paramServerSession) throws SSLParamsException, FeatureNotAvailableException, IOException;
  
  default void performTlsHandshake(ServerSession serverSession, Log log) throws SSLParamsException, FeatureNotAvailableException, IOException {
    performTlsHandshake(serverSession);
  }
  
  void forceClose();
  
  NetworkResources getNetworkResources();
  
  String getHost();
  
  int getPort();
  
  Socket getMysqlSocket() throws IOException;
  
  FullReadInputStream getMysqlInput() throws IOException;
  
  void setMysqlInput(FullReadInputStream paramFullReadInputStream);
  
  BufferedOutputStream getMysqlOutput() throws IOException;
  
  boolean isSSLEstablished();
  
  SocketFactory getSocketFactory();
  
  void setSocketFactory(SocketFactory paramSocketFactory);
  
  ExceptionInterceptor getExceptionInterceptor();
  
  PropertySet getPropertySet();
}
