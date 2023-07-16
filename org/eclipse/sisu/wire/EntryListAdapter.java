package org.eclipse.sisu.wire;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

public final class EntryListAdapter<V> extends AbstractSequentialList<V> {
  private final Iterable<? extends Map.Entry<?, V>> iterable;
  
  public EntryListAdapter(Iterable<? extends Map.Entry<?, V>> iterable) {
    this.iterable = iterable;
  }
  
  public Iterator<V> iterator() {
    return new ValueIterator<V>(this.iterable);
  }
  
  public ListIterator<V> listIterator(int index) {
    return new ValueListIterator<V>(this.iterable, index);
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
  
  private static final class ValueListIterator<V> implements ListIterator<V> {
    private final Iterator<? extends Map.Entry<?, V>> iterator;
    
    private final List<Map.Entry<?, V>> entryCache = new ArrayList<Map.Entry<?, V>>();
    
    private int index;
    
    ValueListIterator(Iterable<? extends Map.Entry<?, V>> iterable, int index) {
      if (index < 0)
        throw new IndexOutOfBoundsException(); 
      this.iterator = iterable.iterator();
      try {
        while (this.index < index)
          next(); 
      } catch (NoSuchElementException noSuchElementException) {
        throw new IndexOutOfBoundsException();
      } 
    }
    
    public boolean hasNext() {
      return !(this.index >= this.entryCache.size() && !this.iterator.hasNext());
    }
    
    public boolean hasPrevious() {
      return (this.index > 0);
    }
    
    public V next() {
      if (this.index >= this.entryCache.size())
        this.entryCache.add(this.iterator.next()); 
      return (V)((Map.Entry)this.entryCache.get(this.index++)).getValue();
    }
    
    public V previous() {
      if (this.index <= 0)
        throw new NoSuchElementException(); 
      return (V)((Map.Entry)this.entryCache.get(--this.index)).getValue();
    }
    
    public int nextIndex() {
      return this.index;
    }
    
    public int previousIndex() {
      return this.index - 1;
    }
    
    public void add(V o) {
      throw new UnsupportedOperationException();
    }
    
    public void set(V o) {
      throw new UnsupportedOperationException();
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
