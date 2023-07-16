package org.eclipse.aether.util.graph.version;

import java.util.Iterator;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.version.Version;

public final class SnapshotVersionFilter implements VersionFilter {
  public void filterVersions(VersionFilter.VersionFilterContext context) {
    for (Iterator<Version> it = context.iterator(); it.hasNext(); ) {
      String version = ((Version)it.next()).toString();
      if (version.endsWith("SNAPSHOT"))
        it.remove(); 
    } 
  }
  
  public VersionFilter deriveChildFilter(DependencyCollectionContext context) {
    return this;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    return true;
  }
  
  public int hashCode() {
    return getClass().hashCode();
  }
}
