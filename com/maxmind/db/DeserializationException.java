package com.maxmind.db;

public class DeserializationException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  DeserializationException() {
    super("Database value cannot be deserialized into the type.");
  }
  
  DeserializationException(String message) {
    super(message);
  }
}
