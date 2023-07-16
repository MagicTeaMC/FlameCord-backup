package org.eclipse.aether;

public class RepositoryException extends Exception {
  public RepositoryException(String message) {
    super(message);
  }
  
  public RepositoryException(String message, Throwable cause) {
    super(message, cause);
  }
  
  protected static String getMessage(String prefix, Throwable cause) {
    String msg = "";
    if (cause != null) {
      msg = cause.getMessage();
      if (msg == null || msg.length() <= 0)
        msg = cause.getClass().getSimpleName(); 
      msg = prefix + msg;
    } 
    return msg;
  }
}
