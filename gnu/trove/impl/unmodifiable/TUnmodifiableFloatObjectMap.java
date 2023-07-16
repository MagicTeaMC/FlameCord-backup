package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableFloatObjectMap<V> implements TFloatObjectMap<V>, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TFloatObjectMap<V> m;
  
  public TUnmodifiableFloatObjectMap(TFloatObjectMap<V> m) {
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
  
  public boolean containsKey(float key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(Object val) {
    return this.m.containsValue(val);
  }
  
  public V get(float key) {
    return (V)this.m.get(key);
  }
  
  public V put(float key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public V remove(float key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TFloatObjectMap<? extends V> m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Float, ? extends V> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TFloatSet keySet = null;
  
  private transient Collection<V> values = null;
  
  public TFloatSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public float[] keys() {
    return this.m.keys();
  }
  
  public float[] keys(float[] array) {
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
  
  public float getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public boolean forEachKey(TFloatProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TFloatObjectProcedure<? super V> procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TFloatObjectIterator<V> iterator() {
    return new TFloatObjectIterator<V>() {
        TFloatObjectIterator<V> iter = TUnmodifiableFloatObjectMap.this.m.iterator();
        
        public float key() {
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
  
  public V putIfAbsent(float key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TObjectFunction<V, V> function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TFloatObjectProcedure<? super V> procedure) {
    throw new UnsupportedOperationException();
  }
}
