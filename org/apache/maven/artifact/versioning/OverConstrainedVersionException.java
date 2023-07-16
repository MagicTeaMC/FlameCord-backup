package org.apache.maven.artifact.versioning;

import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;

public class OverConstrainedVersionException extends ArtifactResolutionException {
  public OverConstrainedVersionException(String msg, Artifact artifact) {
    super(msg, artifact);
  }
  
  public OverConstrainedVersionException(String msg, Artifact artifact, List<ArtifactRepository> remoteRepositories) {
    super(msg, artifact, remoteRepositories);
  }
}
