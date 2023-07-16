package dev._2lstudios.flamecord.antibot;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerWrapper {
  private Logger logger;
  
  private long lastLog = System.currentTimeMillis();
  
  public LoggerWrapper(Logger logger) {
    this.logger = logger;
  }
  
  public void log(Level level, String msg, Object... params) {
    long currentTime = System.currentTimeMillis();
    if (currentTime - this.lastLog > 100L) {
      this.lastLog = currentTime;
      this.logger.log(level, msg, params);
    } 
  }
  
  public Logger getLogger() {
    return this.logger;
  }
}
