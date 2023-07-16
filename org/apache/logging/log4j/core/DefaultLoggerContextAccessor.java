package org.apache.logging.log4j.core;

public class DefaultLoggerContextAccessor implements LoggerContextAccessor {
  public static DefaultLoggerContextAccessor INSTANCE = new DefaultLoggerContextAccessor();
  
  public LoggerContext getLoggerContext() {
    return LoggerContext.getContext();
  }
}
