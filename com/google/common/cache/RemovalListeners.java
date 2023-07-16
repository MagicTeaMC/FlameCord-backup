package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class RemovalListeners {
  public static <K, V> RemovalListener<K, V> asynchronous(RemovalListener<K, V> listener, Executor executor) {
    Preconditions.checkNotNull(listener);
    Preconditions.checkNotNull(executor);
    return notification -> executor.execute(());
  }
}
