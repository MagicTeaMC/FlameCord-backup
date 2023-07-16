package com.maxmind.db;

public class ParameterNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  ParameterNotFoundException(String message) {
    super(message);
  }
}
