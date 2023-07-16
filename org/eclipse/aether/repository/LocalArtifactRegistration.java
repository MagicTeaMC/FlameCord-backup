package org.eclipse.aether.repository;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.aether.artifact.Artifact;

public final class LocalArtifactRegistration {
  private Artifact artifact;
  
  private RemoteRepository repository;
  
  private Collection<String> contexts = Collections.emptyList();
  
  public LocalArtifactRegistration() {}
  
  public LocalArtifactRegistration(Artifact artifact) {
    setArtifact(artifact);
  }
  
  public LocalArtifactRegistration(Artifact artifact, RemoteRepository repository, Collection<String> contexts) {
    setArtifact(artifact);
    setRepository(repository);
    setContexts(contexts);
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public LocalArtifactRegistration setArtifact(Artifact artifact) {
    this.artifact = artifact;
    return this;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public LocalArtifactRegistration setRepository(RemoteRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public Collection<String> getContexts() {
    return this.contexts;
  }
  
  public LocalArtifactRegistration setContexts(Collection<String> contexts) {
    if (contexts != null) {
      this.contexts = contexts;
    } else {
      this.contexts = Collections.emptyList();
    } 
    return this;
  }
}
