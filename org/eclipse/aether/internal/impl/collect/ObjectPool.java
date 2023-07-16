package org.eclipse.aether.internal.impl.collect;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

class ObjectPool<T> {
  private final Map<Object, Reference<T>> objects = new WeakHashMap<>(256);
  
  public synchronized T intern(T object) {
    Reference<T> pooledRef = this.objects.get(object);
    if (pooledRef != null) {
      T pooled = pooledRef.get();
      if (pooled != null)
        return pooled; 
    } 
    this.objects.put(object, new WeakReference<>(object));
    return object;
  }
}
