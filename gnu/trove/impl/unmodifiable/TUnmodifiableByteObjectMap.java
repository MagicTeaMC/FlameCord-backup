package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableByteObjectMap<V> implements TByteObjectMap<V>, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TByteObjectMap<V> m;
  
  public TUnmodifiableByteObjectMap(TByteObjectMap<V> m) {
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
  
  public boolean containsKey(byte key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(Object val) {
    return this.m.containsValue(val);
  }
  
  public V get(byte key) {
    return (V)this.m.get(key);
  }
  
  public V put(byte key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public V remove(byte key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TByteObjectMap<? extends V> m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Byte, ? extends V> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TByteSet keySet = null;
  
  private transient Collection<V> values = null;
  
  public TByteSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public byte[] keys() {
    return this.m.keys();
  }
  
  public byte[] keys(byte[] array) {
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
  
  public byte getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public boolean forEachKey(TByteProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TByteObjectProcedure<? super V> procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TByteObjectIterator<V> iterator() {
    return new TByteObjectIterator<V>() {
        TByteObjectIterator<V> iter = TUnmodifiableByteObjectMap.this.m.iterator();
        
        public byte key() {
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
  
  public V putIfAbsent(byte key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TObjectFunction<V, V> function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TByteObjectProcedure<? super V> procedure) {
    throw new UnsupportedOperationException();
  }
}
