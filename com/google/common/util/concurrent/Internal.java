package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import java.time.Duration;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class Internal {
  static long toNanosSaturated(Duration duration) {
    try {
      return duration.toNanos();
    } catch (ArithmeticException tooBig) {
      return duration.isNegative() ? Long.MIN_VALUE : Long.MAX_VALUE;
    } 
  }
}
