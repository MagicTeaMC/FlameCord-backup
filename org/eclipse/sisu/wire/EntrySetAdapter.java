package org.eclipse.sisu.wire;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

public final class EntrySetAdapter<V> extends AbstractSet<V> {
  private final Iterable<? extends Map.Entry<?, V>> iterable;
  
  public EntrySetAdapter(Iterable<? extends Map.Entry<?, V>> iterable) {
    this.iterable = iterable;
  }
  
  public Iterator<V> iterator() {
    return new ValueIterator<V>(this.iterable);
  }
  
  public boolean isEmpty() {
    return !iterator().hasNext();
  }
  
  public int size() {
    int size = 0;
    for (Iterator<?> i = this.iterable.iterator(); i.hasNext(); i.next())
      size++; 
    return size;
  }
  
  private static final class ValueIterator<V> implements Iterator<V> {
    private final Iterator<? extends Map.Entry<?, V>> iterator;
    
    ValueIterator(Iterable<? extends Map.Entry<?, V>> iterable) {
      this.iterator = iterable.iterator();
    }
    
    public boolean hasNext() {
      return this.iterator.hasNext();
    }
    
    public V next() {
      return (V)((Map.Entry)this.iterator.next()).getValue();
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
