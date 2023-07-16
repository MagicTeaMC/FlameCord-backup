package org.eclipse.aether.internal.impl.collect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;

class DefaultDependencyGraphTransformationContext implements DependencyGraphTransformationContext {
  private final RepositorySystemSession session;
  
  private final Map<Object, Object> map;
  
  DefaultDependencyGraphTransformationContext(RepositorySystemSession session) {
    this.session = session;
    this.map = new HashMap<>();
  }
  
  public RepositorySystemSession getSession() {
    return this.session;
  }
  
  public Object get(Object key) {
    return this.map.get(Objects.requireNonNull(key, "key cannot be null"));
  }
  
  public Object put(Object key, Object value) {
    Objects.requireNonNull(key, "key cannot be null");
    if (value != null)
      return this.map.put(key, value); 
    return this.map.remove(key);
  }
  
  public String toString() {
    return String.valueOf(this.map);
  }
}
