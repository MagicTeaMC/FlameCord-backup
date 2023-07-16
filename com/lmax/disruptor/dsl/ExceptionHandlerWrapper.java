package com.lmax.disruptor.dsl;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.ExceptionHandlers;

public class ExceptionHandlerWrapper<T> implements ExceptionHandler<T> {
  private ExceptionHandler<? super T> delegate;
  
  public void switchTo(ExceptionHandler<? super T> exceptionHandler) {
    this.delegate = exceptionHandler;
  }
  
  public void handleEventException(Throwable ex, long sequence, T event) {
    getExceptionHandler().handleEventException(ex, sequence, event);
  }
  
  public void handleOnStartException(Throwable ex) {
    getExceptionHandler().handleOnStartException(ex);
  }
  
  public void handleOnShutdownException(Throwable ex) {
    getExceptionHandler().handleOnShutdownException(ex);
  }
  
  private ExceptionHandler<? super T> getExceptionHandler() {
    ExceptionHandler<? super T> handler = this.delegate;
    if (handler == null)
      return ExceptionHandlers.defaultHandler(); 
    return handler;
  }
}
