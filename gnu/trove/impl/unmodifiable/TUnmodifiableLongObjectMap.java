package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableLongObjectMap<V> implements TLongObjectMap<V>, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TLongObjectMap<V> m;
  
  public TUnmodifiableLongObjectMap(TLongObjectMap<V> m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
  }
  
  public int size() {
    return this.m.size();
  }
  
  public boolean isEmpty() {
    return this.m.isEmpty();
  }
  
  public boolean containsKey(long key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(Object val) {
    return this.m.containsValue(val);
  }
  
  public V get(long key) {
    return (V)this.m.get(key);
  }
  
  public V put(long key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public V remove(long key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TLongObjectMap<? extends V> m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Long, ? extends V> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TLongSet keySet = null;
  
  private transient Collection<V> values = null;
  
  public TLongSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public long[] keys() {
    return this.m.keys();
  }
  
  public long[] keys(long[] array) {
    return this.m.keys(array);
  }
  
  public Collection<V> valueCollection() {
    if (this.values == null)
      this.values = Collections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public Object[] values() {
    return this.m.values();
  }
  
  public V[] values(V[] array) {
    return (V[])this.m.values((Object[])array);
  }
  
  public boolean equals(Object o) {
    return (o == this || this.m.equals(o));
  }
  
  public int hashCode() {
    return this.m.hashCode();
  }
  
  public String toString() {
    return this.m.toString();
  }
  
  public long getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public boolean forEachKey(TLongProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TLongObjectProcedure<? super V> procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TLongObjectIterator<V> iterator() {
    return new TLongObjectIterator<V>() {
        TLongObjectIterator<V> iter = TUnmodifiableLongObjectMap.this.m.iterator();
        
        public long key() {
          return this.iter.key();
        }
        
        public V value() {
          return (V)this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public V setValue(V val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public V putIfAbsent(long key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TObjectFunction<V, V> function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TLongObjectProcedure<? super V> procedure) {
    throw new UnsupportedOperationException();
  }
}
