package org.apache.http.impl.conn;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.HttpClientConnectionOperator;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.LangUtils;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class BasicHttpClientConnectionManager implements HttpClientConnectionManager, Closeable {
  private final Log log = LogFactory.getLog(getClass());
  
  private final HttpClientConnectionOperator connectionOperator;
  
  private final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory;
  
  private ManagedHttpClientConnection conn;
  
  private HttpRoute route;
  
  private Object state;
  
  private long updated;
  
  private long expiry;
  
  private boolean leased;
  
  private SocketConfig socketConfig;
  
  private ConnectionConfig connConfig;
  
  private final AtomicBoolean isShutdown;
  
  private static Registry<ConnectionSocketFactory> getDefaultRegistry() {
    return RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
  }
  
  public BasicHttpClientConnectionManager(Lookup<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory, SchemePortResolver schemePortResolver, DnsResolver dnsResolver) {
    this(new DefaultHttpClientConnectionOperator(socketFactoryRegistry, schemePortResolver, dnsResolver), connFactory);
  }
  
  public BasicHttpClientConnectionManager(HttpClientConnectionOperator httpClientConnectionOperator, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
    this.connectionOperator = (HttpClientConnectionOperator)Args.notNull(httpClientConnectionOperator, "Connection operator");
    this.connFactory = (connFactory != null) ? connFactory : ManagedHttpClientConnectionFactory.INSTANCE;
    this.expiry = Long.MAX_VALUE;
    this.socketConfig = SocketConfig.DEFAULT;
    this.connConfig = ConnectionConfig.DEFAULT;
    this.isShutdown = new AtomicBoolean(false);
  }
  
  public BasicHttpClientConnectionManager(Lookup<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
    this(socketFactoryRegistry, connFactory, null, null);
  }
  
  public BasicHttpClientConnectionManager(Lookup<ConnectionSocketFactory> socketFactoryRegistry) {
    this(socketFactoryRegistry, null, null, null);
  }
  
  public BasicHttpClientConnectionManager() {
    this((Lookup<ConnectionSocketFactory>)getDefaultRegistry(), null, null, null);
  }
  
  protected void finalize() throws Throwable {
    try {
      shutdown();
    } finally {
      super.finalize();
    } 
  }
  
  public void close() {
    if (this.isShutdown.compareAndSet(false, true))
      closeConnection(); 
  }
  
  HttpRoute getRoute() {
    return this.route;
  }
  
  Object getState() {
    return this.state;
  }
  
  public synchronized SocketConfig getSocketConfig() {
    return this.socketConfig;
  }
  
  public synchronized void setSocketConfig(SocketConfig socketConfig) {
    this.socketConfig = (socketConfig != null) ? socketConfig : SocketConfig.DEFAULT;
  }
  
  public synchronized ConnectionConfig getConnectionConfig() {
    return this.connConfig;
  }
  
  public synchronized void setConnectionConfig(ConnectionConfig connConfig) {
    this.connConfig = (connConfig != null) ? connConfig : ConnectionConfig.DEFAULT;
  }
  
  public final ConnectionRequest requestConnection(final HttpRoute route, final Object state) {
    Args.notNull(route, "Route");
    return new ConnectionRequest() {
        public boolean cancel() {
          return false;
        }
        
        public HttpClientConnection get(long timeout, TimeUnit timeUnit) {
          return BasicHttpClientConnectionManager.this.getConnection(route, state);
        }
      };
  }
  
  private synchronized void closeConnection() {
    if (this.conn != null) {
      this.log.debug("Closing connection");
      try {
        this.conn.close();
      } catch (IOException iox) {
        if (this.log.isDebugEnabled())
          this.log.debug("I/O exception closing connection", iox); 
      } 
      this.conn = null;
    } 
  }
  
  private void checkExpiry() {
    if (this.conn != null && System.currentTimeMillis() >= this.expiry) {
      if (this.log.isDebugEnabled())
        this.log.debug("Connection expired @ " + new Date(this.expiry)); 
      closeConnection();
    } 
  }
  
  synchronized HttpClientConnection getConnection(HttpRoute route, Object state) {
    Asserts.check(!this.isShutdown.get(), "Connection manager has been shut down");
    if (this.log.isDebugEnabled())
      this.log.debug("Get connection for route " + route); 
    Asserts.check(!this.leased, "Connection is still allocated");
    if (!LangUtils.equals(this.route, route) || !LangUtils.equals(this.state, state))
      closeConnection(); 
    this.route = route;
    this.state = state;
    checkExpiry();
    if (this.conn == null)
      this.conn = (ManagedHttpClientConnection)this.connFactory.create(route, this.connConfig); 
    this.conn.setSocketTimeout(this.socketConfig.getSoTimeout());
    this.leased = true;
    return (HttpClientConnection)this.conn;
  }
  
  public synchronized void releaseConnection(HttpClientConnection conn, Object state, long keepalive, TimeUnit timeUnit) {
    Args.notNull(conn, "Connection");
    Asserts.check((conn == this.conn), "Connection not obtained from this manager");
    if (this.log.isDebugEnabled())
      this.log.debug("Releasing connection " + conn); 
    if (this.isShutdown.get())
      return; 
    try {
      this.updated = System.currentTimeMillis();
      if (!this.conn.isOpen()) {
        this.conn = null;
        this.route = null;
        this.conn = null;
        this.expiry = Long.MAX_VALUE;
      } else {
        this.state = state;
        this.conn.setSocketTimeout(0);
        if (this.log.isDebugEnabled()) {
          String s;
          if (keepalive > 0L) {
            s = "for " + keepalive + " " + timeUnit;
          } else {
            s = "indefinitely";
          } 
          this.log.debug("Connection can be kept alive " + s);
        } 
        if (keepalive > 0L) {
          this.expiry = this.updated + timeUnit.toMillis(keepalive);
        } else {
          this.expiry = Long.MAX_VALUE;
        } 
      } 
    } finally {
      this.leased = false;
    } 
  }
  
  public void connect(HttpClientConnection conn, HttpRoute route, int connectTimeout, HttpContext context) throws IOException {
    HttpHost host;
    Args.notNull(conn, "Connection");
    Args.notNull(route, "HTTP route");
    Asserts.check((conn == this.conn), "Connection not obtained from this manager");
    if (route.getProxyHost() != null) {
      host = route.getProxyHost();
    } else {
      host = route.getTargetHost();
    } 
    InetSocketAddress localAddress = route.getLocalSocketAddress();
    this.connectionOperator.connect(this.conn, host, localAddress, connectTimeout, this.socketConfig, context);
  }
  
  public void upgrade(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {
    Args.notNull(conn, "Connection");
    Args.notNull(route, "HTTP route");
    Asserts.check((conn == this.conn), "Connection not obtained from this manager");
    this.connectionOperator.upgrade(this.conn, route.getTargetHost(), context);
  }
  
  public void routeComplete(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {}
  
  public synchronized void closeExpiredConnections() {
    if (this.isShutdown.get())
      return; 
    if (!this.leased)
      checkExpiry(); 
  }
  
  public synchronized void closeIdleConnections(long idletime, TimeUnit timeUnit) {
    Args.notNull(timeUnit, "Time unit");
    if (this.isShutdown.get())
      return; 
    if (!this.leased) {
      long time = timeUnit.toMillis(idletime);
      if (time < 0L)
        time = 0L; 
      long deadline = System.currentTimeMillis() - time;
      if (this.updated <= deadline)
        closeConnection(); 
    } 
  }
  
  public void shutdown() {
    if (this.isShutdown.compareAndSet(false, true) && 
      this.conn != null) {
      this.log.debug("Shutting down connection");
      try {
        this.conn.shutdown();
      } catch (IOException iox) {
        if (this.log.isDebugEnabled())
          this.log.debug("I/O exception shutting down connection", iox); 
      } 
      this.conn = null;
    } 
  }
}
