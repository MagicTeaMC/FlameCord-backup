package org.eclipse.sisu.wire;

import com.google.inject.name.Named;
import java.util.Iterator;
import java.util.Map;

final class NamedIterableAdapter<V> implements Iterable<Map.Entry<String, V>> {
  private final Iterable<Map.Entry<Named, V>> delegate;
  
  NamedIterableAdapter(Iterable<Map.Entry<Named, V>> delegate) {
    this.delegate = delegate;
  }
  
  public Iterator<Map.Entry<String, V>> iterator() {
    return new NamedIterator<V>(this.delegate);
  }
  
  private static final class NamedIterator<V> implements Iterator<Map.Entry<String, V>> {
    private final Iterator<Map.Entry<Named, V>> iterator;
    
    NamedIterator(Iterable<Map.Entry<Named, V>> iterable) {
      this.iterator = iterable.iterator();
    }
    
    public boolean hasNext() {
      return this.iterator.hasNext();
    }
    
    public Map.Entry<String, V> next() {
      return new NamedIterableAdapter.NamedEntry<V>(this.iterator.next());
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  private static final class NamedEntry<V> implements Map.Entry<String, V> {
    private final Map.Entry<Named, V> entry;
    
    NamedEntry(Map.Entry<Named, V> entry) {
      this.entry = entry;
    }
    
    public String getKey() {
      return ((Named)this.entry.getKey()).value();
    }
    
    public V getValue() {
      return this.entry.getValue();
    }
    
    public V setValue(V value) {
      throw new UnsupportedOperationException();
    }
    
    public String toString() {
      return String.valueOf(getKey()) + "=" + getValue();
    }
  }
}
