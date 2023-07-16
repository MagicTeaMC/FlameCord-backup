package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntIntMap implements TIntIntMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TIntIntMap m;
  
  public TUnmodifiableIntIntMap(TIntIntMap m) {
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
  
  public boolean containsValue(int val) {
    return this.m.containsValue(val);
  }
  
  public int get(int key) {
    return this.m.get(key);
  }
  
  public int put(int key, int value) {
    throw new UnsupportedOperationException();
  }
  
  public int remove(int key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TIntIntMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Integer, ? extends Integer> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TIntSet keySet = null;
  
  private transient TIntCollection values = null;
  
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
  
  public int getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public int getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TIntProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TIntProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TIntIntProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TIntIntIterator iterator() {
    return new TIntIntIterator() {
        TIntIntIterator iter = TUnmodifiableIntIntMap.this.m.iterator();
        
        public int key() {
          return this.iter.key();
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
  
  public int putIfAbsent(int key, int value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TIntFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TIntIntProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(int key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(int key, int amount) {
    throw new UnsupportedOperationException();
  }
  
  public int adjustOrPutValue(int key, int adjust_amount, int put_amount) {
    throw new UnsupportedOperationException();
  }
}
