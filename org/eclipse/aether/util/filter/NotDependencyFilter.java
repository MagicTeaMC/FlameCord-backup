package org.eclipse.aether.util.filter;

import java.util.List;
import java.util.Objects;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class NotDependencyFilter implements DependencyFilter {
  private final DependencyFilter filter;
  
  public NotDependencyFilter(DependencyFilter filter) {
    this.filter = Objects.<DependencyFilter>requireNonNull(filter, "dependency filter cannot be null");
  }
  
  public boolean accept(DependencyNode node, List<DependencyNode> parents) {
    return !this.filter.accept(node, parents);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    NotDependencyFilter that = (NotDependencyFilter)obj;
    return this.filter.equals(that.filter);
  }
  
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + this.filter.hashCode();
    return hash;
  }
}
