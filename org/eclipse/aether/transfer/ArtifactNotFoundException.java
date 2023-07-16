package org.eclipse.aether.transfer;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

public class ArtifactNotFoundException extends ArtifactTransferException {
  public ArtifactNotFoundException(Artifact artifact, RemoteRepository repository) {
    super(artifact, repository, getMessage(artifact, repository));
  }
  
  private static String getMessage(Artifact artifact, RemoteRepository repository) {
    StringBuilder buffer = new StringBuilder(256);
    buffer.append("Could not find artifact ").append(artifact);
    buffer.append(getString(" in ", repository));
    if (artifact != null) {
      String localPath = artifact.getProperty("localPath", null);
      if (localPath != null && repository == null)
        buffer.append(" at specified path ").append(localPath); 
      String downloadUrl = artifact.getProperty("downloadUrl", null);
      if (downloadUrl != null)
        buffer.append(", try downloading from ").append(downloadUrl); 
    } 
    return buffer.toString();
  }
  
  public ArtifactNotFoundException(Artifact artifact, RemoteRepository repository, String message) {
    super(artifact, repository, message);
  }
  
  public ArtifactNotFoundException(Artifact artifact, RemoteRepository repository, String message, boolean fromCache) {
    super(artifact, repository, message, fromCache);
  }
  
  public ArtifactNotFoundException(Artifact artifact, RemoteRepository repository, String message, Throwable cause) {
    super(artifact, repository, message, cause);
  }
}
