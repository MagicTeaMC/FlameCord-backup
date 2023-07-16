package io.netty.util.internal.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class Slf4JLoggerFactory extends InternalLoggerFactory {
  public static final InternalLoggerFactory INSTANCE = new Slf4JLoggerFactory();
  
  @Deprecated
  public Slf4JLoggerFactory() {}
  
  Slf4JLoggerFactory(boolean failIfNOP) {
    assert failIfNOP;
    if (LoggerFactory.getILoggerFactory() instanceof org.slf4j.helpers.NOPLoggerFactory)
      throw new NoClassDefFoundError("NOPLoggerFactory not supported"); 
  }
  
  public InternalLogger newInstance(String name) {
    return wrapLogger(LoggerFactory.getLogger(name));
  }
  
  static InternalLogger wrapLogger(Logger logger) {
    return (logger instanceof LocationAwareLogger) ? new LocationAwareSlf4JLogger((LocationAwareLogger)logger) : new Slf4JLogger(logger);
  }
  
  static InternalLoggerFactory getInstanceWithNopCheck() {
    return NopInstanceHolder.INSTANCE_WITH_NOP_CHECK;
  }
  
  private static final class NopInstanceHolder {
    private static final InternalLoggerFactory INSTANCE_WITH_NOP_CHECK = new Slf4JLoggerFactory(true);
  }
}
