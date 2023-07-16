package org.eclipse.aether.collection;

import org.eclipse.aether.RepositorySystemSession;

public interface DependencyGraphTransformationContext {
  RepositorySystemSession getSession();
  
  Object get(Object paramObject);
  
  Object put(Object paramObject1, Object paramObject2);
}
