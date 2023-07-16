package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

class MildKeys<K, V> implements Map<K, V> {
  final ReferenceQueue<K> queue = new ReferenceQueue<K>();
  
  final Map<Reference<K>, V> map;
  
  private final boolean soft;
  
  MildKeys(Map<Reference<K>, V> map, boolean soft) {
    this.map = map;
    this.soft = soft;
  }
  
  public final boolean containsKey(Object key) {
    return this.map.containsKey(tempKey(key));
  }
  
  public final boolean containsValue(Object value) {
    return this.map.containsValue(value);
  }
  
  public final V get(Object key) {
    return this.map.get(tempKey(key));
  }
  
  public final V put(K key, V value) {
    compact();
    return this.map.put(mildKey(key), value);
  }
  
  public final void putAll(Map<? extends K, ? extends V> m) {
    compact();
    for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
      this.map.put(mildKey(e.getKey()), e.getValue()); 
  }
  
  public final V remove(Object key) {
    compact();
    return this.map.remove(tempKey(key));
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
    return new AbstractSet<K>() {
        public Iterator<K> iterator() {
          return new MildKeys.KeyItr();
        }
        
        public int size() {
          return MildKeys.this.map.size();
        }
      };
  }
  
  public final Collection<V> values() {
    compact();
    return this.map.values();
  }
  
  public final Set<Map.Entry<K, V>> entrySet() {
    compact();
    return new AbstractSet<Map.Entry<K, V>>() {
        public Iterator<Map.Entry<K, V>> iterator() {
          return new MildKeys.EntryItr();
        }
        
        public int size() {
          return MildKeys.this.map.size();
        }
      };
  }
  
  final Reference<K> mildKey(K key) {
    return this.soft ? new Soft<K>(key, this.queue) : new Weak<K>(key, this.queue);
  }
  
  static final <K> Reference<K> tempKey(K key) {
    return new Weak<K>(key, null);
  }
  
  final void compact() {
    Reference<? extends K> ref;
    while ((ref = this.queue.poll()) != null)
      this.map.remove(ref); 
  }
  
  static class Soft<T> extends SoftReference<T> {
    private final int hash;
    
    Soft(T o, ReferenceQueue<T> queue) {
      super(o, queue);
      this.hash = o.hashCode();
    }
    
    public final int hashCode() {
      return this.hash;
    }
    
    public final boolean equals(Object rhs) {
      if (this == rhs)
        return true; 
      T o = get();
      if (o != null && rhs instanceof Reference)
        return (o == ((Reference<T>)rhs).get()); 
      return false;
    }
  }
  
  static class Weak<T> extends WeakReference<T> {
    private final int hash;
    
    Weak(T o, ReferenceQueue<T> queue) {
      super(o, queue);
      this.hash = o.hashCode();
    }
    
    public final int hashCode() {
      return this.hash;
    }
    
    public final boolean equals(Object rhs) {
      if (this == rhs)
        return true; 
      T o = get();
      if (o != null && rhs instanceof Reference)
        return (o == ((Reference<T>)rhs).get()); 
      return false;
    }
  }
  
  final class KeyItr implements Iterator<K> {
    private final Iterator<Reference<K>> itr;
    
    private K nextKey;
    
    KeyItr() {
      this.itr = MildKeys.this.map.keySet().iterator();
    }
    
    public boolean hasNext() {
      while (this.nextKey == null && this.itr.hasNext())
        this.nextKey = ((Reference<K>)this.itr.next()).get(); 
      return (this.nextKey != null);
    }
    
    public K next() {
      if (hasNext()) {
        K key = this.nextKey;
        this.nextKey = null;
        return key;
      } 
      throw new NoSuchElementException();
    }
    
    public void remove() {
      this.itr.remove();
    }
  }
  
  final class EntryItr implements Iterator<Map.Entry<K, V>> {
    private final Iterator<Map.Entry<Reference<K>, V>> itr;
    
    private Map.Entry<Reference<K>, V> nextEntry;
    
    private K nextKey;
    
    EntryItr() {
      this.itr = MildKeys.this.map.entrySet().iterator();
    }
    
    public boolean hasNext() {
      while (this.nextKey == null && this.itr.hasNext()) {
        this.nextEntry = this.itr.next();
        this.nextKey = ((Reference<K>)this.nextEntry.getKey()).get();
      } 
      return (this.nextKey != null);
    }
    
    public Map.Entry<K, V> next() {
      if (hasNext()) {
        Map.Entry<K, V> entry = new MildKeys.StrongEntry(this.nextEntry, this.nextKey);
        this.nextEntry = null;
        this.nextKey = null;
        return entry;
      } 
      throw new NoSuchElementException();
    }
    
    public void remove() {
      this.itr.remove();
    }
  }
  
  final class StrongEntry implements Map.Entry<K, V> {
    private final Map.Entry<Reference<K>, V> entry;
    
    private final K key;
    
    StrongEntry(Map.Entry<Reference<K>, V> entry, K key) {
      this.entry = entry;
      this.key = key;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.entry.getValue();
    }
    
    public V setValue(V value) {
      return this.entry.setValue(value);
    }
  }
}
