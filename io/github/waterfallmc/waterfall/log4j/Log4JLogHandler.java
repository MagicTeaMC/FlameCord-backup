package io.github.waterfallmc.waterfall.log4j;

import com.google.common.base.Strings;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.jul.LevelTranslator;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFormatMessage;

class Log4JLogHandler extends Handler {
  private final Map<String, Logger> cache = new ConcurrentHashMap<>();
  
  public void publish(LogRecord record) {
    if (!isLoggable(record))
      return; 
    Logger logger = this.cache.computeIfAbsent(Strings.nullToEmpty(record.getLoggerName()), LogManager::getLogger);
    String message = record.getMessage();
    if (record.getResourceBundle() != null)
      try {
        message = record.getResourceBundle().getString(message);
      } catch (MissingResourceException missingResourceException) {} 
    Level level = LevelTranslator.toLevel(record.getLevel());
    if (record.getParameters() != null && (record.getParameters()).length > 0) {
      logger.log(level, (Message)new MessageFormatMessage(message, record.getParameters()), record.getThrown());
    } else {
      logger.log(level, message, record.getThrown());
    } 
  }
  
  public void flush() {}
  
  public void close() {}
}
