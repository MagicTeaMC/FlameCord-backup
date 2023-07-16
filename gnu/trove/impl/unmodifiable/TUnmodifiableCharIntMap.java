package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.map.TCharIntMap;
import gnu.trove.procedure.TCharIntProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharIntMap implements TCharIntMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TCharIntMap m;
  
  public TUnmodifiableCharIntMap(TCharIntMap m) {
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
  
  public boolean containsValue(int val) {
    return this.m.containsValue(val);
  }
  
  public int get(char key) {
    return this.m.get(key);
  }
  
  public int put(char key, int value) {
    throw new UnsupportedOperationException();
  }
  
  public int remove(char key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TCharIntMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Character, ? extends Integer> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TCharSet keySet = null;
  
  private transient TIntCollection values = null;
  
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
  
  public char getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public int getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TIntProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TCharIntProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TCharIntIterator iterator() {
    return new TCharIntIterator() {
        TCharIntIterator iter = TUnmodifiableCharIntMap.this.m.iterator();
        
        public char key() {
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
  
  public int putIfAbsent(char key, int value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TIntFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TCharIntProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(char key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(char key, int amount) {
    throw new UnsupportedOperationException();
  }
  
  public int adjustOrPutValue(char key, int adjust_amount, int put_amount) {
    throw new UnsupportedOperationException();
  }
}
