package org.apache.logging.log4j.jul;

import java.security.AccessController;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.status.StatusLogger;

public class ApiLogger extends Logger {
  private final WrappedLogger logger;
  
  private static final String FQCN = ApiLogger.class.getName();
  
  ApiLogger(ExtendedLogger logger) {
    super(logger.getName(), null);
    Level javaLevel = LevelTranslator.toJavaLevel(logger.getLevel());
    AccessController.doPrivileged(() -> {
          access$001(this, javaLevel);
          return null;
        });
    this.logger = new WrappedLogger(logger);
  }
  
  public void log(LogRecord record) {
    if (isFiltered(record))
      return; 
    Level level = LevelTranslator.toLevel(record.getLevel());
    Object[] parameters = record.getParameters();
    MessageFactory messageFactory = this.logger.getMessageFactory();
    Message message = (parameters == null) ? messageFactory.newMessage(record.getMessage()) : messageFactory.newMessage(record.getMessage(), parameters);
    Throwable thrown = record.getThrown();
    this.logger.logIfEnabled(FQCN, level, null, message, thrown);
  }
  
  boolean isFiltered(LogRecord logRecord) {
    Filter filter = getFilter();
    return (filter != null && !filter.isLoggable(logRecord));
  }
  
  public boolean isLoggable(Level level) {
    return this.logger.isEnabled(LevelTranslator.toLevel(level));
  }
  
  public String getName() {
    return this.logger.getName();
  }
  
  public void setLevel(Level newLevel) throws SecurityException {
    StatusLogger.getLogger().error("Cannot set JUL log level through log4j-api: ignoring call to Logger.setLevel({})", newLevel);
  }
  
  protected void doSetLevel(Level newLevel) throws SecurityException {
    super.setLevel(newLevel);
  }
  
  public void setParent(Logger parent) {
    throw new UnsupportedOperationException("Cannot set parent logger");
  }
  
  public void log(Level level, String msg) {
    if (getFilter() == null) {
      this.logger.log(LevelTranslator.toLevel(level), msg);
    } else {
      super.log(level, msg);
    } 
  }
  
  public void log(Level level, String msg, Object param1) {
    if (getFilter() == null) {
      this.logger.log(LevelTranslator.toLevel(level), msg, param1);
    } else {
      super.log(level, msg, param1);
    } 
  }
  
  public void log(Level level, String msg, Object[] params) {
    if (getFilter() == null) {
      this.logger.log(LevelTranslator.toLevel(level), msg, params);
    } else {
      super.log(level, msg, params);
    } 
  }
  
  public void log(Level level, String msg, Throwable thrown) {
    if (getFilter() == null) {
      this.logger.log(LevelTranslator.toLevel(level), msg, thrown);
    } else {
      super.log(level, msg, thrown);
    } 
  }
  
  public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
    log(level, msg);
  }
  
  public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
    log(level, msg, param1);
  }
  
  public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
    log(level, msg, params);
  }
  
  public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
    log(level, msg, thrown);
  }
  
  public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
    log(level, msg);
  }
  
  public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {
    log(level, msg, param1);
  }
  
  public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {
    log(level, msg, params);
  }
  
  public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
    log(level, msg, thrown);
  }
  
  public void entering(String sourceClass, String sourceMethod) {
    this.logger.entry();
  }
  
  public void entering(String sourceClass, String sourceMethod, Object param1) {
    this.logger.entry(new Object[] { param1 });
  }
  
  public void entering(String sourceClass, String sourceMethod, Object[] params) {
    this.logger.entry(params);
  }
  
  public void exiting(String sourceClass, String sourceMethod) {
    this.logger.exit();
  }
  
  public void exiting(String sourceClass, String sourceMethod, Object result) {
    this.logger.exit(result);
  }
  
  public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
    this.logger.throwing(thrown);
  }
  
  public void severe(String msg) {
    if (getFilter() == null) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, msg);
    } else {
      super.severe(msg);
    } 
  }
  
  public void warning(String msg) {
    if (getFilter() == null) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, msg);
    } else {
      super.warning(msg);
    } 
  }
  
  public void info(String msg) {
    if (getFilter() == null) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, msg);
    } else {
      super.info(msg);
    } 
  }
  
  public void config(String msg) {
    if (getFilter() == null) {
      this.logger.logIfEnabled(FQCN, LevelTranslator.CONFIG, null, msg);
    } else {
      super.config(msg);
    } 
  }
  
  public void fine(String msg) {
    if (getFilter() == null) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, msg);
    } else {
      super.fine(msg);
    } 
  }
  
  public void finer(String msg) {
    if (getFilter() == null) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, msg);
    } else {
      super.finer(msg);
    } 
  }
  
  public void finest(String msg) {
    if (getFilter() == null) {
      this.logger.logIfEnabled(FQCN, LevelTranslator.FINEST, null, msg);
    } else {
      super.finest(msg);
    } 
  }
}
