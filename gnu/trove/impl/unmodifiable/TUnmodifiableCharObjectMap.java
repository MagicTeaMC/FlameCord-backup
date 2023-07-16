package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.procedure.TCharObjectProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableCharObjectMap<V> implements TCharObjectMap<V>, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TCharObjectMap<V> m;
  
  public TUnmodifiableCharObjectMap(TCharObjectMap<V> m) {
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
  
  public boolean containsKey(char key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(Object val) {
    return this.m.containsValue(val);
  }
  
  public V get(char key) {
    return (V)this.m.get(key);
  }
  
  public V put(char key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public V remove(char key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TCharObjectMap<? extends V> m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Character, ? extends V> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TCharSet keySet = null;
  
  private transient Collection<V> values = null;
  
  public TCharSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public char[] keys() {
    return this.m.keys();
  }
  
  public char[] keys(char[] array) {
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
  
  public char getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TCharObjectProcedure<? super V> procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TCharObjectIterator<V> iterator() {
    return new TCharObjectIterator<V>() {
        TCharObjectIterator<V> iter = TUnmodifiableCharObjectMap.this.m.iterator();
        
        public char key() {
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
  
  public V putIfAbsent(char key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TObjectFunction<V, V> function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TCharObjectProcedure<? super V> procedure) {
    throw new UnsupportedOperationException();
  }
}
