package org.eclipse.aether.collection;

import org.eclipse.aether.graph.Dependency;

public interface DependencyManager {
  DependencyManagement manageDependency(Dependency paramDependency);
  
  DependencyManager deriveChildManager(DependencyCollectionContext paramDependencyCollectionContext);
}
