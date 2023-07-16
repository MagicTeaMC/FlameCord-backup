package com.maxmind.db;

import java.io.IOException;

public class InvalidDatabaseException extends IOException {
  private static final long serialVersionUID = 6161763462364823003L;
  
  public InvalidDatabaseException(String message) {
    super(message);
  }
  
  public InvalidDatabaseException(String message, Throwable cause) {
    super(message, cause);
  }
}
