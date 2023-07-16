package org.eclipse.sisu.wire;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class EntryMapAdapter<K, V> extends AbstractMap<K, V> {
  private final Set<Map.Entry<K, V>> entrySet;
  
  public EntryMapAdapter(Iterable<? extends Map.Entry<K, V>> iterable) {
    this.entrySet = new EntrySet<K, V>(iterable);
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    return this.entrySet;
  }
  
  public boolean isEmpty() {
    return this.entrySet.isEmpty();
  }
  
  private static final class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {
    private final Iterable<Map.Entry<K, V>> iterable;
    
    EntrySet(Iterable<? extends Map.Entry<K, V>> iterable) {
      this.iterable = (Iterable)iterable;
    }
    
    public Iterator<Map.Entry<K, V>> iterator() {
      return this.iterable.iterator();
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
  }
}
