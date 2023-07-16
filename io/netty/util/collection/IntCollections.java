package io.netty.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class IntCollections {
  private static final IntObjectMap<Object> EMPTY_MAP = new EmptyMap();
  
  public static <V> IntObjectMap<V> emptyMap() {
    return (IntObjectMap)EMPTY_MAP;
  }
  
  public static <V> IntObjectMap<V> unmodifiableMap(IntObjectMap<V> map) {
    return new UnmodifiableMap<V>(map);
  }
  
  private static final class EmptyMap implements IntObjectMap<Object> {
    private EmptyMap() {}
    
    public Object get(int key) {
      return null;
    }
    
    public Object put(int key, Object value) {
      throw new UnsupportedOperationException("put");
    }
    
    public Object remove(int key) {
      return null;
    }
    
    public int size() {
      return 0;
    }
    
    public boolean isEmpty() {
      return true;
    }
    
    public boolean containsKey(Object key) {
      return false;
    }
    
    public void clear() {}
    
    public Set<Integer> keySet() {
      return Collections.emptySet();
    }
    
    public boolean containsKey(int key) {
      return false;
    }
    
    public boolean containsValue(Object value) {
      return false;
    }
    
    public Iterable<IntObjectMap.PrimitiveEntry<Object>> entries() {
      return Collections.emptySet();
    }
    
    public Object get(Object key) {
      return null;
    }
    
    public Object put(Integer key, Object value) {
      throw new UnsupportedOperationException();
    }
    
    public Object remove(Object key) {
      return null;
    }
    
    public void putAll(Map<? extends Integer, ?> m) {
      throw new UnsupportedOperationException();
    }
    
    public Collection<Object> values() {
      return Collections.emptyList();
    }
    
    public Set<Map.Entry<Integer, Object>> entrySet() {
      return Collections.emptySet();
    }
  }
  
  private static final class UnmodifiableMap<V> implements IntObjectMap<V> {
    private final IntObjectMap<V> map;
    
    private Set<Integer> keySet;
    
    private Set<Map.Entry<Integer, V>> entrySet;
    
    private Collection<V> values;
    
    private Iterable<IntObjectMap.PrimitiveEntry<V>> entries;
    
    UnmodifiableMap(IntObjectMap<V> map) {
      this.map = map;
    }
    
    public V get(int key) {
      return this.map.get(key);
    }
    
    public V put(int key, V value) {
      throw new UnsupportedOperationException("put");
    }
    
    public V remove(int key) {
      throw new UnsupportedOperationException("remove");
    }
    
    public int size() {
      return this.map.size();
    }
    
    public boolean isEmpty() {
      return this.map.isEmpty();
    }
    
    public void clear() {
      throw new UnsupportedOperationException("clear");
    }
    
    public boolean containsKey(int key) {
      return this.map.containsKey(key);
    }
    
    public boolean containsValue(Object value) {
      return this.map.containsValue(value);
    }
    
    public boolean containsKey(Object key) {
      return this.map.containsKey(key);
    }
    
    public V get(Object key) {
      return this.map.get(key);
    }
    
    public V put(Integer key, V value) {
      throw new UnsupportedOperationException("put");
    }
    
    public V remove(Object key) {
      throw new UnsupportedOperationException("remove");
    }
    
    public void putAll(Map<? extends Integer, ? extends V> m) {
      throw new UnsupportedOperationException("putAll");
    }
    
    public Iterable<IntObjectMap.PrimitiveEntry<V>> entries() {
      if (this.entries == null)
        this.entries = new Iterable<IntObjectMap.PrimitiveEntry<V>>() {
            public Iterator<IntObjectMap.PrimitiveEntry<V>> iterator() {
              return new IntCollections.UnmodifiableMap.IteratorImpl(IntCollections.UnmodifiableMap.this.map.entries().iterator());
            }
          }; 
      return this.entries;
    }
    
    public Set<Integer> keySet() {
      if (this.keySet == null)
        this.keySet = Collections.unmodifiableSet(this.map.keySet()); 
      return this.keySet;
    }
    
    public Set<Map.Entry<Integer, V>> entrySet() {
      if (this.entrySet == null)
        this.entrySet = Collections.unmodifiableSet(this.map.entrySet()); 
      return this.entrySet;
    }
    
    public Collection<V> values() {
      if (this.values == null)
        this.values = Collections.unmodifiableCollection(this.map.values()); 
      return this.values;
    }
    
    private class IteratorImpl implements Iterator<IntObjectMap.PrimitiveEntry<V>> {
      final Iterator<IntObjectMap.PrimitiveEntry<V>> iter;
      
      IteratorImpl(Iterator<IntObjectMap.PrimitiveEntry<V>> iter) {
        this.iter = iter;
      }
      
      public boolean hasNext() {
        return this.iter.hasNext();
      }
      
      public IntObjectMap.PrimitiveEntry<V> next() {
        if (!hasNext())
          throw new NoSuchElementException(); 
        return new IntCollections.UnmodifiableMap.EntryImpl(this.iter.next());
      }
      
      public void remove() {
        throw new UnsupportedOperationException("remove");
      }
    }
    
    private class EntryImpl implements IntObjectMap.PrimitiveEntry<V> {
      private final IntObjectMap.PrimitiveEntry<V> entry;
      
      EntryImpl(IntObjectMap.PrimitiveEntry<V> entry) {
        this.entry = entry;
      }
      
      public int key() {
        return this.entry.key();
      }
      
      public V value() {
        return this.entry.value();
      }
      
      public void setValue(V value) {
        throw new UnsupportedOperationException("setValue");
      }
    }
  }
}