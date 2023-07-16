package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectShortMap<K> implements TObjectShortMap<K>, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TObjectShortMap<K> m;
  
  public TUnmodifiableObjectShortMap(TObjectShortMap<K> m) {
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
  
  public boolean containsKey(Object key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(short val) {
    return this.m.containsValue(val);
  }
  
  public short get(Object key) {
    return this.m.get(key);
  }
  
  public short put(K key, short value) {
    throw new UnsupportedOperationException();
  }
  
  public short remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TObjectShortMap<? extends K> m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends K, ? extends Short> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient Set<K> keySet = null;
  
  private transient TShortCollection values = null;
  
  public Set<K> keySet() {
    if (this.keySet == null)
      this.keySet = Collections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public Object[] keys() {
    return this.m.keys();
  }
  
  public K[] keys(K[] array) {
    return (K[])this.m.keys((Object[])array);
  }
  
  public TShortCollection valueCollection() {
    if (this.values == null)
      this.values = TCollections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public short[] values() {
    return this.m.values();
  }
  
  public short[] values(short[] array) {
    return this.m.values(array);
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
  
  public short getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TShortProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TObjectShortProcedure<? super K> procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TObjectShortIterator<K> iterator() {
    return new TObjectShortIterator<K>() {
        TObjectShortIterator<K> iter = TUnmodifiableObjectShortMap.this.m.iterator();
        
        public K key() {
          return (K)this.iter.key();
        }
        
        public short value() {
          return this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public short setValue(short val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public short putIfAbsent(K key, short value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TShortFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TObjectShortProcedure<? super K> procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(K key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(K key, short amount) {
    throw new UnsupportedOperationException();
  }
  
  public short adjustOrPutValue(K key, short adjust_amount, short put_amount) {
    throw new UnsupportedOperationException();
  }
}
