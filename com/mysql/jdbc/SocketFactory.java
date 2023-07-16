package com.mysql.jdbc;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

@Deprecated
public interface SocketFactory {
  Socket afterHandshake() throws SocketException, IOException;
  
  Socket beforeHandshake() throws SocketException, IOException;
  
  Socket connect(String paramString, int paramInt, Properties paramProperties) throws SocketException, IOException;
}
