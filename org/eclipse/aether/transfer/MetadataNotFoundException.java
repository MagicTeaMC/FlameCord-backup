package org.eclipse.aether.transfer;

import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;

public class MetadataNotFoundException extends MetadataTransferException {
  public MetadataNotFoundException(Metadata metadata, LocalRepository repository) {
    super(metadata, (RemoteRepository)null, "Could not find metadata " + metadata + getString(" in ", repository));
  }
  
  private static String getString(String prefix, LocalRepository repository) {
    if (repository == null)
      return ""; 
    return prefix + repository.getId() + " (" + repository.getBasedir() + ")";
  }
  
  public MetadataNotFoundException(Metadata metadata, RemoteRepository repository) {
    super(metadata, repository, "Could not find metadata " + metadata + getString(" in ", repository));
  }
  
  public MetadataNotFoundException(Metadata metadata, RemoteRepository repository, String message) {
    super(metadata, repository, message);
  }
  
  public MetadataNotFoundException(Metadata metadata, RemoteRepository repository, String message, boolean fromCache) {
    super(metadata, repository, message, fromCache);
  }
  
  public MetadataNotFoundException(Metadata metadata, RemoteRepository repository, String message, Throwable cause) {
    super(metadata, repository, message, cause);
  }
}
