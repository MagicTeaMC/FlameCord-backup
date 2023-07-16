package com.mysql.cj.exceptions;

public class UnableToConnectException extends CJException {
  private static final long serialVersionUID = 6824175447292574109L;
  
  public UnableToConnectException() {
    setSQLState("08001");
  }
  
  public UnableToConnectException(String message) {
    super(message);
    setSQLState("08001");
  }
  
  public UnableToConnectException(String message, Throwable cause) {
    super(message, cause);
    setSQLState("08001");
  }
  
  public UnableToConnectException(Throwable cause) {
    super(cause);
    setSQLState("08001");
  }
  
  public UnableToConnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    setSQLState("08001");
  }
}
