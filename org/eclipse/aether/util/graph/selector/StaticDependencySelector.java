package org.eclipse.aether.util.graph.selector;

import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;

public final class StaticDependencySelector implements DependencySelector {
  private final boolean select;
  
  public StaticDependencySelector(boolean select) {
    this.select = select;
  }
  
  public boolean selectDependency(Dependency dependency) {
    return this.select;
  }
  
  public DependencySelector deriveChildSelector(DependencyCollectionContext context) {
    return this;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    StaticDependencySelector that = (StaticDependencySelector)obj;
    return (this.select == that.select);
  }
  
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + (this.select ? 1 : 0);
    return hash;
  }
  
  public String toString() {
    return String.format("%s(%s)", new Object[] { getClass().getSimpleName(), this.select ? "Select all" : "Exclude all" });
  }
}
