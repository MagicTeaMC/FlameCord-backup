package org.eclipse.aether.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.aether.graph.DependencyCycle;
import org.eclipse.aether.graph.DependencyNode;

public final class CollectResult {
  private final CollectRequest request;
  
  private List<Exception> exceptions;
  
  private List<DependencyCycle> cycles;
  
  private DependencyNode root;
  
  public CollectResult(CollectRequest request) {
    this.request = Objects.<CollectRequest>requireNonNull(request, "dependency collection request cannot be null");
    this.exceptions = Collections.emptyList();
    this.cycles = Collections.emptyList();
  }
  
  public CollectRequest getRequest() {
    return this.request;
  }
  
  public List<Exception> getExceptions() {
    return this.exceptions;
  }
  
  public CollectResult addException(Exception exception) {
    if (exception != null) {
      if (this.exceptions.isEmpty())
        this.exceptions = new ArrayList<>(); 
      this.exceptions.add(exception);
    } 
    return this;
  }
  
  public List<DependencyCycle> getCycles() {
    return this.cycles;
  }
  
  public CollectResult addCycle(DependencyCycle cycle) {
    if (cycle != null) {
      if (this.cycles.isEmpty())
        this.cycles = new ArrayList<>(); 
      this.cycles.add(cycle);
    } 
    return this;
  }
  
  public DependencyNode getRoot() {
    return this.root;
  }
  
  public CollectResult setRoot(DependencyNode root) {
    this.root = root;
    return this;
  }
  
  public String toString() {
    return String.valueOf(getRoot());
  }
}
