package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.Artifact;

public class CyclicDependencyException extends ArtifactResolutionException {
  private Artifact artifact;
  
  public CyclicDependencyException(String message, Artifact artifact) {
    super(message, artifact);
    this.artifact = artifact;
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
}
