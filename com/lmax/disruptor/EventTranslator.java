package com.lmax.disruptor;

public interface EventTranslator<T> {
  void translateTo(T paramT, long paramLong);
}
