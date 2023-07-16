package org.eclipse.aether.graph;

public interface DependencyVisitor {
  boolean visitEnter(DependencyNode paramDependencyNode);
  
  boolean visitLeave(DependencyNode paramDependencyNode);
}
