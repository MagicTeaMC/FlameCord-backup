package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class TimeoutFuture<V> extends FluentFuture.TrustedFuture<V> {
  @CheckForNull
  private ListenableFuture<V> delegateRef;
  
  @CheckForNull
  private ScheduledFuture<?> timer;
  
  static <V> ListenableFuture<V> create(ListenableFuture<V> delegate, long time, TimeUnit unit, ScheduledExecutorService scheduledExecutor) {
    TimeoutFuture<V> result = new TimeoutFuture<>(delegate);
    Fire<V> fire = new Fire<>(result);
    result.timer = scheduledExecutor.schedule(fire, time, unit);
    delegate.addListener(fire, MoreExecutors.directExecutor());
    return result;
  }
  
  private TimeoutFuture(ListenableFuture<V> delegate) {
    this.delegateRef = (ListenableFuture<V>)Preconditions.checkNotNull(delegate);
  }
  
  private static final class Fire<V> implements Runnable {
    @CheckForNull
    TimeoutFuture<V> timeoutFutureRef;
    
    Fire(TimeoutFuture<V> timeoutFuture) {
      this.timeoutFutureRef = timeoutFuture;
    }
    
    public void run() {
      TimeoutFuture<V> timeoutFuture = this.timeoutFutureRef;
      if (timeoutFuture == null)
        return; 
      ListenableFuture<V> delegate = timeoutFuture.delegateRef;
      if (delegate == null)
        return; 
      this.timeoutFutureRef = null;
      if (delegate.isDone()) {
        timeoutFuture.setFuture(delegate);
      } else {
        try {
          ScheduledFuture<?> timer = timeoutFuture.timer;
          timeoutFuture.timer = null;
          String message = "Timed out";
          try {
            if (timer != null) {
              long overDelayMs = Math.abs(timer.getDelay(TimeUnit.MILLISECONDS));
              if (overDelayMs > 10L) {
                String str = String.valueOf(message);
                message = (new StringBuilder(66 + String.valueOf(str).length())).append(str).append(" (timeout delayed by ").append(overDelayMs).append(" ms after scheduled time)").toString();
              } 
            } 
            String str1 = String.valueOf(message), str2 = String.valueOf(delegate);
            message = (new StringBuilder(2 + String.valueOf(str1).length() + String.valueOf(str2).length())).append(str1).append(": ").append(str2).toString();
          } finally {
            timeoutFuture.setException(new TimeoutFuture.TimeoutFutureException(message));
          } 
        } finally {
          delegate.cancel(true);
        } 
      } 
    }
  }
  
  private static final class TimeoutFutureException extends TimeoutException {
    private TimeoutFutureException(String message) {
      super(message);
    }
    
    public synchronized Throwable fillInStackTrace() {
      setStackTrace(new StackTraceElement[0]);
      return this;
    }
  }
  
  @CheckForNull
  protected String pendingToString() {
    ListenableFuture<? extends V> localInputFuture = this.delegateRef;
    ScheduledFuture<?> localTimer = this.timer;
    if (localInputFuture != null) {
      String str1 = String.valueOf(localInputFuture), message = (new StringBuilder(14 + String.valueOf(str1).length())).append("inputFuture=[").append(str1).append("]").toString();
      if (localTimer != null) {
        long delay = localTimer.getDelay(TimeUnit.MILLISECONDS);
        if (delay > 0L) {
          String str = String.valueOf(message);
          message = (new StringBuilder(43 + String.valueOf(str).length())).append(str).append(", remaining delay=[").append(delay).append(" ms]").toString();
        } 
      } 
      return message;
    } 
    return null;
  }
  
  protected void afterDone() {
    maybePropagateCancellationTo(this.delegateRef);
    Future<?> localTimer = this.timer;
    if (localTimer != null)
      localTimer.cancel(false); 
    this.delegateRef = null;
    this.timer = null;
  }
}
