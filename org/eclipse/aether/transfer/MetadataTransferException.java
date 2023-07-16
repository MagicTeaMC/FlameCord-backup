package org.eclipse.aether.transfer;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;

public class MetadataTransferException extends RepositoryException {
  private final transient Metadata metadata;
  
  private final transient RemoteRepository repository;
  
  private final boolean fromCache;
  
  static String getString(String prefix, RemoteRepository repository) {
    if (repository == null)
      return ""; 
    return prefix + repository.getId() + " (" + repository.getUrl() + ")";
  }
  
  public MetadataTransferException(Metadata metadata, RemoteRepository repository, String message) {
    this(metadata, repository, message, false);
  }
  
  public MetadataTransferException(Metadata metadata, RemoteRepository repository, String message, boolean fromCache) {
    super(message);
    this.metadata = metadata;
    this.repository = repository;
    this.fromCache = fromCache;
  }
  
  public MetadataTransferException(Metadata metadata, RemoteRepository repository, Throwable cause) {
    this(metadata, repository, "Could not transfer metadata " + metadata + getString(" from/to ", repository) + 
        getMessage(": ", cause), cause);
  }
  
  public MetadataTransferException(Metadata metadata, RemoteRepository repository, String message, Throwable cause) {
    super(message, cause);
    this.metadata = metadata;
    this.repository = repository;
    this.fromCache = false;
  }
  
  public Metadata getMetadata() {
    return this.metadata;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public boolean isFromCache() {
    return this.fromCache;
  }
}
