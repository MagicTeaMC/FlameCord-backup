package com.mysql.cj.exceptions;

public class DataReadException extends CJException {
  private static final long serialVersionUID = 1684265521187171525L;
  
  public DataReadException(Exception cause) {
    super(cause);
    setSQLState("S1009");
  }
  
  public DataReadException(String msg) {
    super(msg);
    setSQLState("S1009");
  }
}
