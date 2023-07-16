package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.function.Function;
import javax.annotation.CheckForNull;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Function<F, T> extends Function<F, T> {
  @ParametricNullness
  @CanIgnoreReturnValue
  T apply(@ParametricNullness F paramF);
  
  boolean equals(@CheckForNull Object paramObject);
}
