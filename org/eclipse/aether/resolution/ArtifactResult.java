package org.eclipse.aether.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.ArtifactRepository;

public final class ArtifactResult {
  private final ArtifactRequest request;
  
  private List<Exception> exceptions;
  
  private Artifact artifact;
  
  private ArtifactRepository repository;
  
  public ArtifactResult(ArtifactRequest request) {
    this.request = Objects.<ArtifactRequest>requireNonNull(request, "artifact request cannot be null");
    this.exceptions = Collections.emptyList();
  }
  
  public ArtifactRequest getRequest() {
    return this.request;
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public ArtifactResult setArtifact(Artifact artifact) {
    this.artifact = artifact;
    return this;
  }
  
  public List<Exception> getExceptions() {
    return this.exceptions;
  }
  
  public ArtifactResult addException(Exception exception) {
    if (exception != null) {
      if (this.exceptions.isEmpty())
        this.exceptions = new ArrayList<>(); 
      this.exceptions.add(exception);
    } 
    return this;
  }
  
  public ArtifactRepository getRepository() {
    return this.repository;
  }
  
  public ArtifactResult setRepository(ArtifactRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public boolean isResolved() {
    return (getArtifact() != null && getArtifact().getFile() != null);
  }
  
  public boolean isMissing() {
    for (Exception e : getExceptions()) {
      if (!(e instanceof org.eclipse.aether.transfer.ArtifactNotFoundException))
        return false; 
    } 
    return !isResolved();
  }
  
  public String toString() {
    return getArtifact() + " < " + getRepository();
  }
}
