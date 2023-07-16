package com.lmax.disruptor;

public interface Sequencer extends Cursored, Sequenced {
  public static final long INITIAL_CURSOR_VALUE = -1L;
  
  void claim(long paramLong);
  
  boolean isAvailable(long paramLong);
  
  void addGatingSequences(Sequence... paramVarArgs);
  
  boolean removeGatingSequence(Sequence paramSequence);
  
  SequenceBarrier newBarrier(Sequence... paramVarArgs);
  
  long getMinimumSequence();
  
  long getHighestPublishedSequence(long paramLong1, long paramLong2);
  
  <T> EventPoller<T> newPoller(DataProvider<T> paramDataProvider, Sequence... paramVarArgs);
}
