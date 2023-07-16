package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public final class Uninterruptibles {
  @GwtIncompatible
  public static void awaitUninterruptibly(CountDownLatch latch) {
    boolean interrupted = false;
    while (true) {
      try {
        latch.await();
        return;
      } catch (InterruptedException e) {
      
      } finally {
        if (interrupted)
          Thread.currentThread().interrupt(); 
      } 
    } 
  }
  
  @CanIgnoreReturnValue
  @GwtIncompatible
  public static boolean awaitUninterruptibly(CountDownLatch latch, Duration timeout) {
    return awaitUninterruptibly(latch, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @CanIgnoreReturnValue
  @GwtIncompatible
  public static boolean awaitUninterruptibly(CountDownLatch latch, long timeout, TimeUnit unit) {
    boolean interrupted = false;
    try {
      long remainingNanos = unit.toNanos(timeout);
      long end = System.nanoTime() + remainingNanos;
      while (true) {
        try {
          return latch.await(remainingNanos, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
          interrupted = true;
          remainingNanos = end - System.nanoTime();
        } 
      } 
    } finally {
      if (interrupted)
        Thread.currentThread().interrupt(); 
    } 
  }
  
  @GwtIncompatible
  public static boolean awaitUninterruptibly(Condition condition, Duration timeout) {
    return awaitUninterruptibly(condition, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @GwtIncompatible
  public static boolean awaitUninterruptibly(Condition condition, long timeout, TimeUnit unit) {
    boolean interrupted = false;
    try {
      long remainingNanos = unit.toNanos(timeout);
      long end = System.nanoTime() + remainingNanos;
      while (true) {
        try {
          return condition.await(remainingNanos, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
          interrupted = true;
          remainingNanos = end - System.nanoTime();
        } 
      } 
    } finally {
      if (interrupted)
        Thread.currentThread().interrupt(); 
    } 
  }
  
  @GwtIncompatible
  public static void joinUninterruptibly(Thread toJoin) {
    boolean interrupted = false;
    while (true) {
      try {
        toJoin.join();
        return;
      } catch (InterruptedException e) {
      
      } finally {
        if (interrupted)
          Thread.currentThread().interrupt(); 
      } 
    } 
  }
  
  @GwtIncompatible
  public static void joinUninterruptibly(Thread toJoin, Duration timeout) {
    joinUninterruptibly(toJoin, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @GwtIncompatible
  public static void joinUninterruptibly(Thread toJoin, long timeout, TimeUnit unit) {
    Preconditions.checkNotNull(toJoin);
    boolean interrupted = false;
    try {
      long remainingNanos = unit.toNanos(timeout);
      long end = System.nanoTime() + remainingNanos;
      while (true) {
        try {
          TimeUnit.NANOSECONDS.timedJoin(toJoin, remainingNanos);
          return;
        } catch (InterruptedException e) {
          interrupted = true;
          remainingNanos = end - System.nanoTime();
        } 
      } 
    } finally {
      if (interrupted)
        Thread.currentThread().interrupt(); 
    } 
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
    boolean interrupted = false;
    while (true) {
      try {
        return future.get();
      } catch (InterruptedException e) {
      
      } finally {
        if (interrupted)
          Thread.currentThread().interrupt(); 
      } 
    } 
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  @GwtIncompatible
  public static <V> V getUninterruptibly(Future<V> future, Duration timeout) throws ExecutionException, TimeoutException {
    return getUninterruptibly(future, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  @GwtIncompatible
  public static <V> V getUninterruptibly(Future<V> future, long timeout, TimeUnit unit) throws ExecutionException, TimeoutException {
    boolean interrupted = false;
    try {
      long remainingNanos = unit.toNanos(timeout);
      long end = System.nanoTime() + remainingNanos;
      while (true) {
        try {
          return future.get(remainingNanos, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
          interrupted = true;
          remainingNanos = end - System.nanoTime();
        } 
      } 
    } finally {
      if (interrupted)
        Thread.currentThread().interrupt(); 
    } 
  }
  
  @GwtIncompatible
  public static <E> E takeUninterruptibly(BlockingQueue<E> queue) {
    boolean interrupted = false;
    while (true) {
      try {
        return queue.take();
      } catch (InterruptedException e) {
      
      } finally {
        if (interrupted)
          Thread.currentThread().interrupt(); 
      } 
    } 
  }
  
  @GwtIncompatible
  public static <E> void putUninterruptibly(BlockingQueue<E> queue, E element) {
    boolean interrupted = false;
    while (true) {
      try {
        queue.put(element);
        return;
      } catch (InterruptedException e) {
      
      } finally {
        if (interrupted)
          Thread.currentThread().interrupt(); 
      } 
    } 
  }
  
  @GwtIncompatible
  public static void sleepUninterruptibly(Duration sleepFor) {
    sleepUninterruptibly(Internal.toNanosSaturated(sleepFor), TimeUnit.NANOSECONDS);
  }
  
  @GwtIncompatible
  public static void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
    boolean interrupted = false;
    try {
      long remainingNanos = unit.toNanos(sleepFor);
      long end = System.nanoTime() + remainingNanos;
      while (true) {
        try {
          TimeUnit.NANOSECONDS.sleep(remainingNanos);
          return;
        } catch (InterruptedException e) {
          interrupted = true;
          remainingNanos = end - System.nanoTime();
        } 
      } 
    } finally {
      if (interrupted)
        Thread.currentThread().interrupt(); 
    } 
  }
  
  @GwtIncompatible
  public static boolean tryAcquireUninterruptibly(Semaphore semaphore, Duration timeout) {
    return tryAcquireUninterruptibly(semaphore, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @GwtIncompatible
  public static boolean tryAcquireUninterruptibly(Semaphore semaphore, long timeout, TimeUnit unit) {
    return tryAcquireUninterruptibly(semaphore, 1, timeout, unit);
  }
  
  @GwtIncompatible
  public static boolean tryAcquireUninterruptibly(Semaphore semaphore, int permits, Duration timeout) {
    return tryAcquireUninterruptibly(semaphore, permits, 
        Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @GwtIncompatible
  public static boolean tryAcquireUninterruptibly(Semaphore semaphore, int permits, long timeout, TimeUnit unit) {
    boolean interrupted = false;
    try {
      long remainingNanos = unit.toNanos(timeout);
      long end = System.nanoTime() + remainingNanos;
      while (true) {
        try {
          return semaphore.tryAcquire(permits, remainingNanos, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
          interrupted = true;
          remainingNanos = end - System.nanoTime();
        } 
      } 
    } finally {
      if (interrupted)
        Thread.currentThread().interrupt(); 
    } 
  }
  
  @GwtIncompatible
  public static boolean tryLockUninterruptibly(Lock lock, Duration timeout) {
    return tryLockUninterruptibly(lock, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @GwtIncompatible
  public static boolean tryLockUninterruptibly(Lock lock, long timeout, TimeUnit unit) {
    boolean interrupted = false;
    try {
      long remainingNanos = unit.toNanos(timeout);
      long end = System.nanoTime() + remainingNanos;
      while (true) {
        try {
          return lock.tryLock(remainingNanos, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
          interrupted = true;
          remainingNanos = end - System.nanoTime();
        } 
      } 
    } finally {
      if (interrupted)
        Thread.currentThread().interrupt(); 
    } 
  }
  
  @GwtIncompatible
  public static void awaitTerminationUninterruptibly(ExecutorService executor) {
    Verify.verify(awaitTerminationUninterruptibly(executor, Long.MAX_VALUE, TimeUnit.NANOSECONDS));
  }
  
  @GwtIncompatible
  public static boolean awaitTerminationUninterruptibly(ExecutorService executor, Duration timeout) {
    return awaitTerminationUninterruptibly(executor, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @GwtIncompatible
  public static boolean awaitTerminationUninterruptibly(ExecutorService executor, long timeout, TimeUnit unit) {
    boolean interrupted = false;
    try {
      long remainingNanos = unit.toNanos(timeout);
      long end = System.nanoTime() + remainingNanos;
      while (true) {
        try {
          return executor.awaitTermination(remainingNanos, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
          interrupted = true;
          remainingNanos = end - System.nanoTime();
        } 
      } 
    } finally {
      if (interrupted)
        Thread.currentThread().interrupt(); 
    } 
  }
}
