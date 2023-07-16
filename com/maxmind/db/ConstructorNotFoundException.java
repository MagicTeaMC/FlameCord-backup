package com.maxmind.db;

public class ConstructorNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  ConstructorNotFoundException(String message) {
    super(message);
  }
}
