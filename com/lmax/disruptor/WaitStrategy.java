package com.lmax.disruptor;

public interface WaitStrategy {
  long waitFor(long paramLong, Sequence paramSequence1, Sequence paramSequence2, SequenceBarrier paramSequenceBarrier) throws AlertException, InterruptedException, TimeoutException;
  
  void signalAllWhenBlocking();
}
