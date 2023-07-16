package org.eclipse.aether.util.graph.traverser;

import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.graph.Dependency;

public final class FatArtifactTraverser implements DependencyTraverser {
  public boolean traverseDependency(Dependency dependency) {
    String prop = dependency.getArtifact().getProperty("includesDependencies", "");
    return !Boolean.parseBoolean(prop);
  }
  
  public DependencyTraverser deriveChildTraverser(DependencyCollectionContext context) {
    return this;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    return true;
  }
  
  public int hashCode() {
    return getClass().hashCode();
  }
}
