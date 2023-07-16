package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;

public interface LoggerContext {
  public static final LoggerContext[] EMPTY_ARRAY = new LoggerContext[0];
  
  Object getExternalContext();
  
  default ExtendedLogger getLogger(Class<?> cls) {
    String canonicalName = cls.getCanonicalName();
    return getLogger((canonicalName != null) ? canonicalName : cls.getName());
  }
  
  default ExtendedLogger getLogger(Class<?> cls, MessageFactory messageFactory) {
    String canonicalName = cls.getCanonicalName();
    return getLogger((canonicalName != null) ? canonicalName : cls.getName(), messageFactory);
  }
  
  ExtendedLogger getLogger(String paramString);
  
  ExtendedLogger getLogger(String paramString, MessageFactory paramMessageFactory);
  
  default LoggerRegistry<? extends Logger> getLoggerRegistry() {
    return null;
  }
  
  default Object getObject(String key) {
    return null;
  }
  
  boolean hasLogger(String paramString);
  
  boolean hasLogger(String paramString, Class<? extends MessageFactory> paramClass);
  
  boolean hasLogger(String paramString, MessageFactory paramMessageFactory);
  
  default Object putObject(String key, Object value) {
    return null;
  }
  
  default Object putObjectIfAbsent(String key, Object value) {
    return null;
  }
  
  default Object removeObject(String key) {
    return null;
  }
  
  default boolean removeObject(String key, Object value) {
    return false;
  }
}
