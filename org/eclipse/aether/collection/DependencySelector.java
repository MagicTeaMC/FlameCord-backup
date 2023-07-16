package org.eclipse.aether.collection;

import org.eclipse.aether.graph.Dependency;

public interface DependencySelector {
  boolean selectDependency(Dependency paramDependency);
  
  DependencySelector deriveChildSelector(DependencyCollectionContext paramDependencyCollectionContext);
}
