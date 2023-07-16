package org.eclipse.aether.resolution;

import org.eclipse.aether.RepositoryException;

public class VersionRangeResolutionException extends RepositoryException {
  private final transient VersionRangeResult result;
  
  public VersionRangeResolutionException(VersionRangeResult result) {
    super(getMessage(result), getCause(result));
    this.result = result;
  }
  
  private static String getMessage(VersionRangeResult result) {
    StringBuilder buffer = new StringBuilder(256);
    buffer.append("Failed to resolve version range");
    if (result != null) {
      buffer.append(" for ").append(result.getRequest().getArtifact());
      if (!result.getExceptions().isEmpty())
        buffer.append(": ").append(((Exception)result.getExceptions().iterator().next()).getMessage()); 
    } 
    return buffer.toString();
  }
  
  private static Throwable getCause(VersionRangeResult result) {
    Throwable cause = null;
    if (result != null && !result.getExceptions().isEmpty())
      cause = result.getExceptions().get(0); 
    return cause;
  }
  
  public VersionRangeResolutionException(VersionRangeResult result, String message) {
    super(message);
    this.result = result;
  }
  
  public VersionRangeResolutionException(VersionRangeResult result, String message, Throwable cause) {
    super(message, cause);
    this.result = result;
  }
  
  public VersionRangeResult getResult() {
    return this.result;
  }
}
