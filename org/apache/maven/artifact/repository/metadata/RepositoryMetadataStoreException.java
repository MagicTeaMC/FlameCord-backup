package org.apache.maven.artifact.repository.metadata;

public class RepositoryMetadataStoreException extends Exception {
  public RepositoryMetadataStoreException(String message) {
    super(message);
  }
  
  public RepositoryMetadataStoreException(String message, Exception e) {
    super(message, e);
  }
}
