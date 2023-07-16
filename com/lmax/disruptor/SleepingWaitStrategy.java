package com.lmax.disruptor;

import java.util.concurrent.locks.LockSupport;

public final class SleepingWaitStrategy implements WaitStrategy {
  private static final int DEFAULT_RETRIES = 200;
  
  private static final long DEFAULT_SLEEP = 100L;
  
  private final int retries;
  
  private final long sleepTimeNs;
  
  public SleepingWaitStrategy() {
    this(200, 100L);
  }
  
  public SleepingWaitStrategy(int retries) {
    this(retries, 100L);
  }
  
  public SleepingWaitStrategy(int retries, long sleepTimeNs) {
    this.retries = retries;
    this.sleepTimeNs = sleepTimeNs;
  }
  
  public long waitFor(long sequence, Sequence cursor, Sequence dependentSequence, SequenceBarrier barrier) throws AlertException {
    int counter = this.retries;
    long availableSequence;
    while ((availableSequence = dependentSequence.get()) < sequence)
      counter = applyWaitMethod(barrier, counter); 
    return availableSequence;
  }
  
  public void signalAllWhenBlocking() {}
  
  private int applyWaitMethod(SequenceBarrier barrier, int counter) throws AlertException {
    barrier.checkAlert();
    if (counter > 100) {
      counter--;
    } else if (counter > 0) {
      counter--;
      Thread.yield();
    } else {
      LockSupport.parkNanos(this.sleepTimeNs);
    } 
    return counter;
  }
}
