package org.eclipse.aether.util.graph.selector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;

public final class ExclusionDependencySelector implements DependencySelector {
  private final Exclusion[] exclusions;
  
  private int hashCode;
  
  public ExclusionDependencySelector() {
    this.exclusions = new Exclusion[0];
  }
  
  public ExclusionDependencySelector(Collection<Exclusion> exclusions) {
    if (exclusions != null && !exclusions.isEmpty()) {
      TreeSet<Exclusion> sorted = new TreeSet<>(ExclusionComparator.INSTANCE);
      sorted.addAll(exclusions);
      this.exclusions = (Exclusion[])sorted.toArray((Object[])new Exclusion[sorted.size()]);
    } else {
      this.exclusions = new Exclusion[0];
    } 
  }
  
  private ExclusionDependencySelector(Exclusion[] exclusions) {
    this.exclusions = exclusions;
  }
  
  public boolean selectDependency(Dependency dependency) {
    Artifact artifact = dependency.getArtifact();
    for (Exclusion exclusion : this.exclusions) {
      if (matches(exclusion, artifact))
        return false; 
    } 
    return true;
  }
  
  private boolean matches(Exclusion exclusion, Artifact artifact) {
    if (!matches(exclusion.getArtifactId(), artifact.getArtifactId()))
      return false; 
    if (!matches(exclusion.getGroupId(), artifact.getGroupId()))
      return false; 
    if (!matches(exclusion.getExtension(), artifact.getExtension()))
      return false; 
    if (!matches(exclusion.getClassifier(), artifact.getClassifier()))
      return false; 
    return true;
  }
  
  private boolean matches(String pattern, String value) {
    return ("*".equals(pattern) || pattern.equals(value));
  }
  
  public DependencySelector deriveChildSelector(DependencyCollectionContext context) {
    Dependency dependency = context.getDependency();
    Collection<Exclusion> exclusions = (dependency != null) ? dependency.getExclusions() : null;
    if (exclusions == null || exclusions.isEmpty())
      return this; 
    Exclusion[] merged = this.exclusions;
    int count = merged.length;
    for (Exclusion exclusion : exclusions) {
      int index = Arrays.binarySearch(merged, exclusion, ExclusionComparator.INSTANCE);
      if (index < 0) {
        index = -(index + 1);
        if (count >= merged.length) {
          Exclusion[] tmp = new Exclusion[merged.length + exclusions.size()];
          System.arraycopy(merged, 0, tmp, 0, index);
          tmp[index] = exclusion;
          System.arraycopy(merged, index, tmp, index + 1, count - index);
          merged = tmp;
        } else {
          System.arraycopy(merged, index, merged, index + 1, count - index);
          merged[index] = exclusion;
        } 
        count++;
      } 
    } 
    if (merged == this.exclusions)
      return this; 
    if (merged.length != count) {
      Exclusion[] tmp = new Exclusion[count];
      System.arraycopy(merged, 0, tmp, 0, count);
      merged = tmp;
    } 
    return new ExclusionDependencySelector(merged);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    ExclusionDependencySelector that = (ExclusionDependencySelector)obj;
    return Arrays.equals((Object[])this.exclusions, (Object[])that.exclusions);
  }
  
  public int hashCode() {
    if (this.hashCode == 0) {
      int hash = getClass().hashCode();
      hash = hash * 31 + Arrays.hashCode((Object[])this.exclusions);
      this.hashCode = hash;
    } 
    return this.hashCode;
  }
  
  public String toString() {
    StringBuilder builder = (new StringBuilder()).append(getClass().getSimpleName()).append('(');
    for (int i = 0; i < this.exclusions.length; i++) {
      builder.append(this.exclusions[i]);
      if (i < this.exclusions.length - 1)
        builder.append(", "); 
    } 
    return builder.append(')').toString();
  }
  
  private static class ExclusionComparator implements Comparator<Exclusion> {
    static final ExclusionComparator INSTANCE = new ExclusionComparator();
    
    public int compare(Exclusion e1, Exclusion e2) {
      if (e1 == null)
        return (e2 == null) ? 0 : 1; 
      if (e2 == null)
        return -1; 
      int rel = e1.getArtifactId().compareTo(e2.getArtifactId());
      if (rel == 0) {
        rel = e1.getGroupId().compareTo(e2.getGroupId());
        if (rel == 0) {
          rel = e1.getExtension().compareTo(e2.getExtension());
          if (rel == 0)
            rel = e1.getClassifier().compareTo(e2.getClassifier()); 
        } 
      } 
      return rel;
    }
  }
}
