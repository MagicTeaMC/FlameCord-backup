package org.apache.logging.log4j.jul;

import java.util.logging.Level;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

class WrappedLogger extends ExtendedLoggerWrapper {
  private static final long serialVersionUID = 1L;
  
  private static final String FQCN = ApiLogger.class.getName();
  
  WrappedLogger(ExtendedLogger logger) {
    super(logger, logger.getName(), logger.getMessageFactory());
  }
  
  public void log(Level level, String message, Throwable t) {
    logIfEnabled(FQCN, level, null, message, t);
  }
  
  public void log(Level level, String message, Object... params) {
    logIfEnabled(FQCN, level, null, message, params);
  }
  
  public void log(Level level, String message) {
    logIfEnabled(FQCN, level, null, message);
  }
  
  public void entry() {
    entry(FQCN, new Object[0]);
  }
  
  public void entry(Object... params) {
    entry(FQCN, params);
  }
  
  public void exit() {
    exit(FQCN, null);
  }
  
  public <R> R exit(R result) {
    return (R)exit(FQCN, result);
  }
  
  public <T extends Throwable> T throwing(T t) {
    return (T)throwing(FQCN, LevelTranslator.toLevel(Level.FINER), (Throwable)t);
  }
}
