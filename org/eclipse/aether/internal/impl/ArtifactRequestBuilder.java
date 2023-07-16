package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.resolution.ArtifactRequest;

class ArtifactRequestBuilder implements DependencyVisitor {
  private final RequestTrace trace;
  
  private List<ArtifactRequest> requests;
  
  ArtifactRequestBuilder(RequestTrace trace) {
    this.trace = trace;
    this.requests = new ArrayList<>();
  }
  
  public List<ArtifactRequest> getRequests() {
    return this.requests;
  }
  
  public boolean visitEnter(DependencyNode node) {
    if (node.getDependency() != null) {
      ArtifactRequest request = new ArtifactRequest(node);
      request.setTrace(this.trace);
      this.requests.add(request);
    } 
    return true;
  }
  
  public boolean visitLeave(DependencyNode node) {
    return true;
  }
}
