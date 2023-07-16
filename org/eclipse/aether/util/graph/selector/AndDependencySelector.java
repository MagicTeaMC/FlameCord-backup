package org.eclipse.aether.util.graph.selector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;

public final class AndDependencySelector implements DependencySelector {
  private final Set<? extends DependencySelector> selectors;
  
  private int hashCode;
  
  public AndDependencySelector(DependencySelector... selectors) {
    if (selectors != null && selectors.length > 0) {
      this.selectors = new LinkedHashSet<>(Arrays.asList(selectors));
    } else {
      this.selectors = Collections.emptySet();
    } 
  }
  
  public AndDependencySelector(Collection<? extends DependencySelector> selectors) {
    if (selectors != null && !selectors.isEmpty()) {
      this.selectors = new LinkedHashSet<>(selectors);
    } else {
      this.selectors = Collections.emptySet();
    } 
  }
  
  private AndDependencySelector(Set<DependencySelector> selectors) {
    if (selectors != null && !selectors.isEmpty()) {
      this.selectors = selectors;
    } else {
      this.selectors = Collections.emptySet();
    } 
  }
  
  public static DependencySelector newInstance(DependencySelector selector1, DependencySelector selector2) {
    if (selector1 == null)
      return selector2; 
    if (selector2 == null || selector2.equals(selector1))
      return selector1; 
    return new AndDependencySelector(new DependencySelector[] { selector1, selector2 });
  }
  
  public boolean selectDependency(Dependency dependency) {
    for (DependencySelector selector : this.selectors) {
      if (!selector.selectDependency(dependency))
        return false; 
    } 
    return true;
  }
  
  public DependencySelector deriveChildSelector(DependencyCollectionContext context) {
    int seen = 0;
    Set<DependencySelector> childSelectors = null;
    for (DependencySelector selector : this.selectors) {
      DependencySelector childSelector = selector.deriveChildSelector(context);
      if (childSelectors != null) {
        if (childSelector != null)
          childSelectors.add(childSelector); 
        continue;
      } 
      if (selector != childSelector) {
        childSelectors = new LinkedHashSet<>();
        if (seen > 0)
          for (DependencySelector s : this.selectors) {
            if (childSelectors.size() >= seen)
              break; 
            childSelectors.add(s);
          }  
        if (childSelector != null)
          childSelectors.add(childSelector); 
        continue;
      } 
      seen++;
    } 
    if (childSelectors == null)
      return this; 
    if (childSelectors.size() <= 1) {
      if (childSelectors.isEmpty())
        return null; 
      return childSelectors.iterator().next();
    } 
    return new AndDependencySelector(childSelectors);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    AndDependencySelector that = (AndDependencySelector)obj;
    return this.selectors.equals(that.selectors);
  }
  
  public int hashCode() {
    if (this.hashCode == 0) {
      int hash = 17;
      hash = hash * 31 + this.selectors.hashCode();
      this.hashCode = hash;
    } 
    return this.hashCode;
  }
  
  public String toString() {
    StringBuilder builder = (new StringBuilder()).append(getClass().getSimpleName()).append('(');
    Iterator<? extends DependencySelector> iterator = this.selectors.iterator();
    while (iterator.hasNext()) {
      DependencySelector selector = iterator.next();
      builder.append(selector.toString());
      if (iterator.hasNext())
        builder.append(" && "); 
    } 
    return builder.append(')').toString();
  }
}
