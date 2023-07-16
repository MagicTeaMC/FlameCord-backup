package com.lmax.disruptor;

public interface WorkHandler<T> {
  void onEvent(T paramT) throws Exception;
}
