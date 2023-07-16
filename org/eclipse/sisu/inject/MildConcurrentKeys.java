package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentMap;

final class MildConcurrentKeys<K, V> extends MildKeys<K, V> implements ConcurrentMap<K, V> {
  private final ConcurrentMap<Reference<K>, V> concurrentMap;
  
  MildConcurrentKeys(ConcurrentMap<Reference<K>, V> map, boolean soft) {
    super(map, soft);
    this.concurrentMap = map;
  }
  
  public V putIfAbsent(K key, V value) {
    compact();
    return this.concurrentMap.putIfAbsent(mildKey(key), value);
  }
  
  public V replace(K key, V value) {
    compact();
    return this.concurrentMap.replace(mildKey(key), value);
  }
  
  public boolean replace(K key, V oldValue, V newValue) {
    compact();
    return this.concurrentMap.replace(mildKey(key), oldValue, newValue);
  }
  
  public boolean remove(Object key, Object value) {
    compact();
    return this.concurrentMap.remove(tempKey((K)key), value);
  }
}
