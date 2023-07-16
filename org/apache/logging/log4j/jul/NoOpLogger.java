package org.apache.logging.log4j.jul;

import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class NoOpLogger extends Logger {
  protected NoOpLogger(String name) {
    super(name, null);
  }
  
  public void log(LogRecord record) {}
  
  public void log(Level level, String msg) {}
  
  public void log(Level level, Supplier<String> msgSupplier) {}
  
  public void log(Level level, String msg, Object param1) {}
  
  public void log(Level level, String msg, Object[] params) {}
  
  public void log(Level level, String msg, Throwable thrown) {}
  
  public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {}
  
  public void logp(Level level, String sourceClass, String sourceMethod, String msg) {}
  
  public void logp(Level level, String sourceClass, String sourceMethod, Supplier<String> msgSupplier) {}
  
  public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {}
  
  public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {}
  
  public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {}
  
  public void logp(Level level, String sourceClass, String sourceMethod, Throwable thrown, Supplier<String> msgSupplier) {}
  
  public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {}
  
  public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {}
  
  public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {}
  
  public void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Object... params) {}
  
  public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {}
  
  public void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Throwable thrown) {}
  
  public void entering(String sourceClass, String sourceMethod) {}
  
  public void entering(String sourceClass, String sourceMethod, Object param1) {}
  
  public void entering(String sourceClass, String sourceMethod, Object[] params) {}
  
  public void exiting(String sourceClass, String sourceMethod) {}
  
  public void exiting(String sourceClass, String sourceMethod, Object result) {}
  
  public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {}
  
  public void severe(String msg) {}
  
  public void warning(String msg) {}
  
  public void info(String msg) {}
  
  public void config(String msg) {}
  
  public void fine(String msg) {}
  
  public void finer(String msg) {}
  
  public void finest(String msg) {}
  
  public void severe(Supplier<String> msgSupplier) {}
  
  public void warning(Supplier<String> msgSupplier) {}
  
  public void info(Supplier<String> msgSupplier) {}
  
  public void config(Supplier<String> msgSupplier) {}
  
  public void fine(Supplier<String> msgSupplier) {}
  
  public void finer(Supplier<String> msgSupplier) {}
  
  public void finest(Supplier<String> msgSupplier) {}
  
  public void setLevel(Level newLevel) throws SecurityException {}
  
  public Level getLevel() {
    return Level.OFF;
  }
  
  public boolean isLoggable(Level level) {
    return false;
  }
}
