package org.eclipse.aether.util.graph.visitor;

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

abstract class AbstractDepthFirstNodeListGenerator implements DependencyVisitor {
  protected final List<DependencyNode> nodes = new ArrayList<>(128);
  
  private final Map<DependencyNode, Object> visitedNodes = new IdentityHashMap<>(512);
  
  public List<DependencyNode> getNodes() {
    return this.nodes;
  }
  
  public List<Dependency> getDependencies(boolean includeUnresolved) {
    List<Dependency> dependencies = new ArrayList<>(getNodes().size());
    for (DependencyNode node : getNodes()) {
      Dependency dependency = node.getDependency();
      if (dependency != null)
        if (includeUnresolved || dependency.getArtifact().getFile() != null)
          dependencies.add(dependency);  
    } 
    return dependencies;
  }
  
  public List<Artifact> getArtifacts(boolean includeUnresolved) {
    List<Artifact> artifacts = new ArrayList<>(getNodes().size());
    for (DependencyNode node : getNodes()) {
      if (node.getDependency() != null) {
        Artifact artifact = node.getDependency().getArtifact();
        if (includeUnresolved || artifact.getFile() != null)
          artifacts.add(artifact); 
      } 
    } 
    return artifacts;
  }
  
  public List<File> getFiles() {
    List<File> files = new ArrayList<>(getNodes().size());
    for (DependencyNode node : getNodes()) {
      if (node.getDependency() != null) {
        File file = node.getDependency().getArtifact().getFile();
        if (file != null)
          files.add(file); 
      } 
    } 
    return files;
  }
  
  public String getClassPath() {
    StringBuilder buffer = new StringBuilder(1024);
    for (Iterator<DependencyNode> it = getNodes().iterator(); it.hasNext(); ) {
      DependencyNode node = it.next();
      if (node.getDependency() != null) {
        Artifact artifact = node.getDependency().getArtifact();
        if (artifact.getFile() != null) {
          buffer.append(artifact.getFile().getAbsolutePath());
          if (it.hasNext())
            buffer.append(File.pathSeparatorChar); 
        } 
      } 
    } 
    return buffer.toString();
  }
  
  protected boolean setVisited(DependencyNode node) {
    return (this.visitedNodes.put(node, Boolean.TRUE) == null);
  }
  
  public abstract boolean visitEnter(DependencyNode paramDependencyNode);
  
  public abstract boolean visitLeave(DependencyNode paramDependencyNode);
}
