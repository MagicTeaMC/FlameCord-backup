package com.google.common.graph;

import com.google.common.base.Optional;

@ElementTypesAreNonnullByDefault
abstract class AbstractGraphBuilder<N> {
  final boolean directed;
  
  boolean allowsSelfLoops = false;
  
  ElementOrder<N> nodeOrder = ElementOrder.insertion();
  
  ElementOrder<N> incidentEdgeOrder = ElementOrder.unordered();
  
  Optional<Integer> expectedNodeCount = Optional.absent();
  
  AbstractGraphBuilder(boolean directed) {
    this.directed = directed;
  }
}
