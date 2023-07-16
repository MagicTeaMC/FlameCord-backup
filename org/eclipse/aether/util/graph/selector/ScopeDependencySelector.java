package org.eclipse.aether.util.graph.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.TreeSet;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;

public final class ScopeDependencySelector implements DependencySelector {
  private final boolean transitive;
  
  private final Collection<String> included;
  
  private final Collection<String> excluded;
  
  public ScopeDependencySelector(Collection<String> included, Collection<String> excluded) {
    this.transitive = false;
    this.included = clone(included);
    this.excluded = clone(excluded);
  }
  
  private static Collection<String> clone(Collection<String> scopes) {
    Collection<String> copy;
    if (scopes == null || scopes.isEmpty()) {
      copy = null;
    } else {
      copy = new HashSet<>(scopes);
      if (copy.size() <= 2)
        copy = new ArrayList<>(new TreeSet<>(copy)); 
    } 
    return copy;
  }
  
  public ScopeDependencySelector(String... excluded) {
    this(null, (excluded != null) ? Arrays.<String>asList(excluded) : null);
  }
  
  private ScopeDependencySelector(boolean transitive, Collection<String> included, Collection<String> excluded) {
    this.transitive = transitive;
    this.included = included;
    this.excluded = excluded;
  }
  
  public boolean selectDependency(Dependency dependency) {
    if (!this.transitive)
      return true; 
    String scope = dependency.getScope();
    return ((this.included == null || this.included.contains(scope)) && (this.excluded == null || 
      !this.excluded.contains(scope)));
  }
  
  public DependencySelector deriveChildSelector(DependencyCollectionContext context) {
    if (this.transitive || context.getDependency() == null)
      return this; 
    return new ScopeDependencySelector(true, this.included, this.excluded);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    ScopeDependencySelector that = (ScopeDependencySelector)obj;
    return (this.transitive == that.transitive && Objects.equals(this.included, that.included) && 
      Objects.equals(this.excluded, that.excluded));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + (this.transitive ? 1 : 0);
    hash = hash * 31 + ((this.included != null) ? this.included.hashCode() : 0);
    hash = hash * 31 + ((this.excluded != null) ? this.excluded.hashCode() : 0);
    return hash;
  }
  
  public String toString() {
    return String.format("%s(included: %s, excluded: %s, transitive: %s)", new Object[] { getClass().getSimpleName(), this.included, this.excluded, Boolean.valueOf(this.transitive) });
  }
}
