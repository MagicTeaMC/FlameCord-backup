package org.eclipse.aether.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;

public final class ArtifactDescriptorResult {
  private final ArtifactDescriptorRequest request;
  
  private List<Exception> exceptions;
  
  private List<Artifact> relocations;
  
  private Collection<Artifact> aliases;
  
  private Artifact artifact;
  
  private ArtifactRepository repository;
  
  private List<Dependency> dependencies;
  
  private List<Dependency> managedDependencies;
  
  private List<RemoteRepository> repositories;
  
  private Map<String, Object> properties;
  
  public ArtifactDescriptorResult(ArtifactDescriptorRequest request) {
    this.request = Objects.<ArtifactDescriptorRequest>requireNonNull(request, "artifact descriptor request cannot be null");
    this.artifact = request.getArtifact();
    this.exceptions = Collections.emptyList();
    this.relocations = Collections.emptyList();
    this.aliases = Collections.emptyList();
    this.dependencies = Collections.emptyList();
    this.managedDependencies = Collections.emptyList();
    this.repositories = Collections.emptyList();
    this.properties = Collections.emptyMap();
  }
  
  public ArtifactDescriptorRequest getRequest() {
    return this.request;
  }
  
  public List<Exception> getExceptions() {
    return this.exceptions;
  }
  
  public ArtifactDescriptorResult setExceptions(List<Exception> exceptions) {
    if (exceptions == null) {
      this.exceptions = Collections.emptyList();
    } else {
      this.exceptions = exceptions;
    } 
    return this;
  }
  
  public ArtifactDescriptorResult addException(Exception exception) {
    if (exception != null) {
      if (this.exceptions.isEmpty())
        this.exceptions = new ArrayList<>(); 
      this.exceptions.add(exception);
    } 
    return this;
  }
  
  public List<Artifact> getRelocations() {
    return this.relocations;
  }
  
  public ArtifactDescriptorResult setRelocations(List<Artifact> relocations) {
    if (relocations == null) {
      this.relocations = Collections.emptyList();
    } else {
      this.relocations = relocations;
    } 
    return this;
  }
  
  public ArtifactDescriptorResult addRelocation(Artifact artifact) {
    if (artifact != null) {
      if (this.relocations.isEmpty())
        this.relocations = new ArrayList<>(); 
      this.relocations.add(artifact);
    } 
    return this;
  }
  
  public Collection<Artifact> getAliases() {
    return this.aliases;
  }
  
  public ArtifactDescriptorResult setAliases(Collection<Artifact> aliases) {
    if (aliases == null) {
      this.aliases = Collections.emptyList();
    } else {
      this.aliases = aliases;
    } 
    return this;
  }
  
  public ArtifactDescriptorResult addAlias(Artifact alias) {
    if (alias != null) {
      if (this.aliases.isEmpty())
        this.aliases = new ArrayList<>(); 
      this.aliases.add(alias);
    } 
    return this;
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public ArtifactDescriptorResult setArtifact(Artifact artifact) {
    this.artifact = artifact;
    return this;
  }
  
  public ArtifactRepository getRepository() {
    return this.repository;
  }
  
  public ArtifactDescriptorResult setRepository(ArtifactRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public List<Dependency> getDependencies() {
    return this.dependencies;
  }
  
  public ArtifactDescriptorResult setDependencies(List<Dependency> dependencies) {
    if (dependencies == null) {
      this.dependencies = Collections.emptyList();
    } else {
      this.dependencies = dependencies;
    } 
    return this;
  }
  
  public ArtifactDescriptorResult addDependency(Dependency dependency) {
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
  
  public ArtifactDescriptorResult setManagedDependencies(List<Dependency> dependencies) {
    if (dependencies == null) {
      this.managedDependencies = Collections.emptyList();
    } else {
      this.managedDependencies = dependencies;
    } 
    return this;
  }
  
  public ArtifactDescriptorResult addManagedDependency(Dependency dependency) {
    if (dependency != null) {
      if (this.managedDependencies.isEmpty())
        this.managedDependencies = new ArrayList<>(); 
      this.managedDependencies.add(dependency);
    } 
    return this;
  }
  
  public List<RemoteRepository> getRepositories() {
    return this.repositories;
  }
  
  public ArtifactDescriptorResult setRepositories(List<RemoteRepository> repositories) {
    if (repositories == null) {
      this.repositories = Collections.emptyList();
    } else {
      this.repositories = repositories;
    } 
    return this;
  }
  
  public ArtifactDescriptorResult addRepository(RemoteRepository repository) {
    if (repository != null) {
      if (this.repositories.isEmpty())
        this.repositories = new ArrayList<>(); 
      this.repositories.add(repository);
    } 
    return this;
  }
  
  public Map<String, Object> getProperties() {
    return this.properties;
  }
  
  public ArtifactDescriptorResult setProperties(Map<String, Object> properties) {
    if (properties == null) {
      this.properties = Collections.emptyMap();
    } else {
      this.properties = properties;
    } 
    return this;
  }
  
  public String toString() {
    return getArtifact() + " -> " + getDependencies();
  }
}
