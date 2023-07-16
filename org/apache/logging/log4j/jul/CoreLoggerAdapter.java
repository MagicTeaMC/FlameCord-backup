package org.apache.logging.log4j.jul;

import java.util.logging.Logger;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFormatMessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;

public class CoreLoggerAdapter extends AbstractLoggerAdapter {
  private static final MessageFactory MESSAGE_FACTORY = (MessageFactory)new MessageFormatMessageFactory();
  
  protected Logger newLogger(String name, LoggerContext context) {
    ExtendedLogger original = context.getLogger(name, MESSAGE_FACTORY);
    if (original instanceof Logger)
      return new CoreLogger((Logger)original); 
    return new ApiLogger(original);
  }
}
