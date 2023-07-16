package org.eclipse.aether.util.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class AndDependencyFilter implements DependencyFilter {
  private final Set<DependencyFilter> filters = new LinkedHashSet<>();
  
  public AndDependencyFilter(DependencyFilter... filters) {
    if (filters != null)
      Collections.addAll(this.filters, filters); 
  }
  
  public AndDependencyFilter(Collection<DependencyFilter> filters) {
    if (filters != null)
      this.filters.addAll(filters); 
  }
  
  public static DependencyFilter newInstance(DependencyFilter filter1, DependencyFilter filter2) {
    if (filter1 == null)
      return filter2; 
    if (filter2 == null)
      return filter1; 
    return new AndDependencyFilter(new DependencyFilter[] { filter1, filter2 });
  }
  
  public boolean accept(DependencyNode node, List<DependencyNode> parents) {
    for (DependencyFilter filter : this.filters) {
      if (!filter.accept(node, parents))
        return false; 
    } 
    return true;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    AndDependencyFilter that = (AndDependencyFilter)obj;
    return this.filters.equals(that.filters);
  }
  
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + this.filters.hashCode();
    return hash;
  }
}
