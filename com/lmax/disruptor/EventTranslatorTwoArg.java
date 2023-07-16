package com.lmax.disruptor;

public interface EventTranslatorTwoArg<T, A, B> {
  void translateTo(T paramT, long paramLong, A paramA, B paramB);
}
