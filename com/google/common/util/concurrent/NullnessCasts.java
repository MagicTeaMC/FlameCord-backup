package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class NullnessCasts {
  @ParametricNullness
  static <T> T uncheckedCastNullableTToT(@CheckForNull T t) {
    return t;
  }
  
  @ParametricNullness
  static <T> T uncheckedNull() {
    return null;
  }
}