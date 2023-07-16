package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectIntMap<K> implements TObjectIntMap<K>, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TObjectIntMap<K> m;
  
  public TUnmodifiableObjectIntMap(TObjectIntMap<K> m) {
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
  
  public boolean containsValue(int val) {
    return this.m.containsValue(val);
  }
  
  public int get(Object key) {
    return this.m.get(key);
  }
  
  public int put(K key, int value) {
    throw new UnsupportedOperationException();
  }
  
  public int remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TObjectIntMap<? extends K> m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends K, ? extends Integer> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient Set<K> keySet = null;
  
  private transient TIntCollection values = null;
  
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
  
  public TIntCollection valueCollection() {
    if (this.values == null)
      this.values = TCollections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public int[] values() {
    return this.m.values();
  }
  
  public int[] values(int[] array) {
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
  
  public int getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TIntProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TObjectIntProcedure<? super K> procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TObjectIntIterator<K> iterator() {
    return new TObjectIntIterator<K>() {
        TObjectIntIterator<K> iter = TUnmodifiableObjectIntMap.this.m.iterator();
        
        public K key() {
          return (K)this.iter.key();
        }
        
        public int value() {
          return this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public int setValue(int val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public int putIfAbsent(K key, int value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TIntFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TObjectIntProcedure<? super K> procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(K key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(K key, int amount) {
    throw new UnsupportedOperationException();
  }
  
  public int adjustOrPutValue(K key, int adjust_amount, int put_amount) {
    throw new UnsupportedOperationException();
  }
}
