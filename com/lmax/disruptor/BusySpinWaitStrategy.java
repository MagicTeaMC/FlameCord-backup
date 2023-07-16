package com.lmax.disruptor;

import com.lmax.disruptor.util.ThreadHints;

public final class BusySpinWaitStrategy implements WaitStrategy {
  public long waitFor(long sequence, Sequence cursor, Sequence dependentSequence, SequenceBarrier barrier) throws AlertException, InterruptedException {
    long availableSequence;
    while ((availableSequence = dependentSequence.get()) < sequence) {
      barrier.checkAlert();
      ThreadHints.onSpinWait();
    } 
    return availableSequence;
  }
  
  public void signalAllWhenBlocking() {}
}
