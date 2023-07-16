package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableIntObjectMap<V> implements TIntObjectMap<V>, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TIntObjectMap<V> m;
  
  public TUnmodifiableIntObjectMap(TIntObjectMap<V> m) {
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
  
  public boolean containsKey(int key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(Object val) {
    return this.m.containsValue(val);
  }
  
  public V get(int key) {
    return (V)this.m.get(key);
  }
  
  public V put(int key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public V remove(int key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TIntObjectMap<? extends V> m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Integer, ? extends V> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TIntSet keySet = null;
  
  private transient Collection<V> values = null;
  
  public TIntSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public int[] keys() {
    return this.m.keys();
  }
  
  public int[] keys(int[] array) {
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
  
  public int getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public boolean forEachKey(TIntProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TIntObjectProcedure<? super V> procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TIntObjectIterator<V> iterator() {
    return new TIntObjectIterator<V>() {
        TIntObjectIterator<V> iter = TUnmodifiableIntObjectMap.this.m.iterator();
        
        public int key() {
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
  
  public V putIfAbsent(int key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TObjectFunction<V, V> function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TIntObjectProcedure<? super V> procedure) {
    throw new UnsupportedOperationException();
  }
}
