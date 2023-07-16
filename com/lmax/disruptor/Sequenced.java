package com.lmax.disruptor;

public interface Sequenced {
  int getBufferSize();
  
  boolean hasAvailableCapacity(int paramInt);
  
  long remainingCapacity();
  
  long next();
  
  long next(int paramInt);
  
  long tryNext() throws InsufficientCapacityException;
  
  long tryNext(int paramInt) throws InsufficientCapacityException;
  
  void publish(long paramLong);
  
  void publish(long paramLong1, long paramLong2);
}
