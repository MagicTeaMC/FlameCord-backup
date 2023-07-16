package com.lmax.disruptor;

public final class AlertException extends Exception {
  public static final AlertException INSTANCE = new AlertException();
  
  public Throwable fillInStackTrace() {
    return this;
  }
}
