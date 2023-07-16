package org.apache.http.impl.client;

import java.net.ProxySelector;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.HttpParams;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class SystemDefaultHttpClient extends DefaultHttpClient {
  public SystemDefaultHttpClient(HttpParams params) {
    super((ClientConnectionManager)null, params);
  }
  
  public SystemDefaultHttpClient() {
    super((ClientConnectionManager)null, (HttpParams)null);
  }
  
  protected ClientConnectionManager createClientConnectionManager() {
    PoolingClientConnectionManager connmgr = new PoolingClientConnectionManager(SchemeRegistryFactory.createSystemDefault());
    String s = System.getProperty("http.keepAlive", "true");
    if ("true".equalsIgnoreCase(s)) {
      s = System.getProperty("http.maxConnections", "5");
      int max = Integer.parseInt(s);
      connmgr.setDefaultMaxPerRoute(max);
      connmgr.setMaxTotal(2 * max);
    } 
    return (ClientConnectionManager)connmgr;
  }
  
  protected HttpRoutePlanner createHttpRoutePlanner() {
    return (HttpRoutePlanner)new ProxySelectorRoutePlanner(getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
  }
  
  protected ConnectionReuseStrategy createConnectionReuseStrategy() {
    String s = System.getProperty("http.keepAlive", "true");
    if ("true".equalsIgnoreCase(s))
      return (ConnectionReuseStrategy)new DefaultConnectionReuseStrategy(); 
    return (ConnectionReuseStrategy)new NoConnectionReuseStrategy();
  }
}
