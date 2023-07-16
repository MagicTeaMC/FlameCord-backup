package org.eclipse.aether;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class DefaultSessionData implements SessionData {
  private final ConcurrentMap<Object, Object> data = new ConcurrentHashMap<>();
  
  public void set(Object key, Object value) {
    Objects.requireNonNull(key, "key cannot be null");
    if (value != null) {
      this.data.put(key, value);
    } else {
      this.data.remove(key);
    } 
  }
  
  public boolean set(Object key, Object oldValue, Object newValue) {
    Objects.requireNonNull(key, "key cannot be null");
    if (newValue != null) {
      if (oldValue == null)
        return (this.data.putIfAbsent(key, newValue) == null); 
      return this.data.replace(key, oldValue, newValue);
    } 
    if (oldValue == null)
      return !this.data.containsKey(key); 
    return this.data.remove(key, oldValue);
  }
  
  public Object get(Object key) {
    Objects.requireNonNull(key, "key cannot be null");
    return this.data.get(key);
  }
}
