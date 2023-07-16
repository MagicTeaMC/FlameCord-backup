package org.eclipse.aether.resolution;

import org.eclipse.aether.RepositoryException;

public class VersionResolutionException extends RepositoryException {
  private final transient VersionResult result;
  
  public VersionResolutionException(VersionResult result) {
    super(getMessage(result), getCause(result));
    this.result = result;
  }
  
  private static String getMessage(VersionResult result) {
    StringBuilder buffer = new StringBuilder(256);
    buffer.append("Failed to resolve version");
    if (result != null) {
      buffer.append(" for ").append(result.getRequest().getArtifact());
      if (!result.getExceptions().isEmpty())
        buffer.append(": ").append(((Exception)result.getExceptions().iterator().next()).getMessage()); 
    } 
    return buffer.toString();
  }
  
  private static Throwable getCause(VersionResult result) {
    Throwable cause = null;
    if (result != null && !result.getExceptions().isEmpty())
      cause = result.getExceptions().get(0); 
    return cause;
  }
  
  public VersionResolutionException(VersionResult result, String message) {
    super(message, getCause(result));
    this.result = result;
  }
  
  public VersionResolutionException(VersionResult result, String message, Throwable cause) {
    super(message, cause);
    this.result = result;
  }
  
  public VersionResult getResult() {
    return this.result;
  }
}
