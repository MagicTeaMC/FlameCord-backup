package com.lmax.disruptor;

import java.util.concurrent.TimeUnit;

public final class PhasedBackoffWaitStrategy implements WaitStrategy {
  private static final int SPIN_TRIES = 10000;
  
  private final long spinTimeoutNanos;
  
  private final long yieldTimeoutNanos;
  
  private final WaitStrategy fallbackStrategy;
  
  public PhasedBackoffWaitStrategy(long spinTimeout, long yieldTimeout, TimeUnit units, WaitStrategy fallbackStrategy) {
    this.spinTimeoutNanos = units.toNanos(spinTimeout);
    this.yieldTimeoutNanos = this.spinTimeoutNanos + units.toNanos(yieldTimeout);
    this.fallbackStrategy = fallbackStrategy;
  }
  
  public static PhasedBackoffWaitStrategy withLock(long spinTimeout, long yieldTimeout, TimeUnit units) {
    return new PhasedBackoffWaitStrategy(spinTimeout, yieldTimeout, units, new BlockingWaitStrategy());
  }
  
  public static PhasedBackoffWaitStrategy withLiteLock(long spinTimeout, long yieldTimeout, TimeUnit units) {
    return new PhasedBackoffWaitStrategy(spinTimeout, yieldTimeout, units, new LiteBlockingWaitStrategy());
  }
  
  public static PhasedBackoffWaitStrategy withSleep(long spinTimeout, long yieldTimeout, TimeUnit units) {
    return new PhasedBackoffWaitStrategy(spinTimeout, yieldTimeout, units, new SleepingWaitStrategy(0));
  }
  
  public long waitFor(long sequence, Sequence cursor, Sequence dependentSequence, SequenceBarrier barrier) throws AlertException, InterruptedException, TimeoutException {
    long startTime = 0L;
    int counter = 10000;
    while (true) {
      long availableSequence;
      if ((availableSequence = dependentSequence.get()) >= sequence)
        return availableSequence; 
      if (0 == --counter) {
        if (0L == startTime) {
          startTime = System.nanoTime();
        } else {
          long timeDelta = System.nanoTime() - startTime;
          if (timeDelta > this.yieldTimeoutNanos)
            return this.fallbackStrategy.waitFor(sequence, cursor, dependentSequence, barrier); 
          if (timeDelta > this.spinTimeoutNanos)
            Thread.yield(); 
        } 
        counter = 10000;
      } 
    } 
  }
  
  public void signalAllWhenBlocking() {
    this.fallbackStrategy.signalAllWhenBlocking();
  }
}
