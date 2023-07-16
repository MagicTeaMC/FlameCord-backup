package com.mysql.cj.xdevapi;

import com.mysql.cj.protocol.Warning;

public class WarningImpl implements Warning {
  private Warning message;
  
  public WarningImpl(Warning message) {
    this.message = message;
  }
  
  public int getLevel() {
    return this.message.getLevel();
  }
  
  public long getCode() {
    return this.message.getCode();
  }
  
  public String getMessage() {
    return this.message.getMessage();
  }
}
