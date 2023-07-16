package org.eclipse.aether.util.graph.manager;

import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyManagement;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.graph.Dependency;

public final class NoopDependencyManager implements DependencyManager {
  public static final DependencyManager INSTANCE = new NoopDependencyManager();
  
  public DependencyManager deriveChildManager(DependencyCollectionContext context) {
    return this;
  }
  
  public DependencyManagement manageDependency(Dependency dependency) {
    return null;
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
