package org.eclipse.aether.util.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class ScopeDependencyFilter implements DependencyFilter {
  private final Set<String> included = new HashSet<>();
  
  private final Set<String> excluded = new HashSet<>();
  
  public ScopeDependencyFilter(Collection<String> included, Collection<String> excluded) {
    if (included != null)
      this.included.addAll(included); 
    if (excluded != null)
      this.excluded.addAll(excluded); 
  }
  
  public ScopeDependencyFilter(String... excluded) {
    if (excluded != null)
      this.excluded.addAll(Arrays.asList(excluded)); 
  }
  
  public boolean accept(DependencyNode node, List<DependencyNode> parents) {
    Dependency dependency = node.getDependency();
    if (dependency == null)
      return true; 
    String scope = node.getDependency().getScope();
    return ((this.included.isEmpty() || this.included.contains(scope)) && (this.excluded
      .isEmpty() || !this.excluded.contains(scope)));
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    ScopeDependencyFilter that = (ScopeDependencyFilter)obj;
    return (this.included.equals(that.included) && this.excluded.equals(that.excluded));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + this.included.hashCode();
    hash = hash * 31 + this.excluded.hashCode();
    return hash;
  }
}
