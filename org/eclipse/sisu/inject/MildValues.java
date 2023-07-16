package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

class MildValues<K, V> implements Map<K, V> {
  final ReferenceQueue<V> queue = new ReferenceQueue<V>();
  
  final Map<K, Reference<V>> map;
  
  private final boolean soft;
  
  MildValues(Map<K, Reference<V>> map, boolean soft) {
    this.map = map;
    this.soft = soft;
  }
  
  public final boolean containsKey(Object key) {
    return this.map.containsKey(key);
  }
  
  public final boolean containsValue(Object value) {
    return this.map.containsValue(tempValue(value));
  }
  
  public final V get(Object key) {
    Reference<V> ref = this.map.get(key);
    return (ref != null) ? ref.get() : null;
  }
  
  public final V put(K key, V value) {
    compact();
    Reference<V> ref = this.map.put(key, mildValue(key, value));
    return (ref != null) ? ref.get() : null;
  }
  
  public final void putAll(Map<? extends K, ? extends V> m) {
    compact();
    for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
      this.map.put(e.getKey(), mildValue(e.getKey(), e.getValue())); 
  }
  
  public final V remove(Object key) {
    compact();
    Reference<V> ref = this.map.remove(key);
    return (ref != null) ? ref.get() : null;
  }
  
  public final void clear() {
    this.map.clear();
    compact();
  }
  
  public final boolean isEmpty() {
    compact();
    return this.map.isEmpty();
  }
  
  public final int size() {
    compact();
    return this.map.size();
  }
  
  public final Set<K> keySet() {
    compact();
    return this.map.keySet();
  }
  
  public final Collection<V> values() {
    compact();
    return new AbstractCollection<V>() {
        public Iterator<V> iterator() {
          return new MildValues.ValueItr();
        }
        
        public int size() {
          return MildValues.this.map.size();
        }
      };
  }
  
  public final Set<Map.Entry<K, V>> entrySet() {
    compact();
    return new AbstractSet<Map.Entry<K, V>>() {
        public Iterator<Map.Entry<K, V>> iterator() {
          return new MildValues.EntryItr();
        }
        
        public int size() {
          return MildValues.this.map.size();
        }
      };
  }
  
  final Reference<V> mildValue(K key, V value) {
    return this.soft ? new Soft<K, V>(key, value, this.queue) : new Weak<K, V>(key, value, this.queue);
  }
  
  static final <V> Reference<V> tempValue(V value) {
    return new Weak<Object, V>(null, value, null);
  }
  
  void compact() {
    Reference<? extends V> ref;
    while ((ref = this.queue.poll()) != null) {
      Object key = ((InverseMapping)ref).key();
      if (this.map.get(key) == ref)
        this.map.remove(key); 
    } 
  }
  
  private static final class Soft<K, V> extends MildKeys.Soft<V> implements InverseMapping {
    private final K key;
    
    Soft(K key, V value, ReferenceQueue<V> queue) {
      super(value, queue);
      this.key = key;
    }
    
    public Object key() {
      return this.key;
    }
  }
  
  private static final class Weak<K, V> extends MildKeys.Weak<V> implements InverseMapping {
    private final K key;
    
    Weak(K key, V value, ReferenceQueue<V> queue) {
      super(value, queue);
      this.key = key;
    }
    
    public Object key() {
      return this.key;
    }
  }
  
  final class ValueItr implements Iterator<V> {
    private final Iterator<Reference<V>> itr = MildValues.this.map.values().iterator();
    
    private V nextValue;
    
    public boolean hasNext() {
      while (this.nextValue == null && this.itr.hasNext())
        this.nextValue = ((Reference<V>)this.itr.next()).get(); 
      return (this.nextValue != null);
    }
    
    public V next() {
      if (hasNext()) {
        V value = this.nextValue;
        this.nextValue = null;
        return value;
      } 
      throw new NoSuchElementException();
    }
    
    public void remove() {
      this.itr.remove();
    }
  }
  
  final class EntryItr implements Iterator<Map.Entry<K, V>> {
    private final Iterator<Map.Entry<K, Reference<V>>> itr;
    
    private Map.Entry<K, Reference<V>> nextEntry;
    
    private V nextValue;
    
    EntryItr() {
      this.itr = MildValues.this.map.entrySet().iterator();
    }
    
    public boolean hasNext() {
      while (this.nextValue == null && this.itr.hasNext()) {
        this.nextEntry = this.itr.next();
        this.nextValue = ((Reference<V>)this.nextEntry.getValue()).get();
      } 
      return (this.nextValue != null);
    }
    
    public Map.Entry<K, V> next() {
      if (hasNext()) {
        Map.Entry<K, V> entry = new MildValues.StrongEntry(this.nextEntry, this.nextValue);
        this.nextEntry = null;
        this.nextValue = null;
        return entry;
      } 
      throw new NoSuchElementException();
    }
    
    public void remove() {
      this.itr.remove();
    }
  }
  
  final class StrongEntry implements Map.Entry<K, V> {
    private final Map.Entry<K, Reference<V>> entry;
    
    private V value;
    
    StrongEntry(Map.Entry<K, Reference<V>> entry, V value) {
      this.entry = entry;
      this.value = value;
    }
    
    public K getKey() {
      return this.entry.getKey();
    }
    
    public V getValue() {
      return this.value;
    }
    
    public V setValue(V newValue) {
      V oldValue = this.value;
      this.entry.setValue(MildValues.this.mildValue(getKey(), newValue));
      this.value = newValue;
      return oldValue;
    }
  }
  
  static interface InverseMapping {
    Object key();
  }
}
