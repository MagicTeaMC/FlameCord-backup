package org.eclipse.aether.util.graph.transformer;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.DependencyNode;

public final class ChainedDependencyGraphTransformer implements DependencyGraphTransformer {
  private final DependencyGraphTransformer[] transformers;
  
  public ChainedDependencyGraphTransformer(DependencyGraphTransformer... transformers) {
    if (transformers == null) {
      this.transformers = new DependencyGraphTransformer[0];
    } else {
      this.transformers = transformers;
    } 
  }
  
  public static DependencyGraphTransformer newInstance(DependencyGraphTransformer transformer1, DependencyGraphTransformer transformer2) {
    if (transformer1 == null)
      return transformer2; 
    if (transformer2 == null)
      return transformer1; 
    return new ChainedDependencyGraphTransformer(new DependencyGraphTransformer[] { transformer1, transformer2 });
  }
  
  public DependencyNode transformGraph(DependencyNode node, DependencyGraphTransformationContext context) throws RepositoryException {
    for (DependencyGraphTransformer transformer : this.transformers)
      node = transformer.transformGraph(node, context); 
    return node;
  }
}
