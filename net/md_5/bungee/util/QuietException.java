package net.md_5.bungee.util;

public class QuietException extends RuntimeException {
  public QuietException(String message) {
    super(message);
  }
  
  public Throwable initCause(Throwable cause) {
    return this;
  }
  
  public Throwable fillInStackTrace() {
    return this;
  }
}
