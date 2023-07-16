package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.internal.InternalFutureFailureAccess;
import com.google.common.util.concurrent.internal.InternalFutures;
import com.google.errorprone.annotations.ForOverride;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractCatchingFuture<V, X extends Throwable, F, T> extends FluentFuture.TrustedFuture<V> implements Runnable {
  @CheckForNull
  ListenableFuture<? extends V> inputFuture;
  
  @CheckForNull
  Class<X> exceptionType;
  
  @CheckForNull
  F fallback;
  
  static <V, X extends Throwable> ListenableFuture<V> create(ListenableFuture<? extends V> input, Class<X> exceptionType, Function<? super X, ? extends V> fallback, Executor executor) {
    CatchingFuture<V, X> future = new CatchingFuture<>(input, exceptionType, fallback);
    input.addListener(future, MoreExecutors.rejectionPropagatingExecutor(executor, future));
    return future;
  }
  
  static <X extends Throwable, V> ListenableFuture<V> create(ListenableFuture<? extends V> input, Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback, Executor executor) {
    AsyncCatchingFuture<V, X> future = new AsyncCatchingFuture<>(input, exceptionType, fallback);
    input.addListener(future, MoreExecutors.rejectionPropagatingExecutor(executor, future));
    return future;
  }
  
  AbstractCatchingFuture(ListenableFuture<? extends V> inputFuture, Class<X> exceptionType, F fallback) {
    this.inputFuture = (ListenableFuture<? extends V>)Preconditions.checkNotNull(inputFuture);
    this.exceptionType = (Class<X>)Preconditions.checkNotNull(exceptionType);
    this.fallback = (F)Preconditions.checkNotNull(fallback);
  }
  
  public final void run() {
    T fallbackResult;
    ListenableFuture<? extends V> localInputFuture = this.inputFuture;
    Class<X> localExceptionType = this.exceptionType;
    F localFallback = this.fallback;
    if ((((localInputFuture == null) ? 1 : 0) | ((localExceptionType == null) ? 1 : 0) | ((localFallback == null) ? 1 : 0)) != 0 || 
      
      isCancelled())
      return; 
    this.inputFuture = null;
    V sourceResult = null;
    Throwable throwable = null;
    try {
      if (localInputFuture instanceof InternalFutureFailureAccess)
        throwable = InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess)localInputFuture); 
      if (throwable == null)
        sourceResult = Futures.getDone((Future)localInputFuture); 
    } catch (ExecutionException e) {
      throwable = e.getCause();
      if (throwable == null) {
        String str1 = String.valueOf(localInputFuture.getClass());
        String str2 = String.valueOf(e.getClass());
        throwable = new NullPointerException((new StringBuilder(35 + String.valueOf(str1).length() + String.valueOf(str2).length())).append("Future type ").append(str1).append(" threw ").append(str2).append(" without a cause").toString());
      } 
    } catch (Throwable e) {
      throwable = e;
    } 
    if (throwable == null) {
      set(NullnessCasts.uncheckedCastNullableTToT(sourceResult));
      return;
    } 
    if (!Platform.isInstanceOfThrowableClass(throwable, localExceptionType)) {
      setFuture(localInputFuture);
      return;
    } 
    Throwable throwable1 = throwable;
    try {
      fallbackResult = doFallback(localFallback, (X)throwable1);
    } catch (Throwable t) {
      setException(t);
      return;
    } finally {
      this.exceptionType = null;
      this.fallback = null;
    } 
    setResult(fallbackResult);
  }
  
  @CheckForNull
  protected String pendingToString() {
    ListenableFuture<? extends V> localInputFuture = this.inputFuture;
    Class<X> localExceptionType = this.exceptionType;
    F localFallback = this.fallback;
    String superString = super.pendingToString();
    String resultString = "";
    if (localInputFuture != null) {
      String str = String.valueOf(localInputFuture);
      resultString = (new StringBuilder(16 + String.valueOf(str).length())).append("inputFuture=[").append(str).append("], ").toString();
    } 
    if (localExceptionType != null && localFallback != null) {
      String str1 = resultString, str2 = String.valueOf(localExceptionType), str3 = String.valueOf(localFallback);
      return (new StringBuilder(29 + String.valueOf(str1).length() + String.valueOf(str2).length() + String.valueOf(str3).length())).append(str1).append("exceptionType=[").append(str2).append("], fallback=[").append(str3).append("]").toString();
    } 
    if (superString != null) {
      String.valueOf(superString);
      return (String.valueOf(superString).length() != 0) ? String.valueOf(resultString).concat(String.valueOf(superString)) : new String(String.valueOf(resultString));
    } 
    return null;
  }
  
  @ParametricNullness
  @ForOverride
  abstract T doFallback(F paramF, X paramX) throws Exception;
  
  @ForOverride
  abstract void setResult(@ParametricNullness T paramT);
  
  protected final void afterDone() {
    maybePropagateCancellationTo(this.inputFuture);
    this.inputFuture = null;
    this.exceptionType = null;
    this.fallback = null;
  }
  
  private static final class AsyncCatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, AsyncFunction<? super X, ? extends V>, ListenableFuture<? extends V>> {
    AsyncCatchingFuture(ListenableFuture<? extends V> input, Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback) {
      super(input, exceptionType, fallback);
    }
    
    ListenableFuture<? extends V> doFallback(AsyncFunction<? super X, ? extends V> fallback, X cause) throws Exception {
      ListenableFuture<? extends V> replacement = fallback.apply(cause);
      Preconditions.checkNotNull(replacement, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", fallback);
      return replacement;
    }
    
    void setResult(ListenableFuture<? extends V> result) {
      setFuture(result);
    }
  }
  
  private static final class CatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, Function<? super X, ? extends V>, V> {
    CatchingFuture(ListenableFuture<? extends V> input, Class<X> exceptionType, Function<? super X, ? extends V> fallback) {
      super(input, exceptionType, fallback);
    }
    
    @ParametricNullness
    V doFallback(Function<? super X, ? extends V> fallback, X cause) throws Exception {
      return (V)fallback.apply(cause);
    }
    
    void setResult(@ParametricNullness V result) {
      set(result);
    }
  }
}
