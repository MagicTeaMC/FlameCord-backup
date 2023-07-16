package org.eclipse.aether.graph;

import java.util.List;

public interface DependencyCycle {
  List<Dependency> getPrecedingDependencies();
  
  List<Dependency> getCyclicDependencies();
}
