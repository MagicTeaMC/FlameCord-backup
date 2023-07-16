package org.eclipse.aether.util.graph.version;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.util.ConfigUtils;

public final class ContextualSnapshotVersionFilter implements VersionFilter {
  public static final String CONFIG_PROP_ENABLE = "aether.snapshotFilter";
  
  private final SnapshotVersionFilter filter = new SnapshotVersionFilter();
  
  private boolean isEnabled(RepositorySystemSession session) {
    return ConfigUtils.getBoolean(session, false, new String[] { "aether.snapshotFilter" });
  }
  
  public void filterVersions(VersionFilter.VersionFilterContext context) {
    if (isEnabled(context.getSession()))
      this.filter.filterVersions(context); 
  }
  
  public VersionFilter deriveChildFilter(DependencyCollectionContext context) {
    if (!isEnabled(context.getSession())) {
      Artifact artifact = context.getArtifact();
      if (artifact == null)
        return this; 
      if (artifact.isSnapshot())
        return null; 
    } 
    return this.filter;
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
