package org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class JDK14LoggerFactory implements ILoggerFactory {
  ConcurrentMap<String, Logger> loggerMap;
  
  public JDK14LoggerFactory() {
    this.loggerMap = new ConcurrentHashMap<String, Logger>();
    Logger.getLogger("");
  }
  
  public Logger getLogger(String name) {
    if (name.equalsIgnoreCase("ROOT"))
      name = ""; 
    Logger slf4jLogger = this.loggerMap.get(name);
    if (slf4jLogger != null)
      return slf4jLogger; 
    Logger julLogger = Logger.getLogger(name);
    JDK14LoggerAdapter jDK14LoggerAdapter = new JDK14LoggerAdapter(julLogger);
    Logger oldInstance = (Logger)this.loggerMap.putIfAbsent(name, jDK14LoggerAdapter);
    return (oldInstance == null) ? (Logger)jDK14LoggerAdapter : oldInstance;
  }
}
