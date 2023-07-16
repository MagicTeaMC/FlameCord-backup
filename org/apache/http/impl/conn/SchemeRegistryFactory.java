package org.apache.http.impl.conn;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public final class SchemeRegistryFactory {
  public static SchemeRegistry createDefault() {
    SchemeRegistry registry = new SchemeRegistry();
    registry.register(new Scheme("http", 80, (SchemeSocketFactory)PlainSocketFactory.getSocketFactory()));
    registry.register(new Scheme("https", 443, (SchemeSocketFactory)SSLSocketFactory.getSocketFactory()));
    return registry;
  }
  
  public static SchemeRegistry createSystemDefault() {
    SchemeRegistry registry = new SchemeRegistry();
    registry.register(new Scheme("http", 80, (SchemeSocketFactory)PlainSocketFactory.getSocketFactory()));
    registry.register(new Scheme("https", 443, (SchemeSocketFactory)SSLSocketFactory.getSystemSocketFactory()));
    return registry;
  }
}
