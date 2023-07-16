package org.eclipse.aether.deployment;

import org.eclipse.aether.RepositoryException;

public class DeploymentException extends RepositoryException {
  public DeploymentException(String message) {
    super(message);
  }
  
  public DeploymentException(String message, Throwable cause) {
    super(message, cause);
  }
}
