package com.lmax.disruptor;

public final class ExceptionHandlers {
  public static ExceptionHandler<Object> defaultHandler() {
    return DefaultExceptionHandlerHolder.HANDLER;
  }
  
  private static final class DefaultExceptionHandlerHolder {
    private static final ExceptionHandler<Object> HANDLER = new FatalExceptionHandler();
  }
}
