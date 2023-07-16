package org.eclipse.aether.repository;

import org.eclipse.aether.RepositoryException;

public class NoLocalRepositoryManagerException extends RepositoryException {
  private final transient LocalRepository repository;
  
  public NoLocalRepositoryManagerException(LocalRepository repository) {
    this(repository, toMessage(repository));
  }
  
  public NoLocalRepositoryManagerException(LocalRepository repository, String message) {
    super(message);
    this.repository = repository;
  }
  
  public NoLocalRepositoryManagerException(LocalRepository repository, Throwable cause) {
    this(repository, toMessage(repository), cause);
  }
  
  public NoLocalRepositoryManagerException(LocalRepository repository, String message, Throwable cause) {
    super(message, cause);
    this.repository = repository;
  }
  
  private static String toMessage(LocalRepository repository) {
    if (repository != null)
      return "No manager available for local repository (" + repository.getBasedir().getAbsolutePath() + ") of type " + repository
        .getContentType(); 
    return "No manager available for local repository";
  }
  
  public LocalRepository getRepository() {
    return this.repository;
  }
}
