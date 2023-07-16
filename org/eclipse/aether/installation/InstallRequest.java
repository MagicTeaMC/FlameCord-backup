package org.eclipse.aether.installation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

public final class InstallRequest {
  private Collection<Artifact> artifacts = Collections.emptyList();
  
  private Collection<Metadata> metadata = Collections.emptyList();
  
  private RequestTrace trace;
  
  public Collection<Artifact> getArtifacts() {
    return this.artifacts;
  }
  
  public InstallRequest setArtifacts(Collection<Artifact> artifacts) {
    if (artifacts == null) {
      this.artifacts = Collections.emptyList();
    } else {
      this.artifacts = artifacts;
    } 
    return this;
  }
  
  public InstallRequest addArtifact(Artifact artifact) {
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
  
  public InstallRequest setMetadata(Collection<Metadata> metadata) {
    if (metadata == null) {
      this.metadata = Collections.emptyList();
    } else {
      this.metadata = metadata;
    } 
    return this;
  }
  
  public InstallRequest addMetadata(Metadata metadata) {
    if (metadata != null) {
      if (this.metadata.isEmpty())
        this.metadata = new ArrayList<>(); 
      this.metadata.add(metadata);
    } 
    return this;
  }
  
  public RequestTrace getTrace() {
    return this.trace;
  }
  
  public InstallRequest setTrace(RequestTrace trace) {
    this.trace = trace;
    return this;
  }
  
  public String toString() {
    return getArtifacts() + ", " + getMetadata();
  }
}
