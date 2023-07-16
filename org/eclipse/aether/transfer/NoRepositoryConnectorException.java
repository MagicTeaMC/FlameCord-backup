package org.eclipse.aether.transfer;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.repository.RemoteRepository;

public class NoRepositoryConnectorException extends RepositoryException {
  private final transient RemoteRepository repository;
  
  public NoRepositoryConnectorException(RemoteRepository repository) {
    this(repository, toMessage(repository));
  }
  
  public NoRepositoryConnectorException(RemoteRepository repository, String message) {
    super(message);
    this.repository = repository;
  }
  
  public NoRepositoryConnectorException(RemoteRepository repository, Throwable cause) {
    this(repository, toMessage(repository), cause);
  }
  
  public NoRepositoryConnectorException(RemoteRepository repository, String message, Throwable cause) {
    super(message, cause);
    this.repository = repository;
  }
  
  private static String toMessage(RemoteRepository repository) {
    if (repository != null)
      return "No connector available to access repository " + repository.getId() + " (" + repository.getUrl() + ") of type " + repository
        .getContentType(); 
    return "No connector available to access repository";
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
}
