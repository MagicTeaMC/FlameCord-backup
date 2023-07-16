package com.lmax.disruptor;

public interface EventTranslatorThreeArg<T, A, B, C> {
  void translateTo(T paramT, long paramLong, A paramA, B paramB, C paramC);
}
