package org.eclipse.aether.collection;

import org.eclipse.aether.graph.Dependency;

public interface DependencyTraverser {
  boolean traverseDependency(Dependency paramDependency);
  
  DependencyTraverser deriveChildTraverser(DependencyCollectionContext paramDependencyCollectionContext);
}
