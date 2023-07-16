package org.eclipse.aether.collection;

import org.eclipse.aether.RepositoryException;

public class DependencyCollectionException extends RepositoryException {
  private final transient CollectResult result;
  
  public DependencyCollectionException(CollectResult result) {
    super("Failed to collect dependencies for " + getSource(result), getCause(result));
    this.result = result;
  }
  
  public DependencyCollectionException(CollectResult result, String message) {
    super(message, getCause(result));
    this.result = result;
  }
  
  public DependencyCollectionException(CollectResult result, String message, Throwable cause) {
    super(message, cause);
    this.result = result;
  }
  
  public CollectResult getResult() {
    return this.result;
  }
  
  private static String getSource(CollectResult result) {
    if (result == null)
      return ""; 
    CollectRequest request = result.getRequest();
    if (request.getRoot() != null)
      return request.getRoot().toString(); 
    if (request.getRootArtifact() != null)
      return request.getRootArtifact().toString(); 
    return request.getDependencies().toString();
  }
  
  private static Throwable getCause(CollectResult result) {
    Throwable cause = null;
    if (result != null && !result.getExceptions().isEmpty())
      cause = result.getExceptions().get(0); 
    return cause;
  }
}
