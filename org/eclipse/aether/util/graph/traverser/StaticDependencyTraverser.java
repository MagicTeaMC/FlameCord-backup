package org.eclipse.aether.util.graph.traverser;

import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.graph.Dependency;

public final class StaticDependencyTraverser implements DependencyTraverser {
  private final boolean traverse;
  
  public StaticDependencyTraverser(boolean traverse) {
    this.traverse = traverse;
  }
  
  public boolean traverseDependency(Dependency dependency) {
    return this.traverse;
  }
  
  public DependencyTraverser deriveChildTraverser(DependencyCollectionContext context) {
    return this;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    StaticDependencyTraverser that = (StaticDependencyTraverser)obj;
    return (this.traverse == that.traverse);
  }
  
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + (this.traverse ? 1 : 0);
    return hash;
  }
}
