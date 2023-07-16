package com.lmax.disruptor;

public interface EventTranslatorVararg<T> {
  void translateTo(T paramT, long paramLong, Object... paramVarArgs);
}
