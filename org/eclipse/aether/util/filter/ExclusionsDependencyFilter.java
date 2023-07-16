package org.eclipse.aether.util.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class ExclusionsDependencyFilter implements DependencyFilter {
  private final Set<String> excludes = new HashSet<>();
  
  public ExclusionsDependencyFilter(Collection<String> excludes) {
    if (excludes != null)
      this.excludes.addAll(excludes); 
  }
  
  public boolean accept(DependencyNode node, List<DependencyNode> parents) {
    Dependency dependency = node.getDependency();
    if (dependency == null)
      return true; 
    String id = dependency.getArtifact().getArtifactId();
    if (this.excludes.contains(id))
      return false; 
    id = dependency.getArtifact().getGroupId() + ':' + id;
    if (this.excludes.contains(id))
      return false; 
    return true;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    ExclusionsDependencyFilter that = (ExclusionsDependencyFilter)obj;
    return this.excludes.equals(that.excludes);
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + this.excludes.hashCode();
    return hash;
  }
}
