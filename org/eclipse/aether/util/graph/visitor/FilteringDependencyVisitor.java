package org.eclipse.aether.util.graph.visitor;

import java.util.Objects;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

public final class FilteringDependencyVisitor implements DependencyVisitor {
  private final DependencyFilter filter;
  
  private final DependencyVisitor visitor;
  
  private final Stack<Boolean> accepts;
  
  private final Stack<DependencyNode> parents;
  
  public FilteringDependencyVisitor(DependencyVisitor visitor, DependencyFilter filter) {
    this.visitor = Objects.<DependencyVisitor>requireNonNull(visitor, "dependency visitor delegate cannot be null");
    this.filter = filter;
    this.accepts = new Stack<>();
    this.parents = new Stack<>();
  }
  
  public DependencyVisitor getVisitor() {
    return this.visitor;
  }
  
  public DependencyFilter getFilter() {
    return this.filter;
  }
  
  public boolean visitEnter(DependencyNode node) {
    boolean accept = (this.filter == null || this.filter.accept(node, this.parents));
    this.accepts.push(Boolean.valueOf(accept));
    this.parents.push(node);
    if (accept)
      return this.visitor.visitEnter(node); 
    return true;
  }
  
  public boolean visitLeave(DependencyNode node) {
    this.parents.pop();
    Boolean accept = this.accepts.pop();
    if (accept.booleanValue())
      return this.visitor.visitLeave(node); 
    return true;
  }
}
