package org.eclipse.aether.util.graph.transformer;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;

public final class JavaDependencyContextRefiner implements DependencyGraphTransformer {
  public DependencyNode transformGraph(DependencyNode node, DependencyGraphTransformationContext context) throws RepositoryException {
    String ctx = node.getRequestContext();
    if ("project".equals(ctx)) {
      String scope = getClasspathScope(node);
      if (scope != null) {
        ctx = ctx + '/' + scope;
        node.setRequestContext(ctx);
      } 
    } 
    for (DependencyNode child : node.getChildren())
      transformGraph(child, context); 
    return node;
  }
  
  private String getClasspathScope(DependencyNode node) {
    Dependency dependency = node.getDependency();
    if (dependency == null)
      return null; 
    String scope = dependency.getScope();
    if ("compile".equals(scope) || "system".equals(scope) || "provided"
      .equals(scope))
      return "compile"; 
    if ("runtime".equals(scope))
      return "runtime"; 
    if ("test".equals(scope))
      return "test"; 
    return null;
  }
}
