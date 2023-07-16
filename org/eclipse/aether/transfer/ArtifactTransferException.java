package org.eclipse.aether.transfer;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

public class ArtifactTransferException extends RepositoryException {
  private final transient Artifact artifact;
  
  private final transient RemoteRepository repository;
  
  private final boolean fromCache;
  
  static String getString(String prefix, RemoteRepository repository) {
    if (repository == null)
      return ""; 
    return prefix + repository.getId() + " (" + repository.getUrl() + ")";
  }
  
  public ArtifactTransferException(Artifact artifact, RemoteRepository repository, String message) {
    this(artifact, repository, message, false);
  }
  
  public ArtifactTransferException(Artifact artifact, RemoteRepository repository, String message, boolean fromCache) {
    super(message);
    this.artifact = artifact;
    this.repository = repository;
    this.fromCache = fromCache;
  }
  
  public ArtifactTransferException(Artifact artifact, RemoteRepository repository, Throwable cause) {
    this(artifact, repository, "Could not transfer artifact " + artifact + getString(" from/to ", repository) + 
        getMessage(": ", cause), cause);
  }
  
  public ArtifactTransferException(Artifact artifact, RemoteRepository repository, String message, Throwable cause) {
    super(message, cause);
    this.artifact = artifact;
    this.repository = repository;
    this.fromCache = false;
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public boolean isFromCache() {
    return this.fromCache;
  }
}
