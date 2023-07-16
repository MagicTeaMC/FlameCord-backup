package com.mysql.cj.protocol;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.log.Log;
import java.io.IOException;

public interface SocketFactory extends SocketMetadata {
  <T extends java.io.Closeable> T connect(String paramString, int paramInt1, PropertySet paramPropertySet, int paramInt2) throws IOException;
  
  default void beforeHandshake() throws IOException {}
  
  <T extends java.io.Closeable> T performTlsHandshake(SocketConnection paramSocketConnection, ServerSession paramServerSession) throws IOException;
  
  default <T extends java.io.Closeable> T performTlsHandshake(SocketConnection socketConnection, ServerSession serverSession, Log log) throws IOException {
    return performTlsHandshake(socketConnection, serverSession);
  }
  
  default void afterHandshake() throws IOException {}
}
