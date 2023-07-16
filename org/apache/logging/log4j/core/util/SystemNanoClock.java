package org.apache.logging.log4j.core.util;

public final class SystemNanoClock implements NanoClock {
  public long nanoTime() {
    return System.nanoTime();
  }
}
