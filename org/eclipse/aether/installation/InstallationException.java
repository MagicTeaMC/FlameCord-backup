package org.eclipse.aether.installation;

import org.eclipse.aether.RepositoryException;

public class InstallationException extends RepositoryException {
  public InstallationException(String message) {
    super(message);
  }
  
  public InstallationException(String message, Throwable cause) {
    super(message, cause);
  }
}
