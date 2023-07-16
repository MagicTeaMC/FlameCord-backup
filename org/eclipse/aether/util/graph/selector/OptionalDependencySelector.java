package org.eclipse.aether.util.graph.selector;

import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;

public final class OptionalDependencySelector implements DependencySelector {
  private final int depth;
  
  public OptionalDependencySelector() {
    this.depth = 0;
  }
  
  private OptionalDependencySelector(int depth) {
    this.depth = depth;
  }
  
  public boolean selectDependency(Dependency dependency) {
    return (this.depth < 2 || !dependency.isOptional());
  }
  
  public DependencySelector deriveChildSelector(DependencyCollectionContext context) {
    if (this.depth >= 2)
      return this; 
    return new OptionalDependencySelector(this.depth + 1);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    OptionalDependencySelector that = (OptionalDependencySelector)obj;
    return (this.depth == that.depth);
  }
  
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + this.depth;
    return hash;
  }
  
  public String toString() {
    return String.format("%s(depth: %d)", new Object[] { getClass().getSimpleName(), Integer.valueOf(this.depth) });
  }
}
