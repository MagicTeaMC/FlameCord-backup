package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentMap;

final class MildConcurrentValues<K, V> extends MildValues<K, V> implements ConcurrentMap<K, V> {
  private final ConcurrentMap<K, Reference<V>> concurrentMap;
  
  MildConcurrentValues(ConcurrentMap<K, Reference<V>> map, boolean soft) {
    super(map, soft);
    this.concurrentMap = map;
  }
  
  public V putIfAbsent(K key, V value) {
    compact();
    Reference<V> ref = mildValue(key, value);
    Reference<V> oldRef;
    while ((oldRef = (Reference<V>)this.concurrentMap.putIfAbsent(key, (Reference)ref)) != null) {
      V oldValue = oldRef.get();
      if (oldValue != null)
        return oldValue; 
      this.concurrentMap.remove(key, oldRef);
    } 
    return null;
  }
  
  public V replace(K key, V value) {
    compact();
    Reference<V> ref = this.concurrentMap.replace(key, mildValue(key, value));
    return (ref != null) ? ref.get() : null;
  }
  
  public boolean replace(K key, V oldValue, V newValue) {
    compact();
    return this.concurrentMap.replace(key, tempValue(oldValue), mildValue(key, newValue));
  }
  
  public boolean remove(Object key, Object value) {
    compact();
    return this.concurrentMap.remove(key, tempValue((V)value));
  }
  
  void compact() {
    Reference<? extends V> ref;
    while ((ref = this.queue.poll()) != null)
      this.concurrentMap.remove(((MildValues.InverseMapping)ref).key(), ref); 
  }
}
