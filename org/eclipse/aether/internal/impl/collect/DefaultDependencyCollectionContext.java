package org.eclipse.aether.internal.impl.collect;

import java.util.List;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.graph.Dependency;

final class DefaultDependencyCollectionContext implements DependencyCollectionContext {
  private final RepositorySystemSession session;
  
  private Artifact artifact;
  
  private Dependency dependency;
  
  private List<Dependency> managedDependencies;
  
  DefaultDependencyCollectionContext(RepositorySystemSession session, Artifact artifact, Dependency dependency, List<Dependency> managedDependencies) {
    this.session = session;
    this.artifact = (dependency != null) ? dependency.getArtifact() : artifact;
    this.dependency = dependency;
    this.managedDependencies = managedDependencies;
  }
  
  public RepositorySystemSession getSession() {
    return this.session;
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public Dependency getDependency() {
    return this.dependency;
  }
  
  public List<Dependency> getManagedDependencies() {
    return this.managedDependencies;
  }
  
  public void set(Dependency dependency, List<Dependency> managedDependencies) {
    this.artifact = dependency.getArtifact();
    this.dependency = dependency;
    this.managedDependencies = managedDependencies;
  }
  
  public String toString() {
    return String.valueOf(getDependency());
  }
}
