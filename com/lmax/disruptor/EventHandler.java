package com.lmax.disruptor;

public interface EventHandler<T> {
  void onEvent(T paramT, long paramLong, boolean paramBoolean) throws Exception;
}
