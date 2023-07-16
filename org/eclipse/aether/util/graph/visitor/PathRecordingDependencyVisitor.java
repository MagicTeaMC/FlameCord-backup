package org.eclipse.aether.util.graph.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

public final class PathRecordingDependencyVisitor implements DependencyVisitor {
  private final DependencyFilter filter;
  
  private final List<List<DependencyNode>> paths;
  
  private final Stack<DependencyNode> parents;
  
  private final boolean excludeChildrenOfMatches;
  
  public PathRecordingDependencyVisitor(DependencyFilter filter) {
    this(filter, true);
  }
  
  public PathRecordingDependencyVisitor(DependencyFilter filter, boolean excludeChildrenOfMatches) {
    this.filter = filter;
    this.excludeChildrenOfMatches = excludeChildrenOfMatches;
    this.paths = new ArrayList<>();
    this.parents = new Stack<>();
  }
  
  public DependencyFilter getFilter() {
    return this.filter;
  }
  
  public List<List<DependencyNode>> getPaths() {
    return this.paths;
  }
  
  public boolean visitEnter(DependencyNode node) {
    boolean accept = (this.filter == null || this.filter.accept(node, this.parents));
    boolean hasDuplicateNodeInParent = this.parents.contains(node);
    this.parents.push(node);
    if (accept) {
      DependencyNode[] path = new DependencyNode[this.parents.size()];
      for (int i = 0, n = this.parents.size(); i < n; i++)
        path[n - i - 1] = this.parents.get(i); 
      this.paths.add(Arrays.asList(path));
      if (this.excludeChildrenOfMatches)
        return false; 
    } 
    return !hasDuplicateNodeInParent;
  }
  
  public boolean visitLeave(DependencyNode node) {
    this.parents.pop();
    return true;
  }
}
