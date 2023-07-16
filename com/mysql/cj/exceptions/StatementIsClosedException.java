package com.mysql.cj.exceptions;

public class StatementIsClosedException extends CJException {
  private static final long serialVersionUID = -4214028635985851906L;
  
  public StatementIsClosedException() {
    setSQLState("S1009");
  }
  
  public StatementIsClosedException(String message) {
    super(message);
    setSQLState("S1009");
  }
  
  public StatementIsClosedException(String message, Throwable cause) {
    super(message, cause);
    setSQLState("S1009");
  }
  
  public StatementIsClosedException(Throwable cause) {
    super(cause);
    setSQLState("S1009");
  }
  
  protected StatementIsClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    setSQLState("S1009");
  }
}
