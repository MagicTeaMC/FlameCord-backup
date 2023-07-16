package org.apache.maven.model.resolution;

import org.apache.maven.model.Repository;

public class InvalidRepositoryException extends Exception {
  private Repository repository;
  
  public InvalidRepositoryException(String message, Repository repository, Throwable cause) {
    super(message, cause);
    this.repository = repository;
  }
  
  public InvalidRepositoryException(String message, Repository repository) {
    super(message);
    this.repository = repository;
  }
  
  public Repository getRepository() {
    return this.repository;
  }
}
