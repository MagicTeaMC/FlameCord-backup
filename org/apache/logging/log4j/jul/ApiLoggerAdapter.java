package org.apache.logging.log4j.jul;

import java.util.logging.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFormatMessageFactory;
import org.apache.logging.log4j.spi.LoggerContext;

public class ApiLoggerAdapter extends AbstractLoggerAdapter {
  private static final MessageFactory MESSAGE_FACTORY = (MessageFactory)new MessageFormatMessageFactory();
  
  protected Logger newLogger(String name, LoggerContext context) {
    return new ApiLogger(context.getLogger(name, MESSAGE_FACTORY));
  }
}
