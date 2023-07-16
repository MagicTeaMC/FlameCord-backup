package org.eclipse.aether.installation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

public final class InstallResult {
  private final InstallRequest request;
  
  private Collection<Artifact> artifacts;
  
  private Collection<Metadata> metadata;
  
  public InstallResult(InstallRequest request) {
    this.request = Objects.<InstallRequest>requireNonNull(request, "install request cannot be null");
    this.artifacts = Collections.emptyList();
    this.metadata = Collections.emptyList();
  }
  
  public InstallRequest getRequest() {
    return this.request;
  }
  
  public Collection<Artifact> getArtifacts() {
    return this.artifacts;
  }
  
  public InstallResult setArtifacts(Collection<Artifact> artifacts) {
    if (artifacts == null) {
      this.artifacts = Collections.emptyList();
    } else {
      this.artifacts = artifacts;
    } 
    return this;
  }
  
  public InstallResult addArtifact(Artifact artifact) {
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
  
  public InstallResult setMetadata(Collection<Metadata> metadata) {
    if (metadata == null) {
      this.metadata = Collections.emptyList();
    } else {
      this.metadata = metadata;
    } 
    return this;
  }
  
  public InstallResult addMetadata(Metadata metadata) {
    if (metadata != null) {
      if (this.metadata.isEmpty())
        this.metadata = new ArrayList<>(); 
      this.metadata.add(metadata);
    } 
    return this;
  }
  
  public String toString() {
    return getArtifacts() + ", " + getMetadata();
  }
}
