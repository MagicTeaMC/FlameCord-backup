package com.lmax.disruptor;

public interface ExceptionHandler<T> {
  void handleEventException(Throwable paramThrowable, long paramLong, T paramT);
  
  void handleOnStartException(Throwable paramThrowable);
  
  void handleOnShutdownException(Throwable paramThrowable);
}
