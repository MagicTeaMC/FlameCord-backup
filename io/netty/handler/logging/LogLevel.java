package io.netty.handler.logging;

import io.netty.util.internal.logging.InternalLogLevel;

public enum LogLevel {
  TRACE(InternalLogLevel.TRACE),
  DEBUG(InternalLogLevel.DEBUG),
  INFO(InternalLogLevel.INFO),
  WARN(InternalLogLevel.WARN),
  ERROR(InternalLogLevel.ERROR);
  
  private final InternalLogLevel internalLevel;
  
  LogLevel(InternalLogLevel internalLevel) {
    this.internalLevel = internalLevel;
  }
  
  public InternalLogLevel toInternalLevel() {
    return this.internalLevel;
  }
}
