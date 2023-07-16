package org.apache.logging.log4j.simple;

import java.net.URI;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;

public class SimpleLoggerContextFactory implements LoggerContextFactory {
  public static final SimpleLoggerContextFactory INSTANCE = new SimpleLoggerContextFactory();
  
  public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext) {
    return SimpleLoggerContext.INSTANCE;
  }
  
  public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext, URI configLocation, String name) {
    return SimpleLoggerContext.INSTANCE;
  }
  
  public void removeContext(LoggerContext removeContext) {}
  
  public boolean isClassLoaderDependent() {
    return false;
  }
}
