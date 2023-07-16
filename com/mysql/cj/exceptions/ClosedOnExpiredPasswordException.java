package com.mysql.cj.exceptions;

public class ClosedOnExpiredPasswordException extends CJException {
  private static final long serialVersionUID = -3807215681364413250L;
  
  public ClosedOnExpiredPasswordException() {
    setVendorCode(1862);
  }
  
  public ClosedOnExpiredPasswordException(String message) {
    super(message);
    setVendorCode(1862);
  }
  
  public ClosedOnExpiredPasswordException(String message, Throwable cause) {
    super(message, cause);
    setVendorCode(1862);
  }
  
  public ClosedOnExpiredPasswordException(Throwable cause) {
    super(cause);
    setVendorCode(1862);
  }
  
  protected ClosedOnExpiredPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    setVendorCode(1862);
  }
}
