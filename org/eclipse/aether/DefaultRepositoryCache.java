package org.eclipse.aether;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultRepositoryCache implements RepositoryCache {
  private final Map<Object, Object> cache = new ConcurrentHashMap<>(256);
  
  public Object get(RepositorySystemSession session, Object key) {
    return this.cache.get(key);
  }
  
  public void put(RepositorySystemSession session, Object key, Object data) {
    if (data != null) {
      this.cache.put(key, data);
    } else {
      this.cache.remove(key);
    } 
  }
}
