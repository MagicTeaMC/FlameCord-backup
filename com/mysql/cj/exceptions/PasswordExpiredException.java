package com.mysql.cj.exceptions;

public class PasswordExpiredException extends CJException {
  private static final long serialVersionUID = -3807215681364413250L;
  
  public PasswordExpiredException() {
    setVendorCode(1820);
  }
  
  public PasswordExpiredException(String message) {
    super(message);
    setVendorCode(1820);
  }
  
  public PasswordExpiredException(String message, Throwable cause) {
    super(message, cause);
    setVendorCode(1820);
  }
  
  public PasswordExpiredException(Throwable cause) {
    super(cause);
    setVendorCode(1820);
  }
  
  protected PasswordExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    setVendorCode(1820);
  }
}
