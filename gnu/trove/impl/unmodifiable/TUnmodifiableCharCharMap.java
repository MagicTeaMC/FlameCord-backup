package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.map.TCharCharMap;
import gnu.trove.procedure.TCharCharProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharCharMap implements TCharCharMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TCharCharMap m;
  
  public TUnmodifiableCharCharMap(TCharCharMap m) {
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
  
  public boolean containsValue(char val) {
    return this.m.containsValue(val);
  }
  
  public char get(char key) {
    return this.m.get(key);
  }
  
  public char put(char key, char value) {
    throw new UnsupportedOperationException();
  }
  
  public char remove(char key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TCharCharMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Character, ? extends Character> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TCharSet keySet = null;
  
  private transient TCharCollection values = null;
  
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
  
  public char getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public char getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TCharProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TCharCharProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TCharCharIterator iterator() {
    return new TCharCharIterator() {
        TCharCharIterator iter = TUnmodifiableCharCharMap.this.m.iterator();
        
        public char key() {
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
  
  public char putIfAbsent(char key, char value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TCharFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TCharCharProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(char key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(char key, char amount) {
    throw new UnsupportedOperationException();
  }
  
  public char adjustOrPutValue(char key, char adjust_amount, char put_amount) {
    throw new UnsupportedOperationException();
  }
}
