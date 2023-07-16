package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectCharMap<K> implements TObjectCharMap<K>, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TObjectCharMap<K> m;
  
  public TUnmodifiableObjectCharMap(TObjectCharMap<K> m) {
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
  
  public boolean containsValue(char val) {
    return this.m.containsValue(val);
  }
  
  public char get(Object key) {
    return this.m.get(key);
  }
  
  public char put(K key, char value) {
    throw new UnsupportedOperationException();
  }
  
  public char remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TObjectCharMap<? extends K> m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends K, ? extends Character> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient Set<K> keySet = null;
  
  private transient TCharCollection values = null;
  
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
  
  public TCharCollection valueCollection() {
    if (this.values == null)
      this.values = TCollections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public char[] values() {
    return this.m.values();
  }
  
  public char[] values(char[] array) {
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
  
  public char getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TCharProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TObjectCharProcedure<? super K> procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TObjectCharIterator<K> iterator() {
    return new TObjectCharIterator<K>() {
        TObjectCharIterator<K> iter = TUnmodifiableObjectCharMap.this.m.iterator();
        
        public K key() {
          return (K)this.iter.key();
        }
        
        public char value() {
          return this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public char setValue(char val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public char putIfAbsent(K key, char value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TCharFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TObjectCharProcedure<? super K> procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(K key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(K key, char amount) {
    throw new UnsupportedOperationException();
  }
  
  public char adjustOrPutValue(K key, char adjust_amount, char put_amount) {
    throw new UnsupportedOperationException();
  }
}
