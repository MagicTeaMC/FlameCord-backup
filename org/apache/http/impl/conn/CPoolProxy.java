package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.protocol.HttpContext;

class CPoolProxy implements ManagedHttpClientConnection, HttpContext {
  private volatile CPoolEntry poolEntry;
  
  CPoolProxy(CPoolEntry entry) {
    this.poolEntry = entry;
  }
  
  CPoolEntry getPoolEntry() {
    return this.poolEntry;
  }
  
  CPoolEntry detach() {
    CPoolEntry local = this.poolEntry;
    this.poolEntry = null;
    return local;
  }
  
  ManagedHttpClientConnection getConnection() {
    CPoolEntry local = this.poolEntry;
    if (local == null)
      return null; 
    return (ManagedHttpClientConnection)local.getConnection();
  }
  
  ManagedHttpClientConnection getValidConnection() {
    ManagedHttpClientConnection conn = getConnection();
    if (conn == null)
      throw new ConnectionShutdownException(); 
    return conn;
  }
  
  public void close() throws IOException {
    CPoolEntry local = this.poolEntry;
    if (local != null)
      local.closeConnection(); 
  }
  
  public void shutdown() throws IOException {
    CPoolEntry local = this.poolEntry;
    if (local != null)
      local.shutdownConnection(); 
  }
  
  public boolean isOpen() {
    CPoolEntry local = this.poolEntry;
    return (local != null) ? (!local.isClosed()) : false;
  }
  
  public boolean isStale() {
    ManagedHttpClientConnection managedHttpClientConnection = getConnection();
    return (managedHttpClientConnection != null) ? managedHttpClientConnection.isStale() : true;
  }
  
  public void setSocketTimeout(int timeout) {
    getValidConnection().setSocketTimeout(timeout);
  }
  
  public int getSocketTimeout() {
    return getValidConnection().getSocketTimeout();
  }
  
  public String getId() {
    return getValidConnection().getId();
  }
  
  public void bind(Socket socket) throws IOException {
    getValidConnection().bind(socket);
  }
  
  public Socket getSocket() {
    return getValidConnection().getSocket();
  }
  
  public SSLSession getSSLSession() {
    return getValidConnection().getSSLSession();
  }
  
  public boolean isResponseAvailable(int timeout) throws IOException {
    return getValidConnection().isResponseAvailable(timeout);
  }
  
  public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
    getValidConnection().sendRequestHeader(request);
  }
  
  public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
    getValidConnection().sendRequestEntity(request);
  }
  
  public HttpResponse receiveResponseHeader() throws HttpException, IOException {
    return getValidConnection().receiveResponseHeader();
  }
  
  public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
    getValidConnection().receiveResponseEntity(response);
  }
  
  public void flush() throws IOException {
    getValidConnection().flush();
  }
  
  public HttpConnectionMetrics getMetrics() {
    return getValidConnection().getMetrics();
  }
  
  public InetAddress getLocalAddress() {
    return getValidConnection().getLocalAddress();
  }
  
  public int getLocalPort() {
    return getValidConnection().getLocalPort();
  }
  
  public InetAddress getRemoteAddress() {
    return getValidConnection().getRemoteAddress();
  }
  
  public int getRemotePort() {
    return getValidConnection().getRemotePort();
  }
  
  public Object getAttribute(String id) {
    ManagedHttpClientConnection conn = getValidConnection();
    return (conn instanceof HttpContext) ? ((HttpContext)conn).getAttribute(id) : null;
  }
  
  public void setAttribute(String id, Object obj) {
    ManagedHttpClientConnection conn = getValidConnection();
    if (conn instanceof HttpContext)
      ((HttpContext)conn).setAttribute(id, obj); 
  }
  
  public Object removeAttribute(String id) {
    ManagedHttpClientConnection conn = getValidConnection();
    return (conn instanceof HttpContext) ? ((HttpContext)conn).removeAttribute(id) : null;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder("CPoolProxy{");
    ManagedHttpClientConnection conn = getConnection();
    if (conn != null) {
      sb.append(conn);
    } else {
      sb.append("detached");
    } 
    sb.append('}');
    return sb.toString();
  }
  
  public static HttpClientConnection newProxy(CPoolEntry poolEntry) {
    return (HttpClientConnection)new CPoolProxy(poolEntry);
  }
  
  private static CPoolProxy getProxy(HttpClientConnection conn) {
    if (!CPoolProxy.class.isInstance(conn))
      throw new IllegalStateException("Unexpected connection proxy class: " + conn.getClass()); 
    return CPoolProxy.class.cast(conn);
  }
  
  public static CPoolEntry getPoolEntry(HttpClientConnection proxy) {
    CPoolEntry entry = getProxy(proxy).getPoolEntry();
    if (entry == null)
      throw new ConnectionShutdownException(); 
    return entry;
  }
  
  public static CPoolEntry detach(HttpClientConnection conn) {
    return getProxy(conn).detach();
  }
}
