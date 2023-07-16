package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
final class UndirectedGraphConnections<N, V> implements GraphConnections<N, V> {
  private final Map<N, V> adjacentNodeValues;
  
  private UndirectedGraphConnections(Map<N, V> adjacentNodeValues) {
    this.adjacentNodeValues = (Map<N, V>)Preconditions.checkNotNull(adjacentNodeValues);
  }
  
  static <N, V> UndirectedGraphConnections<N, V> of(ElementOrder<N> incidentEdgeOrder) {
    switch (incidentEdgeOrder.type()) {
      case UNORDERED:
        return new UndirectedGraphConnections<>(new HashMap<>(2, 1.0F));
      case STABLE:
        return new UndirectedGraphConnections<>(new LinkedHashMap<>(2, 1.0F));
    } 
    throw new AssertionError(incidentEdgeOrder.type());
  }
  
  static <N, V> UndirectedGraphConnections<N, V> ofImmutable(Map<N, V> adjacentNodeValues) {
    return new UndirectedGraphConnections<>((Map<N, V>)ImmutableMap.copyOf(adjacentNodeValues));
  }
  
  public Set<N> adjacentNodes() {
    return Collections.unmodifiableSet(this.adjacentNodeValues.keySet());
  }
  
  public Set<N> predecessors() {
    return adjacentNodes();
  }
  
  public Set<N> successors() {
    return adjacentNodes();
  }
  
  public Iterator<EndpointPair<N>> incidentEdgeIterator(N thisNode) {
    return Iterators.transform(this.adjacentNodeValues
        .keySet().iterator(), incidentNode -> EndpointPair.unordered(thisNode, incidentNode));
  }
  
  @CheckForNull
  public V value(N node) {
    return this.adjacentNodeValues.get(node);
  }
  
  public void removePredecessor(N node) {
    V unused = removeSuccessor(node);
  }
  
  @CheckForNull
  public V removeSuccessor(N node) {
    return this.adjacentNodeValues.remove(node);
  }
  
  public void addPredecessor(N node, V value) {
    V unused = addSuccessor(node, value);
  }
  
  @CheckForNull
  public V addSuccessor(N node, V value) {
    return this.adjacentNodeValues.put(node, value);
  }
}
