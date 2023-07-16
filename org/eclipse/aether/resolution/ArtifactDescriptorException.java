package org.eclipse.aether.resolution;

import org.eclipse.aether.RepositoryException;

public class ArtifactDescriptorException extends RepositoryException {
  private final transient ArtifactDescriptorResult result;
  
  public ArtifactDescriptorException(ArtifactDescriptorResult result) {
    super("Failed to read artifact descriptor" + ((result != null) ? (" for " + result
        .getRequest().getArtifact()) : ""), getCause(result));
    this.result = result;
  }
  
  public ArtifactDescriptorException(ArtifactDescriptorResult result, String message) {
    super(message, getCause(result));
    this.result = result;
  }
  
  public ArtifactDescriptorException(ArtifactDescriptorResult result, String message, Throwable cause) {
    super(message, cause);
    this.result = result;
  }
  
  public ArtifactDescriptorResult getResult() {
    return this.result;
  }
  
  private static Throwable getCause(ArtifactDescriptorResult result) {
    Throwable cause = null;
    if (result != null && !result.getExceptions().isEmpty())
      cause = result.getExceptions().get(0); 
    return cause;
  }
}
