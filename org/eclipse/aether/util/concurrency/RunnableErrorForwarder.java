package org.eclipse.aether.util.concurrency;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public final class RunnableErrorForwarder {
  private final Thread thread = Thread.currentThread();
  
  private final AtomicInteger counter = new AtomicInteger();
  
  private final AtomicReference<Throwable> error = new AtomicReference<>();
  
  public Runnable wrap(final Runnable runnable) {
    Objects.requireNonNull(runnable, "runnable cannot be null");
    this.counter.incrementAndGet();
    return new Runnable() {
        public void run() {
          try {
            runnable.run();
          } catch (RuntimeException|Error e) {
            RunnableErrorForwarder.this.error.compareAndSet(null, e);
            throw e;
          } finally {
            RunnableErrorForwarder.this.counter.decrementAndGet();
            LockSupport.unpark(RunnableErrorForwarder.this.thread);
          } 
        }
      };
  }
  
  public void await() {
    awaitTerminationOfAllRunnables();
    Throwable error = this.error.get();
    if (error != null) {
      if (error instanceof RuntimeException)
        throw (RuntimeException)error; 
      if (error instanceof ThreadDeath)
        throw new IllegalStateException(error); 
      if (error instanceof Error)
        throw (Error)error; 
      throw new IllegalStateException(error);
    } 
  }
  
  private void awaitTerminationOfAllRunnables() {
    if (!this.thread.equals(Thread.currentThread()))
      throw new IllegalStateException("wrong caller thread, expected " + this.thread + " and not " + 
          Thread.currentThread()); 
    boolean interrupted = false;
    while (this.counter.get() > 0) {
      LockSupport.park();
      if (Thread.interrupted())
        interrupted = true; 
    } 
    if (interrupted)
      Thread.currentThread().interrupt(); 
  }
}
