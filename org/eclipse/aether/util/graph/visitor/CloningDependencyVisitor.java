package org.eclipse.aether.util.graph.visitor;

import java.util.IdentityHashMap;
import java.util.Map;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

public class CloningDependencyVisitor implements DependencyVisitor {
  private DependencyNode root;
  
  private final Stack<DependencyNode> parents = new Stack<>();
  
  private final Map<DependencyNode, DependencyNode> clones = new IdentityHashMap<>(256);
  
  public final DependencyNode getRootNode() {
    return this.root;
  }
  
  protected DependencyNode clone(DependencyNode node) {
    return (DependencyNode)new DefaultDependencyNode(node);
  }
  
  public final boolean visitEnter(DependencyNode node) {
    boolean recurse = true;
    DependencyNode clone = this.clones.get(node);
    if (clone == null) {
      clone = clone(node);
      this.clones.put(node, clone);
    } else {
      recurse = false;
    } 
    DependencyNode parent = this.parents.peek();
    if (parent == null) {
      this.root = clone;
    } else {
      parent.getChildren().add(clone);
    } 
    this.parents.push(clone);
    return recurse;
  }
  
  public final boolean visitLeave(DependencyNode node) {
    this.parents.pop();
    return true;
  }
}
