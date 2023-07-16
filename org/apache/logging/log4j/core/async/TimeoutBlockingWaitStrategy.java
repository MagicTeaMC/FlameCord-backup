package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import java.util.concurrent.TimeUnit;

class TimeoutBlockingWaitStrategy implements WaitStrategy {
  private final Object mutex = new Object();
  
  private final long timeoutInNanos;
  
  private static final int ONE_MILLISECOND_IN_NANOSECONDS = 1000000;
  
  public TimeoutBlockingWaitStrategy(long timeout, TimeUnit units) {
    this.timeoutInNanos = units.toNanos(timeout);
  }
  
  public long waitFor(long sequence, Sequence cursorSequence, Sequence dependentSequence, SequenceBarrier barrier) throws AlertException, InterruptedException, TimeoutException {
    long timeoutNanos = this.timeoutInNanos;
    if (cursorSequence.get() < sequence)
      synchronized (this.mutex) {
        while (cursorSequence.get() < sequence) {
          barrier.checkAlert();
          timeoutNanos = awaitNanos(this.mutex, timeoutNanos);
          if (timeoutNanos <= 0L)
            throw TimeoutException.INSTANCE; 
        } 
      }  
    long availableSequence;
    while ((availableSequence = dependentSequence.get()) < sequence)
      barrier.checkAlert(); 
    return availableSequence;
  }
  
  public void signalAllWhenBlocking() {
    synchronized (this.mutex) {
      this.mutex.notifyAll();
    } 
  }
  
  public String toString() {
    return "TimeoutBlockingWaitStrategy{mutex=" + this.mutex + ", timeoutInNanos=" + this.timeoutInNanos + '}';
  }
  
  private static long awaitNanos(Object mutex, long timeoutNanos) throws InterruptedException {
    long millis = timeoutNanos / 1000000L;
    long nanos = timeoutNanos % 1000000L;
    long t0 = System.nanoTime();
    mutex.wait(millis, (int)nanos);
    long t1 = System.nanoTime();
    return timeoutNanos - t1 - t0;
  }
}
