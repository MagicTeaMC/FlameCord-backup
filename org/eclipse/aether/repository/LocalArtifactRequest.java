package org.eclipse.aether.repository;

import java.util.Collections;
import java.util.List;
import org.eclipse.aether.artifact.Artifact;

public final class LocalArtifactRequest {
  private Artifact artifact;
  
  private String context = "";
  
  private List<RemoteRepository> repositories = Collections.emptyList();
  
  public LocalArtifactRequest() {}
  
  public LocalArtifactRequest(Artifact artifact, List<RemoteRepository> repositories, String context) {
    setArtifact(artifact);
    setRepositories(repositories);
    setContext(context);
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public LocalArtifactRequest setArtifact(Artifact artifact) {
    this.artifact = artifact;
    return this;
  }
  
  public String getContext() {
    return this.context;
  }
  
  public LocalArtifactRequest setContext(String context) {
    this.context = (context != null) ? context : "";
    return this;
  }
  
  public List<RemoteRepository> getRepositories() {
    return this.repositories;
  }
  
  public LocalArtifactRequest setRepositories(List<RemoteRepository> repositories) {
    if (repositories != null) {
      this.repositories = repositories;
    } else {
      this.repositories = Collections.emptyList();
    } 
    return this;
  }
  
  public String toString() {
    return getArtifact() + " @ " + getRepositories();
  }
}
