package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TIntCharIterator;
import gnu.trove.map.TIntCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntCharMap implements TIntCharMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TIntCharMap m;
  
  public TUnmodifiableIntCharMap(TIntCharMap m) {
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
  
  public boolean containsValue(char val) {
    return this.m.containsValue(val);
  }
  
  public char get(int key) {
    return this.m.get(key);
  }
  
  public char put(int key, char value) {
    throw new UnsupportedOperationException();
  }
  
  public char remove(int key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TIntCharMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Integer, ? extends Character> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TIntSet keySet = null;
  
  private transient TCharCollection values = null;
  
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
  
  public int getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public char getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TIntProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TCharProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TIntCharProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TIntCharIterator iterator() {
    return new TIntCharIterator() {
        TIntCharIterator iter = TUnmodifiableIntCharMap.this.m.iterator();
        
        public int key() {
          return this.iter.key();
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
  
  public char putIfAbsent(int key, char value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TCharFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TIntCharProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(int key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(int key, char amount) {
    throw new UnsupportedOperationException();
  }
  
  public char adjustOrPutValue(int key, char adjust_amount, char put_amount) {
    throw new UnsupportedOperationException();
  }
}
