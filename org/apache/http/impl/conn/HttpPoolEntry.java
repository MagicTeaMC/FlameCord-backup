package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.pool.PoolEntry;

@Deprecated
class HttpPoolEntry extends PoolEntry<HttpRoute, OperatedClientConnection> {
  private final Log log;
  
  private final RouteTracker tracker;
  
  public HttpPoolEntry(Log log, String id, HttpRoute route, OperatedClientConnection conn, long timeToLive, TimeUnit timeUnit) {
    super(id, route, conn, timeToLive, timeUnit);
    this.log = log;
    this.tracker = new RouteTracker(route);
  }
  
  public boolean isExpired(long now) {
    boolean expired = super.isExpired(now);
    if (expired && this.log.isDebugEnabled())
      this.log.debug("Connection " + this + " expired @ " + new Date(getExpiry())); 
    return expired;
  }
  
  RouteTracker getTracker() {
    return this.tracker;
  }
  
  HttpRoute getPlannedRoute() {
    return (HttpRoute)getRoute();
  }
  
  HttpRoute getEffectiveRoute() {
    return this.tracker.toRoute();
  }
  
  public boolean isClosed() {
    OperatedClientConnection conn = (OperatedClientConnection)getConnection();
    return !conn.isOpen();
  }
  
  public void close() {
    OperatedClientConnection conn = (OperatedClientConnection)getConnection();
    try {
      conn.close();
    } catch (IOException ex) {
      this.log.debug("I/O error closing connection", ex);
    } 
  }
}
