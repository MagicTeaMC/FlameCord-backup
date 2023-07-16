package org.yaml.snakeyaml.internal;

public class Logger {
  private final java.util.logging.Logger logger;
  
  public enum Level {
    WARNING((String)java.util.logging.Level.FINE);
    
    private final java.util.logging.Level level;
    
    Level(java.util.logging.Level level) {
      this.level = level;
    }
  }
  
  private Logger(String name) {
    this.logger = java.util.logging.Logger.getLogger(name);
  }
  
  public static Logger getLogger(String name) {
    return new Logger(name);
  }
  
  public boolean isLoggable(Level level) {
    return this.logger.isLoggable(level.level);
  }
  
  public void warn(String msg) {
    this.logger.log(Level.WARNING.level, msg);
  }
}
