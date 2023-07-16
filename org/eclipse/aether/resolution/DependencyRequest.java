package org.eclipse.aether.resolution;

import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class DependencyRequest {
  private DependencyNode root;
  
  private CollectRequest collectRequest;
  
  private DependencyFilter filter;
  
  private RequestTrace trace;
  
  public DependencyRequest() {}
  
  public DependencyRequest(DependencyNode node, DependencyFilter filter) {
    setRoot(node);
    setFilter(filter);
  }
  
  public DependencyRequest(CollectRequest request, DependencyFilter filter) {
    setCollectRequest(request);
    setFilter(filter);
  }
  
  public DependencyNode getRoot() {
    return this.root;
  }
  
  public DependencyRequest setRoot(DependencyNode root) {
    this.root = root;
    return this;
  }
  
  public CollectRequest getCollectRequest() {
    return this.collectRequest;
  }
  
  public DependencyRequest setCollectRequest(CollectRequest collectRequest) {
    this.collectRequest = collectRequest;
    return this;
  }
  
  public DependencyFilter getFilter() {
    return this.filter;
  }
  
  public DependencyRequest setFilter(DependencyFilter filter) {
    this.filter = filter;
    return this;
  }
  
  public RequestTrace getTrace() {
    return this.trace;
  }
  
  public DependencyRequest setTrace(RequestTrace trace) {
    this.trace = trace;
    return this;
  }
  
  public String toString() {
    if (this.root != null)
      return String.valueOf(this.root); 
    return String.valueOf(this.collectRequest);
  }
}
