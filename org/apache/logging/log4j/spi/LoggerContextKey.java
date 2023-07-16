package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.message.MessageFactory;

@Deprecated
public class LoggerContextKey {
  public static String create(String name) {
    return create(name, AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS);
  }
  
  public static String create(String name, MessageFactory messageFactory) {
    Class<? extends MessageFactory> messageFactoryClass = (messageFactory != null) ? (Class)messageFactory.getClass() : AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS;
    return create(name, messageFactoryClass);
  }
  
  public static String create(String name, Class<? extends MessageFactory> messageFactoryClass) {
    Class<? extends MessageFactory> mfClass = (messageFactoryClass != null) ? messageFactoryClass : AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS;
    return name + "." + mfClass.getName();
  }
}
