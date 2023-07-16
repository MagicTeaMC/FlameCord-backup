package org.eclipse.aether.resolution;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.aether.graph.DependencyCycle;
import org.eclipse.aether.graph.DependencyNode;

public final class DependencyResult {
  private final DependencyRequest request;
  
  private DependencyNode root;
  
  private List<DependencyCycle> cycles;
  
  private List<Exception> collectExceptions;
  
  private List<ArtifactResult> artifactResults;
  
  public DependencyResult(DependencyRequest request) {
    this.request = Objects.<DependencyRequest>requireNonNull(request, "dependency request cannot be null");
    this.root = request.getRoot();
    this.cycles = Collections.emptyList();
    this.collectExceptions = Collections.emptyList();
    this.artifactResults = Collections.emptyList();
  }
  
  public DependencyRequest getRequest() {
    return this.request;
  }
  
  public DependencyNode getRoot() {
    return this.root;
  }
  
  public DependencyResult setRoot(DependencyNode root) {
    this.root = root;
    return this;
  }
  
  public List<DependencyCycle> getCycles() {
    return this.cycles;
  }
  
  public DependencyResult setCycles(List<DependencyCycle> cycles) {
    if (cycles == null) {
      this.cycles = Collections.emptyList();
    } else {
      this.cycles = cycles;
    } 
    return this;
  }
  
  public List<Exception> getCollectExceptions() {
    return this.collectExceptions;
  }
  
  public DependencyResult setCollectExceptions(List<Exception> exceptions) {
    if (exceptions == null) {
      this.collectExceptions = Collections.emptyList();
    } else {
      this.collectExceptions = exceptions;
    } 
    return this;
  }
  
  public List<ArtifactResult> getArtifactResults() {
    return this.artifactResults;
  }
  
  public DependencyResult setArtifactResults(List<ArtifactResult> results) {
    if (results == null) {
      this.artifactResults = Collections.emptyList();
    } else {
      this.artifactResults = results;
    } 
    return this;
  }
  
  public String toString() {
    return String.valueOf(this.artifactResults);
  }
}
