package org.eclipse.aether.internal.impl.collect;

import java.util.Arrays;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;

final class NodeStack {
  private DependencyNode[] nodes = new DependencyNode[96];
  
  private int size;
  
  public DependencyNode top() {
    if (this.size <= 0)
      throw new IllegalStateException("stack empty"); 
    return this.nodes[this.size - 1];
  }
  
  public void push(DependencyNode node) {
    if (this.size >= this.nodes.length) {
      DependencyNode[] tmp = new DependencyNode[this.size + 64];
      System.arraycopy(this.nodes, 0, tmp, 0, this.nodes.length);
      this.nodes = tmp;
    } 
    this.nodes[this.size++] = node;
  }
  
  public void pop() {
    if (this.size <= 0)
      throw new IllegalStateException("stack empty"); 
    this.size--;
  }
  
  public int find(Artifact artifact) {
    for (int i = this.size - 1; i >= 0; i--) {
      DependencyNode node = this.nodes[i];
      Artifact a = node.getArtifact();
      if (a == null)
        break; 
      if (a.getArtifactId().equals(artifact.getArtifactId()))
        if (a.getGroupId().equals(artifact.getGroupId()))
          if (a.getExtension().equals(artifact.getExtension()))
            if (a.getClassifier().equals(artifact.getClassifier()))
              return i;    
    } 
    return -1;
  }
  
  public int size() {
    return this.size;
  }
  
  public DependencyNode get(int index) {
    return this.nodes[index];
  }
  
  public String toString() {
    return Arrays.toString((Object[])this.nodes);
  }
}
