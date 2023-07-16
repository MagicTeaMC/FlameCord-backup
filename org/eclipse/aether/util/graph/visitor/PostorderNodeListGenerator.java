package org.eclipse.aether.util.graph.visitor;

import java.util.List;
import org.eclipse.aether.graph.DependencyNode;

public final class PostorderNodeListGenerator extends AbstractDepthFirstNodeListGenerator {
  private final Stack<Boolean> visits = new Stack<>();
  
  public boolean visitEnter(DependencyNode node) {
    boolean visited = !setVisited(node);
    this.visits.push(Boolean.valueOf(visited));
    if (visited)
      return false; 
    return true;
  }
  
  public boolean visitLeave(DependencyNode node) {
    Boolean visited = this.visits.pop();
    if (visited.booleanValue())
      return true; 
    if (node.getDependency() != null)
      this.nodes.add(node); 
    return true;
  }
}
