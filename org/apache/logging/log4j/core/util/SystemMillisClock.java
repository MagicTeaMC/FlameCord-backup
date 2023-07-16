package org.apache.logging.log4j.core.util;

public final class SystemMillisClock implements Clock {
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }
}
