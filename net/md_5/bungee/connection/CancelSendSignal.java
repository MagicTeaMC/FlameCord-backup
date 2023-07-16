package net.md_5.bungee.connection;

public class CancelSendSignal extends Error {
  public static final CancelSendSignal INSTANCE = new CancelSendSignal();
  
  public Throwable initCause(Throwable cause) {
    return this;
  }
  
  public Throwable fillInStackTrace() {
    return this;
  }
}
