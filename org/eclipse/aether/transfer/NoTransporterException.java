package org.eclipse.aether.transfer;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.repository.RemoteRepository;

public class NoTransporterException extends RepositoryException {
  private final transient RemoteRepository repository;
  
  public NoTransporterException(RemoteRepository repository) {
    this(repository, toMessage(repository));
  }
  
  public NoTransporterException(RemoteRepository repository, String message) {
    super(message);
    this.repository = repository;
  }
  
  public NoTransporterException(RemoteRepository repository, Throwable cause) {
    this(repository, toMessage(repository), cause);
  }
  
  public NoTransporterException(RemoteRepository repository, String message, Throwable cause) {
    super(message, cause);
    this.repository = repository;
  }
  
  private static String toMessage(RemoteRepository repository) {
    if (repository != null)
      return "Unsupported transport protocol " + repository.getProtocol(); 
    return "Unsupported transport protocol";
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
}
