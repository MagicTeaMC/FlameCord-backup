package org.eclipse.aether.transfer;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.repository.RemoteRepository;

public class NoRepositoryLayoutException extends RepositoryException {
  private final transient RemoteRepository repository;
  
  public NoRepositoryLayoutException(RemoteRepository repository) {
    this(repository, toMessage(repository));
  }
  
  public NoRepositoryLayoutException(RemoteRepository repository, String message) {
    super(message);
    this.repository = repository;
  }
  
  public NoRepositoryLayoutException(RemoteRepository repository, Throwable cause) {
    this(repository, toMessage(repository), cause);
  }
  
  public NoRepositoryLayoutException(RemoteRepository repository, String message, Throwable cause) {
    super(message, cause);
    this.repository = repository;
  }
  
  private static String toMessage(RemoteRepository repository) {
    if (repository != null)
      return "Unsupported repository layout " + repository.getContentType(); 
    return "Unsupported repository layout";
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
}
