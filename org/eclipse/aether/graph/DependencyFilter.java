package org.eclipse.aether.graph;

import java.util.List;

public interface DependencyFilter {
  boolean accept(DependencyNode paramDependencyNode, List<DependencyNode> paramList);
}
