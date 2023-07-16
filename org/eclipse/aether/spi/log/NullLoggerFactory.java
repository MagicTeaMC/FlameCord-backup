package org.eclipse.aether.spi.log;

public final class NullLoggerFactory implements LoggerFactory {
  public static final LoggerFactory INSTANCE = new NullLoggerFactory();
  
  public static final Logger LOGGER = new NullLogger();
  
  public Logger getLogger(String name) {
    return LOGGER;
  }
  
  public static Logger getSafeLogger(LoggerFactory loggerFactory, Class<?> type) {
    if (loggerFactory == null)
      return LOGGER; 
    Logger logger = loggerFactory.getLogger(type.getName());
    if (logger == null)
      return LOGGER; 
    return logger;
  }
}
