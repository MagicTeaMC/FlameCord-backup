package org.eclipse.aether.util.graph.version;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.VersionFilter;

public final class ChainedVersionFilter implements VersionFilter {
  private final VersionFilter[] filters;
  
  private int hashCode;
  
  public static VersionFilter newInstance(VersionFilter filter1, VersionFilter filter2) {
    if (filter1 == null)
      return filter2; 
    if (filter2 == null)
      return filter1; 
    return new ChainedVersionFilter(new VersionFilter[] { filter1, filter2 });
  }
  
  public static VersionFilter newInstance(VersionFilter... filters) {
    if (filters.length <= 1) {
      if (filters.length <= 0)
        return null; 
      return filters[0];
    } 
    return new ChainedVersionFilter((VersionFilter[])filters.clone());
  }
  
  public static VersionFilter newInstance(Collection<? extends VersionFilter> filters) {
    if (filters.size() <= 1) {
      if (filters.isEmpty())
        return null; 
      return filters.iterator().next();
    } 
    return new ChainedVersionFilter(filters.<VersionFilter>toArray(new VersionFilter[filters.size()]));
  }
  
  private ChainedVersionFilter(VersionFilter[] filters) {
    this.filters = filters;
  }
  
  public void filterVersions(VersionFilter.VersionFilterContext context) throws RepositoryException {
    for (int i = 0, n = this.filters.length; i < n && context.getCount() > 0; i++)
      this.filters[i].filterVersions(context); 
  }
  
  public VersionFilter deriveChildFilter(DependencyCollectionContext context) {
    VersionFilter[] children = null;
    int removed = 0;
    for (int i = 0, n = this.filters.length; i < n; i++) {
      VersionFilter child = this.filters[i].deriveChildFilter(context);
      if (children != null) {
        children[i - removed] = child;
      } else if (child != this.filters[i]) {
        children = new VersionFilter[this.filters.length];
        System.arraycopy(this.filters, 0, children, 0, i);
        children[i - removed] = child;
      } 
      if (child == null)
        removed++; 
    } 
    if (children == null)
      return this; 
    if (removed > 0) {
      int count = this.filters.length - removed;
      if (count <= 0)
        return null; 
      if (count == 1)
        return children[0]; 
      VersionFilter[] tmp = new VersionFilter[count];
      System.arraycopy(children, 0, tmp, 0, count);
      children = tmp;
    } 
    return new ChainedVersionFilter(children);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    ChainedVersionFilter that = (ChainedVersionFilter)obj;
    return Arrays.equals((Object[])this.filters, (Object[])that.filters);
  }
  
  public int hashCode() {
    if (this.hashCode == 0) {
      int hash = getClass().hashCode();
      hash = hash * 31 + Arrays.hashCode((Object[])this.filters);
      this.hashCode = hash;
    } 
    return this.hashCode;
  }
}
