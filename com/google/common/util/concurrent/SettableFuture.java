package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class SettableFuture<V> extends AbstractFuture.TrustedFuture<V> {
  public static <V> SettableFuture<V> create() {
    return new SettableFuture<>();
  }
  
  @CanIgnoreReturnValue
  public boolean set(@ParametricNullness V value) {
    return super.set(value);
  }
  
  @CanIgnoreReturnValue
  public boolean setException(Throwable throwable) {
    return super.setException(throwable);
  }
  
  @CanIgnoreReturnValue
  public boolean setFuture(ListenableFuture<? extends V> future) {
    return super.setFuture(future);
  }
}
