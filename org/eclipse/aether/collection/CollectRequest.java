package org.eclipse.aether.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public final class CollectRequest {
  private Artifact rootArtifact;
  
  private Dependency root;
  
  private List<Dependency> dependencies = Collections.emptyList();
  
  private List<Dependency> managedDependencies = Collections.emptyList();
  
  private List<RemoteRepository> repositories = Collections.emptyList();
  
  private String context = "";
  
  private RequestTrace trace;
  
  public CollectRequest() {}
  
  public CollectRequest(Dependency root, List<RemoteRepository> repositories) {
    setRoot(root);
    setRepositories(repositories);
  }
  
  public CollectRequest(Dependency root, List<Dependency> dependencies, List<RemoteRepository> repositories) {
    setRoot(root);
    setDependencies(dependencies);
    setRepositories(repositories);
  }
  
  public CollectRequest(List<Dependency> dependencies, List<Dependency> managedDependencies, List<RemoteRepository> repositories) {
    setDependencies(dependencies);
    setManagedDependencies(managedDependencies);
    setRepositories(repositories);
  }
  
  public Artifact getRootArtifact() {
    return this.rootArtifact;
  }
  
  public CollectRequest setRootArtifact(Artifact rootArtifact) {
    this.rootArtifact = rootArtifact;
    return this;
  }
  
  public Dependency getRoot() {
    return this.root;
  }
  
  public CollectRequest setRoot(Dependency root) {
    this.root = root;
    return this;
  }
  
  public List<Dependency> getDependencies() {
    return this.dependencies;
  }
  
  public CollectRequest setDependencies(List<Dependency> dependencies) {
    if (dependencies == null) {
      this.dependencies = Collections.emptyList();
    } else {
      this.dependencies = dependencies;
    } 
    return this;
  }
  
  public CollectRequest addDependency(Dependency dependency) {
    if (dependency != null) {
      if (this.dependencies.isEmpty())
        this.dependencies = new ArrayList<>(); 
      this.dependencies.add(dependency);
    } 
    return this;
  }
  
  public List<Dependency> getManagedDependencies() {
    return this.managedDependencies;
  }
  
  public CollectRequest setManagedDependencies(List<Dependency> managedDependencies) {
    if (managedDependencies == null) {
      this.managedDependencies = Collections.emptyList();
    } else {
      this.managedDependencies = managedDependencies;
    } 
    return this;
  }
  
  public CollectRequest addManagedDependency(Dependency managedDependency) {
    if (managedDependency != null) {
      if (this.managedDependencies.isEmpty())
        this.managedDependencies = new ArrayList<>(); 
      this.managedDependencies.add(managedDependency);
    } 
    return this;
  }
  
  public List<RemoteRepository> getRepositories() {
    return this.repositories;
  }
  
  public CollectRequest setRepositories(List<RemoteRepository> repositories) {
    if (repositories == null) {
      this.repositories = Collections.emptyList();
    } else {
      this.repositories = repositories;
    } 
    return this;
  }
  
  public CollectRequest addRepository(RemoteRepository repository) {
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
  
  public CollectRequest setRequestContext(String context) {
    this.context = (context != null) ? context : "";
    return this;
  }
  
  public RequestTrace getTrace() {
    return this.trace;
  }
  
  public CollectRequest setTrace(RequestTrace trace) {
    this.trace = trace;
    return this;
  }
  
  public String toString() {
    return getRoot() + " -> " + getDependencies() + " < " + getRepositories();
  }
}
