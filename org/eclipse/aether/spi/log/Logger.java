package org.eclipse.aether.spi.log;

public interface Logger {
  boolean isDebugEnabled();
  
  void debug(String paramString);
  
  void debug(String paramString, Throwable paramThrowable);
  
  boolean isWarnEnabled();
  
  void warn(String paramString);
  
  void warn(String paramString, Throwable paramThrowable);
}
