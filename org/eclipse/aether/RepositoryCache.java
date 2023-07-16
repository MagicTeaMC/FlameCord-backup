package org.eclipse.aether;

public interface RepositoryCache {
  void put(RepositorySystemSession paramRepositorySystemSession, Object paramObject1, Object paramObject2);
  
  Object get(RepositorySystemSession paramRepositorySystemSession, Object paramObject);
}
