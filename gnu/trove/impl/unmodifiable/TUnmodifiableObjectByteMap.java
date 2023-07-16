package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectByteMap<K> implements TObjectByteMap<K>, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TObjectByteMap<K> m;
  
  public TUnmodifiableObjectByteMap(TObjectByteMap<K> m) {
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
  
  public boolean containsValue(byte val) {
    return this.m.containsValue(val);
  }
  
  public byte get(Object key) {
    return this.m.get(key);
  }
  
  public byte put(K key, byte value) {
    throw new UnsupportedOperationException();
  }
  
  public byte remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TObjectByteMap<? extends K> m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends K, ? extends Byte> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient Set<K> keySet = null;
  
  private transient TByteCollection values = null;
  
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
  
  public TByteCollection valueCollection() {
    if (this.values == null)
      this.values = TCollections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public byte[] values() {
    return this.m.values();
  }
  
  public byte[] values(byte[] array) {
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
  
  public byte getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TByteProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TObjectByteProcedure<? super K> procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TObjectByteIterator<K> iterator() {
    return new TObjectByteIterator<K>() {
        TObjectByteIterator<K> iter = TUnmodifiableObjectByteMap.this.m.iterator();
        
        public K key() {
          return (K)this.iter.key();
        }
        
        public byte value() {
          return this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public byte setValue(byte val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public byte putIfAbsent(K key, byte value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TByteFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TObjectByteProcedure<? super K> procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(K key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(K key, byte amount) {
    throw new UnsupportedOperationException();
  }
  
  public byte adjustOrPutValue(K key, byte adjust_amount, byte put_amount) {
    throw new UnsupportedOperationException();
  }
}
