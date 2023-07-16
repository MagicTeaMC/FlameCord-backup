package org.apache.logging.log4j.status;

import java.io.PrintStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.util.Strings;

class SimpleLoggerFactory {
  private static final SimpleLoggerFactory INSTANCE = new SimpleLoggerFactory();
  
  static SimpleLoggerFactory getInstance() {
    return INSTANCE;
  }
  
  SimpleLogger createSimpleLogger(String name, Level level, MessageFactory messageFactory, PrintStream stream) {
    String dateFormat = StatusLogger.PROPS.getStringProperty("log4j2.StatusLogger.DateFormat");
    boolean dateFormatProvided = Strings.isNotBlank(dateFormat);
    return new SimpleLogger(name, level, false, true, dateFormatProvided, false, dateFormat, messageFactory, StatusLogger.PROPS, stream);
  }
}
