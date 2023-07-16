package org.eclipse.aether.util.graph.visitor;

import java.util.List;
import org.eclipse.aether.graph.DependencyNode;

public final class PreorderNodeListGenerator extends AbstractDepthFirstNodeListGenerator {
  public boolean visitEnter(DependencyNode node) {
    if (!setVisited(node))
      return false; 
    if (node.getDependency() != null)
      this.nodes.add(node); 
    return true;
  }
  
  public boolean visitLeave(DependencyNode node) {
    return true;
  }
}
