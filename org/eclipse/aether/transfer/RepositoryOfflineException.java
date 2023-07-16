package org.eclipse.aether.transfer;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.repository.RemoteRepository;

public class RepositoryOfflineException extends RepositoryException {
  private final transient RemoteRepository repository;
  
  private static String getMessage(RemoteRepository repository) {
    if (repository == null)
      return "Cannot access remote repositories in offline mode"; 
    return "Cannot access " + repository.getId() + " (" + repository.getUrl() + ") in offline mode";
  }
  
  public RepositoryOfflineException(RemoteRepository repository) {
    super(getMessage(repository));
    this.repository = repository;
  }
  
  public RepositoryOfflineException(RemoteRepository repository, String message) {
    super(message);
    this.repository = repository;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
}
