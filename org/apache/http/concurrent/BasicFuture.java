package org.apache.http.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.util.Args;

public class BasicFuture<T> implements Future<T>, Cancellable {
  private final FutureCallback<T> callback;
  
  private volatile boolean completed;
  
  private volatile boolean cancelled;
  
  private volatile T result;
  
  private volatile Exception ex;
  
  public BasicFuture(FutureCallback<T> callback) {
    this.callback = callback;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public boolean isDone() {
    return this.completed;
  }
  
  private T getResult() throws ExecutionException {
    if (this.ex != null)
      throw new ExecutionException(this.ex); 
    if (this.cancelled)
      throw new CancellationException(); 
    return this.result;
  }
  
  public synchronized T get() throws InterruptedException, ExecutionException {
    while (!this.completed)
      wait(); 
    return getResult();
  }
  
  public synchronized T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    Args.notNull(unit, "Time unit");
    long msecs = unit.toMillis(timeout);
    long startTime = (msecs <= 0L) ? 0L : System.currentTimeMillis();
    long waitTime = msecs;
    if (this.completed)
      return getResult(); 
    if (waitTime <= 0L)
      throw new TimeoutException(); 
    while (true) {
      wait(waitTime);
      if (this.completed)
        return getResult(); 
      waitTime = msecs - System.currentTimeMillis() - startTime;
      if (waitTime <= 0L)
        throw new TimeoutException(); 
    } 
  }
  
  public boolean completed(T result) {
    synchronized (this) {
      if (this.completed)
        return false; 
      this.completed = true;
      this.result = result;
      notifyAll();
    } 
    if (this.callback != null)
      this.callback.completed(result); 
    return true;
  }
  
  public boolean failed(Exception exception) {
    synchronized (this) {
      if (this.completed)
        return false; 
      this.completed = true;
      this.ex = exception;
      notifyAll();
    } 
    if (this.callback != null)
      this.callback.failed(exception); 
    return true;
  }
  
  public boolean cancel(boolean mayInterruptIfRunning) {
    synchronized (this) {
      if (this.completed)
        return false; 
      this.completed = true;
      this.cancelled = true;
      notifyAll();
    } 
    if (this.callback != null)
      this.callback.cancelled(); 
    return true;
  }
  
  public boolean cancel() {
    return cancel(true);
  }
}
