package org.eclipse.aether.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

public final class VersionRangeRequest {
  private Artifact artifact;
  
  private List<RemoteRepository> repositories = Collections.emptyList();
  
  private String context = "";
  
  private RequestTrace trace;
  
  public VersionRangeRequest() {}
  
  public VersionRangeRequest(Artifact artifact, List<RemoteRepository> repositories, String context) {
    setArtifact(artifact);
    setRepositories(repositories);
    setRequestContext(context);
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public VersionRangeRequest setArtifact(Artifact artifact) {
    this.artifact = artifact;
    return this;
  }
  
  public List<RemoteRepository> getRepositories() {
    return this.repositories;
  }
  
  public VersionRangeRequest setRepositories(List<RemoteRepository> repositories) {
    if (repositories == null) {
      this.repositories = Collections.emptyList();
    } else {
      this.repositories = repositories;
    } 
    return this;
  }
  
  public VersionRangeRequest addRepository(RemoteRepository repository) {
    if (repository != null) {
      if (this.repositories.isEmpty())
        this.repositories = new ArrayList<>(); 
      this.repositories.add(repository);
    } 
    return this;
  }
  
  public String getRequestContext() {
    return this.context;
  }
  
  public VersionRangeRequest setRequestContext(String context) {
    this.context = (context != null) ? context : "";
    return this;
  }
  
  public RequestTrace getTrace() {
    return this.trace;
  }
  
  public VersionRangeRequest setTrace(RequestTrace trace) {
    this.trace = trace;
    return this;
  }
  
  public String toString() {
    return getArtifact() + " < " + getRepositories();
  }
}
