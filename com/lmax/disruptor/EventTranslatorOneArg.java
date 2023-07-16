package com.lmax.disruptor;

public interface EventTranslatorOneArg<T, A> {
  void translateTo(T paramT, long paramLong, A paramA);
}
