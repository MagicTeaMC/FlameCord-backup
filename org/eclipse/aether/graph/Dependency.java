package org.eclipse.aether.graph;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.artifact.Artifact;

public final class Dependency {
  private final Artifact artifact;
  
  private final String scope;
  
  private final Boolean optional;
  
  private final Set<Exclusion> exclusions;
  
  public Dependency(Artifact artifact, String scope) {
    this(artifact, scope, Boolean.valueOf(false));
  }
  
  public Dependency(Artifact artifact, String scope, Boolean optional) {
    this(artifact, scope, optional, (Collection<Exclusion>)null);
  }
  
  public Dependency(Artifact artifact, String scope, Boolean optional, Collection<Exclusion> exclusions) {
    this(artifact, scope, Exclusions.copy(exclusions), optional);
  }
  
  private Dependency(Artifact artifact, String scope, Set<Exclusion> exclusions, Boolean optional) {
    this.artifact = Objects.<Artifact>requireNonNull(artifact, "artifact cannot be null");
    this.scope = (scope != null) ? scope : "";
    this.optional = optional;
    this.exclusions = exclusions;
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public Dependency setArtifact(Artifact artifact) {
    if (this.artifact.equals(artifact))
      return this; 
    return new Dependency(artifact, this.scope, this.exclusions, this.optional);
  }
  
  public String getScope() {
    return this.scope;
  }
  
  public Dependency setScope(String scope) {
    if (this.scope.equals(scope) || (scope == null && this.scope.length() <= 0))
      return this; 
    return new Dependency(this.artifact, scope, this.exclusions, this.optional);
  }
  
  public boolean isOptional() {
    return Boolean.TRUE.equals(this.optional);
  }
  
  public Boolean getOptional() {
    return this.optional;
  }
  
  public Dependency setOptional(Boolean optional) {
    if (Objects.equals(this.optional, optional))
      return this; 
    return new Dependency(this.artifact, this.scope, this.exclusions, optional);
  }
  
  public Collection<Exclusion> getExclusions() {
    return this.exclusions;
  }
  
  public Dependency setExclusions(Collection<Exclusion> exclusions) {
    if (hasEquivalentExclusions(exclusions))
      return this; 
    return new Dependency(this.artifact, this.scope, this.optional, exclusions);
  }
  
  private boolean hasEquivalentExclusions(Collection<Exclusion> exclusions) {
    if (exclusions == null || exclusions.isEmpty())
      return this.exclusions.isEmpty(); 
    if (exclusions instanceof Set)
      return this.exclusions.equals(exclusions); 
    return (exclusions.size() >= this.exclusions.size() && this.exclusions.containsAll(exclusions) && exclusions
      .containsAll(this.exclusions));
  }
  
  public String toString() {
    return getArtifact() + " (" + getScope() + (isOptional() ? "?" : "") + ")";
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    Dependency that = (Dependency)obj;
    return (Objects.equals(this.artifact, that.artifact) && Objects.equals(this.scope, that.scope) && 
      Objects.equals(this.optional, that.optional) && Objects.equals(this.exclusions, that.exclusions));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + this.artifact.hashCode();
    hash = hash * 31 + this.scope.hashCode();
    hash = hash * 31 + ((this.optional != null) ? this.optional.hashCode() : 0);
    hash = hash * 31 + this.exclusions.size();
    return hash;
  }
  
  private static class Exclusions extends AbstractSet<Exclusion> {
    private final Exclusion[] exclusions;
    
    public static Set<Exclusion> copy(Collection<Exclusion> exclusions) {
      if (exclusions == null || exclusions.isEmpty())
        return Collections.emptySet(); 
      return new Exclusions(exclusions);
    }
    
    private Exclusions(Collection<Exclusion> exclusions) {
      if (exclusions.size() > 1 && !(exclusions instanceof Set))
        exclusions = new LinkedHashSet<>(exclusions); 
      this.exclusions = exclusions.<Exclusion>toArray(new Exclusion[exclusions.size()]);
    }
    
    public Iterator<Exclusion> iterator() {
      return new Iterator<Exclusion>() {
          private int cursor = 0;
          
          public boolean hasNext() {
            return (this.cursor < Dependency.Exclusions.this.exclusions.length);
          }
          
          public Exclusion next() {
            try {
              Exclusion exclusion = Dependency.Exclusions.this.exclusions[this.cursor];
              this.cursor++;
              return exclusion;
            } catch (IndexOutOfBoundsException e) {
              throw new NoSuchElementException();
            } 
          }
          
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
    }
    
    public int size() {
      return this.exclusions.length;
    }
  }
}
