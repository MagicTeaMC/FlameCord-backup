package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class ImmediateFuture<V> implements ListenableFuture<V> {
  static final ListenableFuture<?> NULL = new ImmediateFuture(null);
  
  private static final Logger log = Logger.getLogger(ImmediateFuture.class.getName());
  
  @ParametricNullness
  private final V value;
  
  ImmediateFuture(@ParametricNullness V value) {
    this.value = value;
  }
  
  public void addListener(Runnable listener, Executor executor) {
    Preconditions.checkNotNull(listener, "Runnable was null.");
    Preconditions.checkNotNull(executor, "Executor was null.");
    try {
      executor.execute(listener);
    } catch (RuntimeException e) {
      String str1 = String.valueOf(listener), str2 = String.valueOf(executor);
      log.log(Level.SEVERE, (new StringBuilder(57 + String.valueOf(str1).length() + String.valueOf(str2).length())).append("RuntimeException while executing runnable ").append(str1).append(" with executor ").append(str2).toString(), e);
    } 
  }
  
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }
  
  @ParametricNullness
  public V get() {
    return this.value;
  }
  
  @ParametricNullness
  public V get(long timeout, TimeUnit unit) throws ExecutionException {
    Preconditions.checkNotNull(unit);
    return get();
  }
  
  public boolean isCancelled() {
    return false;
  }
  
  public boolean isDone() {
    return true;
  }
  
  public String toString() {
    String str1 = super.toString(), str2 = String.valueOf(this.value);
    return (new StringBuilder(27 + String.valueOf(str1).length() + String.valueOf(str2).length())).append(str1).append("[status=SUCCESS, result=[").append(str2).append("]]").toString();
  }
  
  static final class ImmediateFailedFuture<V> extends AbstractFuture.TrustedFuture<V> {
    ImmediateFailedFuture(Throwable thrown) {
      setException(thrown);
    }
  }
  
  static final class ImmediateCancelledFuture<V> extends AbstractFuture.TrustedFuture<V> {
    static final ImmediateCancelledFuture<Object> INSTANCE = AbstractFuture.GENERATE_CANCELLATION_CAUSES ? null : new ImmediateCancelledFuture();
    
    ImmediateCancelledFuture() {
      cancel(false);
    }
  }
}
