package org.apache.logging.log4j.core.appender.rolling;

public interface RolloverListener {
  void rolloverTriggered(String paramString);
  
  void rolloverComplete(String paramString);
}
