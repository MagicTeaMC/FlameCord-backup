package org.eclipse.aether.util.graph.visitor;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

public final class TreeDependencyVisitor implements DependencyVisitor {
  private final Map<DependencyNode, Object> visitedNodes;
  
  private final DependencyVisitor visitor;
  
  private final Stack<Boolean> visits;
  
  public TreeDependencyVisitor(DependencyVisitor visitor) {
    this.visitor = Objects.<DependencyVisitor>requireNonNull(visitor, "dependency visitor delegate cannot be null");
    this.visitedNodes = new IdentityHashMap<>(512);
    this.visits = new Stack<>();
  }
  
  public boolean visitEnter(DependencyNode node) {
    boolean visited = (this.visitedNodes.put(node, Boolean.TRUE) != null);
    this.visits.push(Boolean.valueOf(visited));
    if (visited)
      return false; 
    return this.visitor.visitEnter(node);
  }
  
  public boolean visitLeave(DependencyNode node) {
    Boolean visited = this.visits.pop();
    if (visited.booleanValue())
      return true; 
    return this.visitor.visitLeave(node);
  }
}
