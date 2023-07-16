package org.apache.logging.log4j.jul;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class CoreLogger extends ApiLogger {
  private final Logger logger;
  
  CoreLogger(Logger logger) {
    super((ExtendedLogger)logger);
    this.logger = logger;
  }
  
  public void setLevel(Level level) throws SecurityException {
    doSetLevel(level);
    this.logger.setLevel(LevelTranslator.toLevel(level));
  }
  
  public synchronized void setUseParentHandlers(boolean additive) {
    this.logger.setAdditive(additive);
  }
  
  public synchronized boolean getUseParentHandlers() {
    return this.logger.isAdditive();
  }
  
  public Logger getParent() {
    Logger parent = this.logger.getParent();
    return (parent == null) ? null : Logger.getLogger(parent.getName());
  }
}
