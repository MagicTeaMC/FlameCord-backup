package io.github.waterfallmc.waterfall.utils;

public class FastException extends RuntimeException {
  public FastException(String message) {
    super(message);
  }
  
  public synchronized Throwable initCause(Throwable cause) {
    return this;
  }
  
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
