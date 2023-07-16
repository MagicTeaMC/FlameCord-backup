package com.google.common.graph;

import com.google.common.annotations.Beta;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@Beta
public abstract class AbstractGraph<N> extends AbstractBaseGraph<N> implements Graph<N> {
  public final boolean equals(@CheckForNull Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Graph))
      return false; 
    Graph<?> other = (Graph)obj;
    return (isDirected() == other.isDirected() && 
      nodes().equals(other.nodes()) && 
      edges().equals(other.edges()));
  }
  
  public final int hashCode() {
    return edges().hashCode();
  }
  
  public String toString() {
    boolean bool1 = isDirected();
    boolean bool2 = allowsSelfLoops();
    String str1 = String.valueOf(nodes());
    String str2 = String.valueOf(edges());
    return (new StringBuilder(59 + String.valueOf(str1).length() + String.valueOf(str2).length())).append("isDirected: ").append(bool1).append(", allowsSelfLoops: ").append(bool2).append(", nodes: ").append(str1).append(", edges: ").append(str2).toString();
  }
}
