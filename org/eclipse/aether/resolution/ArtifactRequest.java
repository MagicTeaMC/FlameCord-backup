package org.eclipse.aether.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;

public final class ArtifactRequest {
  private Artifact artifact;
  
  private DependencyNode node;
  
  private List<RemoteRepository> repositories = Collections.emptyList();
  
  private String context = "";
  
  private RequestTrace trace;
  
  public ArtifactRequest() {}
  
  public ArtifactRequest(Artifact artifact, List<RemoteRepository> repositories, String context) {
    setArtifact(artifact);
    setRepositories(repositories);
    setRequestContext(context);
  }
  
  public ArtifactRequest(DependencyNode node) {
    setDependencyNode(node);
    setRepositories(node.getRepositories());
    setRequestContext(node.getRequestContext());
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public ArtifactRequest setArtifact(Artifact artifact) {
    this.artifact = artifact;
    return this;
  }
  
  public DependencyNode getDependencyNode() {
    return this.node;
  }
  
  public ArtifactRequest setDependencyNode(DependencyNode node) {
    this.node = node;
    if (node != null)
      setArtifact(node.getDependency().getArtifact()); 
    return this;
  }
  
  public List<RemoteRepository> getRepositories() {
    return this.repositories;
  }
  
  public ArtifactRequest setRepositories(List<RemoteRepository> repositories) {
    if (repositories == null) {
      this.repositories = Collections.emptyList();
    } else {
      this.repositories = repositories;
    } 
    return this;
  }
  
  public ArtifactRequest addRepository(RemoteRepository repository) {
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
  
  public ArtifactRequest setRequestContext(String context) {
    this.context = (context != null) ? context : "";
    return this;
  }
  
  public RequestTrace getTrace() {
    return this.trace;
  }
  
  public ArtifactRequest setTrace(RequestTrace trace) {
    this.trace = trace;
    return this;
  }
  
  public String toString() {
    return getArtifact() + " < " + getRepositories();
  }
}
