package org.eclipse.aether.internal.impl.slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.spi.log.Logger;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.sisu.Nullable;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

@Named("slf4j")
public class Slf4jLoggerFactory implements LoggerFactory, Service {
  private static final boolean AVAILABLE;
  
  private ILoggerFactory factory;
  
  static {
    boolean available;
    try {
      Slf4jLoggerFactory.class.getClassLoader().loadClass("org.slf4j.ILoggerFactory");
      available = true;
    } catch (Exception|LinkageError e) {
      available = false;
    } 
    AVAILABLE = available;
  }
  
  public static boolean isSlf4jAvailable() {
    return AVAILABLE;
  }
  
  public Slf4jLoggerFactory() {}
  
  @Inject
  Slf4jLoggerFactory(@Nullable ILoggerFactory factory) {
    setLoggerFactory(factory);
  }
  
  public void initService(ServiceLocator locator) {
    setLoggerFactory((ILoggerFactory)locator.getService(ILoggerFactory.class));
  }
  
  public Slf4jLoggerFactory setLoggerFactory(ILoggerFactory factory) {
    this.factory = factory;
    return this;
  }
  
  public Logger getLogger(String name) {
    Logger logger = getFactory().getLogger(name);
    if (logger instanceof LocationAwareLogger)
      return new Slf4jLoggerEx((LocationAwareLogger)logger); 
    return new Slf4jLogger(logger);
  }
  
  private ILoggerFactory getFactory() {
    if (this.factory == null)
      this.factory = LoggerFactory.getILoggerFactory(); 
    return this.factory;
  }
  
  private static final class Slf4jLogger implements Logger {
    private final Logger logger;
    
    Slf4jLogger(Logger logger) {
      this.logger = logger;
    }
    
    public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
    }
    
    public void debug(String msg) {
      this.logger.debug(msg);
    }
    
    public void debug(String msg, Throwable error) {
      this.logger.debug(msg, error);
    }
    
    public boolean isWarnEnabled() {
      return this.logger.isWarnEnabled();
    }
    
    public void warn(String msg) {
      this.logger.warn(msg);
    }
    
    public void warn(String msg, Throwable error) {
      this.logger.warn(msg, error);
    }
  }
  
  private static final class Slf4jLoggerEx implements Logger {
    private static final String FQCN = Slf4jLoggerEx.class.getName();
    
    private final LocationAwareLogger logger;
    
    Slf4jLoggerEx(LocationAwareLogger logger) {
      this.logger = logger;
    }
    
    public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
    }
    
    public void debug(String msg) {
      this.logger.log(null, FQCN, 10, msg, null, null);
    }
    
    public void debug(String msg, Throwable error) {
      this.logger.log(null, FQCN, 10, msg, null, error);
    }
    
    public boolean isWarnEnabled() {
      return this.logger.isWarnEnabled();
    }
    
    public void warn(String msg) {
      this.logger.log(null, FQCN, 30, msg, null, null);
    }
    
    public void warn(String msg, Throwable error) {
      this.logger.log(null, FQCN, 30, msg, null, error);
    }
  }
}
