package com.maxmind.db;

import java.io.IOException;

public class ClosedDatabaseException extends IOException {
  private static final long serialVersionUID = 1L;
  
  ClosedDatabaseException() {
    super("The MaxMind DB has been closed.");
  }
}
