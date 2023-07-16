package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectDoubleMap<K> implements TObjectDoubleMap<K>, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TObjectDoubleMap<K> m;
  
  public TUnmodifiableObjectDoubleMap(TObjectDoubleMap<K> m) {
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
  
  public boolean containsValue(double val) {
    return this.m.containsValue(val);
  }
  
  public double get(Object key) {
    return this.m.get(key);
  }
  
  public double put(K key, double value) {
    throw new UnsupportedOperationException();
  }
  
  public double remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TObjectDoubleMap<? extends K> m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends K, ? extends Double> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient Set<K> keySet = null;
  
  private transient TDoubleCollection values = null;
  
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
  
  public TDoubleCollection valueCollection() {
    if (this.values == null)
      this.values = TCollections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public double[] values() {
    return this.m.values();
  }
  
  public double[] values(double[] array) {
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
  
  public double getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TDoubleProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TObjectDoubleProcedure<? super K> procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TObjectDoubleIterator<K> iterator() {
    return new TObjectDoubleIterator<K>() {
        TObjectDoubleIterator<K> iter = TUnmodifiableObjectDoubleMap.this.m.iterator();
        
        public K key() {
          return (K)this.iter.key();
        }
        
        public double value() {
          return this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public double setValue(double val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public double putIfAbsent(K key, double value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TDoubleFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TObjectDoubleProcedure<? super K> procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(K key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(K key, double amount) {
    throw new UnsupportedOperationException();
  }
  
  public double adjustOrPutValue(K key, double adjust_amount, double put_amount) {
    throw new UnsupportedOperationException();
  }
}
