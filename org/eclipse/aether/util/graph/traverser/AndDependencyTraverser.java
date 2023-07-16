package org.eclipse.aether.util.graph.traverser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.graph.Dependency;

public final class AndDependencyTraverser implements DependencyTraverser {
  private final Set<? extends DependencyTraverser> traversers;
  
  private int hashCode;
  
  public AndDependencyTraverser(DependencyTraverser... traversers) {
    if (traversers != null && traversers.length > 0) {
      this.traversers = new LinkedHashSet<>(Arrays.asList(traversers));
    } else {
      this.traversers = Collections.emptySet();
    } 
  }
  
  public AndDependencyTraverser(Collection<? extends DependencyTraverser> traversers) {
    if (traversers != null && !traversers.isEmpty()) {
      this.traversers = new LinkedHashSet<>(traversers);
    } else {
      this.traversers = Collections.emptySet();
    } 
  }
  
  private AndDependencyTraverser(Set<DependencyTraverser> traversers) {
    if (traversers != null && !traversers.isEmpty()) {
      this.traversers = traversers;
    } else {
      this.traversers = Collections.emptySet();
    } 
  }
  
  public static DependencyTraverser newInstance(DependencyTraverser traverser1, DependencyTraverser traverser2) {
    if (traverser1 == null)
      return traverser2; 
    if (traverser2 == null || traverser2.equals(traverser1))
      return traverser1; 
    return new AndDependencyTraverser(new DependencyTraverser[] { traverser1, traverser2 });
  }
  
  public boolean traverseDependency(Dependency dependency) {
    for (DependencyTraverser traverser : this.traversers) {
      if (!traverser.traverseDependency(dependency))
        return false; 
    } 
    return true;
  }
  
  public DependencyTraverser deriveChildTraverser(DependencyCollectionContext context) {
    int seen = 0;
    Set<DependencyTraverser> childTraversers = null;
    for (DependencyTraverser traverser : this.traversers) {
      DependencyTraverser childTraverser = traverser.deriveChildTraverser(context);
      if (childTraversers != null) {
        if (childTraverser != null)
          childTraversers.add(childTraverser); 
        continue;
      } 
      if (traverser != childTraverser) {
        childTraversers = new LinkedHashSet<>();
        if (seen > 0)
          for (DependencyTraverser s : this.traversers) {
            if (childTraversers.size() >= seen)
              break; 
            childTraversers.add(s);
          }  
        if (childTraverser != null)
          childTraversers.add(childTraverser); 
        continue;
      } 
      seen++;
    } 
    if (childTraversers == null)
      return this; 
    if (childTraversers.size() <= 1) {
      if (childTraversers.isEmpty())
        return null; 
      return childTraversers.iterator().next();
    } 
    return new AndDependencyTraverser(childTraversers);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    AndDependencyTraverser that = (AndDependencyTraverser)obj;
    return this.traversers.equals(that.traversers);
  }
  
  public int hashCode() {
    if (this.hashCode == 0) {
      int hash = 17;
      hash = hash * 31 + this.traversers.hashCode();
      this.hashCode = hash;
    } 
    return this.hashCode;
  }
}
