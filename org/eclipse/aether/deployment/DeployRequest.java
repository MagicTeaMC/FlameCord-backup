package org.eclipse.aether.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;

public final class DeployRequest {
  private Collection<Artifact> artifacts = Collections.emptyList();
  
  private Collection<Metadata> metadata = Collections.emptyList();
  
  private RemoteRepository repository;
  
  private RequestTrace trace;
  
  public Collection<Artifact> getArtifacts() {
    return this.artifacts;
  }
  
  public DeployRequest setArtifacts(Collection<Artifact> artifacts) {
    if (artifacts == null) {
      this.artifacts = Collections.emptyList();
    } else {
      this.artifacts = artifacts;
    } 
    return this;
  }
  
  public DeployRequest addArtifact(Artifact artifact) {
    if (artifact != null) {
      if (this.artifacts.isEmpty())
        this.artifacts = new ArrayList<>(); 
      this.artifacts.add(artifact);
    } 
    return this;
  }
  
  public Collection<Metadata> getMetadata() {
    return this.metadata;
  }
  
  public DeployRequest setMetadata(Collection<Metadata> metadata) {
    if (metadata == null) {
      this.metadata = Collections.emptyList();
    } else {
      this.metadata = metadata;
    } 
    return this;
  }
  
  public DeployRequest addMetadata(Metadata metadata) {
    if (metadata != null) {
      if (this.metadata.isEmpty())
        this.metadata = new ArrayList<>(); 
      this.metadata.add(metadata);
    } 
    return this;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public DeployRequest setRepository(RemoteRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public RequestTrace getTrace() {
    return this.trace;
  }
  
  public DeployRequest setTrace(RequestTrace trace) {
    this.trace = trace;
    return this;
  }
  
  public String toString() {
    return getArtifacts() + ", " + getMetadata() + " > " + getRepository();
  }
}
