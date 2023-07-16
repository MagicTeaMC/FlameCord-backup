package org.eclipse.aether.internal.impl.collect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyCycle;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;

final class DefaultDependencyCycle implements DependencyCycle {
  private final List<Dependency> dependencies;
  
  private final int cycleEntry;
  
  DefaultDependencyCycle(NodeStack nodes, int cycleEntry, Dependency dependency) {
    int offset = (cycleEntry > 0 && nodes.get(0).getDependency() == null) ? 1 : 0;
    Dependency[] dependencies = new Dependency[nodes.size() - offset + 1];
    for (int i = 0, n = dependencies.length - 1; i < n; i++) {
      DependencyNode node = nodes.get(i + offset);
      dependencies[i] = node.getDependency();
      if (dependencies[i] == null)
        dependencies[i] = new Dependency(node.getArtifact(), null); 
    } 
    dependencies[dependencies.length - 1] = dependency;
    this.dependencies = Collections.unmodifiableList(Arrays.asList(dependencies));
    this.cycleEntry = cycleEntry;
  }
  
  public List<Dependency> getPrecedingDependencies() {
    return this.dependencies.subList(0, this.cycleEntry);
  }
  
  public List<Dependency> getCyclicDependencies() {
    return this.dependencies.subList(this.cycleEntry, this.dependencies.size());
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder(256);
    for (int i = 0, n = this.dependencies.size(); i < n; i++) {
      if (i > 0)
        buffer.append(" -> "); 
      buffer.append(ArtifactIdUtils.toVersionlessId(((Dependency)this.dependencies.get(i)).getArtifact()));
    } 
    return buffer.toString();
  }
}
