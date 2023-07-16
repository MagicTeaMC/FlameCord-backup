package org.eclipse.aether.resolution;

import org.eclipse.aether.RepositoryException;

public class DependencyResolutionException extends RepositoryException {
  private final transient DependencyResult result;
  
  public DependencyResolutionException(DependencyResult result, Throwable cause) {
    super(getMessage(cause), cause);
    this.result = result;
  }
  
  public DependencyResolutionException(DependencyResult result, String message, Throwable cause) {
    super(message, cause);
    this.result = result;
  }
  
  private static String getMessage(Throwable cause) {
    String msg = null;
    if (cause != null)
      msg = cause.getMessage(); 
    if (msg == null || msg.length() <= 0)
      msg = "Could not resolve transitive dependencies"; 
    return msg;
  }
  
  public DependencyResult getResult() {
    return this.result;
  }
}
