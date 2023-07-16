package org.apache.http.impl.bootstrap;

import java.io.IOException;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpService;

class Worker implements Runnable {
  private final HttpService httpservice;
  
  private final HttpServerConnection conn;
  
  private final ExceptionLogger exceptionLogger;
  
  Worker(HttpService httpservice, HttpServerConnection conn, ExceptionLogger exceptionLogger) {
    this.httpservice = httpservice;
    this.conn = conn;
    this.exceptionLogger = exceptionLogger;
  }
  
  public HttpServerConnection getConnection() {
    return this.conn;
  }
  
  public void run() {
    try {
      BasicHttpContext localContext = new BasicHttpContext();
      HttpCoreContext context = HttpCoreContext.adapt((HttpContext)localContext);
      while (!Thread.interrupted() && this.conn.isOpen()) {
        this.httpservice.handleRequest(this.conn, (HttpContext)context);
        localContext.clear();
      } 
      this.conn.close();
    } catch (Exception ex) {
      this.exceptionLogger.log(ex);
    } finally {
      try {
        this.conn.shutdown();
      } catch (IOException ex) {
        this.exceptionLogger.log(ex);
      } 
    } 
  }
}
