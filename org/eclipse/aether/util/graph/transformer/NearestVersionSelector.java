package org.eclipse.aether.util.graph.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.collection.UnsolvableVersionConflictException;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.util.graph.visitor.PathRecordingDependencyVisitor;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

public final class NearestVersionSelector extends ConflictResolver.VersionSelector {
  public void selectVersion(ConflictResolver.ConflictContext context) throws RepositoryException {
    ConflictGroup group = new ConflictGroup();
    for (ConflictResolver.ConflictItem item : context.getItems()) {
      DependencyNode node = item.getNode();
      VersionConstraint constraint = node.getVersionConstraint();
      boolean backtrack = false;
      boolean hardConstraint = (constraint.getRange() != null);
      if (hardConstraint)
        if (group.constraints.add(constraint))
          if (group.winner != null && !constraint.containsVersion(group.winner.getNode().getVersion()))
            backtrack = true;   
      if (isAcceptable(group, node.getVersion())) {
        group.candidates.add(item);
        if (backtrack) {
          backtrack(group, context);
          continue;
        } 
        if (group.winner == null || isNearer(item, group.winner))
          group.winner = item; 
        continue;
      } 
      if (backtrack)
        backtrack(group, context); 
    } 
    context.setWinner(group.winner);
  }
  
  private void backtrack(ConflictGroup group, ConflictResolver.ConflictContext context) throws UnsolvableVersionConflictException {
    group.winner = null;
    for (Iterator<ConflictResolver.ConflictItem> it = group.candidates.iterator(); it.hasNext(); ) {
      ConflictResolver.ConflictItem candidate = it.next();
      if (!isAcceptable(group, candidate.getNode().getVersion())) {
        it.remove();
        continue;
      } 
      if (group.winner == null || isNearer(candidate, group.winner))
        group.winner = candidate; 
    } 
    if (group.winner == null)
      throw newFailure(context); 
  }
  
  private boolean isAcceptable(ConflictGroup group, Version version) {
    for (VersionConstraint constraint : group.constraints) {
      if (!constraint.containsVersion(version))
        return false; 
    } 
    return true;
  }
  
  private boolean isNearer(ConflictResolver.ConflictItem item1, ConflictResolver.ConflictItem item2) {
    if (item1.isSibling(item2))
      return (item1.getNode().getVersion().compareTo(item2.getNode().getVersion()) > 0); 
    return (item1.getDepth() < item2.getDepth());
  }
  
  private UnsolvableVersionConflictException newFailure(final ConflictResolver.ConflictContext context) {
    DependencyFilter filter = new DependencyFilter() {
        public boolean accept(DependencyNode node, List<DependencyNode> parents) {
          return context.isIncluded(node);
        }
      };
    PathRecordingDependencyVisitor visitor = new PathRecordingDependencyVisitor(filter);
    context.getRoot().accept((DependencyVisitor)visitor);
    return new UnsolvableVersionConflictException(visitor.getPaths());
  }
  
  static final class ConflictGroup {
    final Collection<VersionConstraint> constraints = new HashSet<>();
    
    final Collection<ConflictResolver.ConflictItem> candidates = new ArrayList<>(64);
    
    ConflictResolver.ConflictItem winner;
    
    public String toString() {
      return String.valueOf(this.winner);
    }
  }
}
