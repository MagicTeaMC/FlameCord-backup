package org.eclipse.aether.spi.log;

final class NullLogger implements Logger {
  public boolean isDebugEnabled() {
    return false;
  }
  
  public void debug(String msg) {}
  
  public void debug(String msg, Throwable error) {}
  
  public boolean isWarnEnabled() {
    return false;
  }
  
  public void warn(String msg) {}
  
  public void warn(String msg, Throwable error) {}
}
