package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.map.TShortCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TShortCharProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortCharMap implements TShortCharMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TShortCharMap m;
  
  public TUnmodifiableShortCharMap(TShortCharMap m) {
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
  
  public boolean containsKey(short key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(char val) {
    return this.m.containsValue(val);
  }
  
  public char get(short key) {
    return this.m.get(key);
  }
  
  public char put(short key, char value) {
    throw new UnsupportedOperationException();
  }
  
  public char remove(short key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TShortCharMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Short, ? extends Character> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TShortSet keySet = null;
  
  private transient TCharCollection values = null;
  
  public TShortSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public short[] keys() {
    return this.m.keys();
  }
  
  public short[] keys(short[] array) {
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
  
  public short getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public char getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TShortProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TCharProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TShortCharProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TShortCharIterator iterator() {
    return new TShortCharIterator() {
        TShortCharIterator iter = TUnmodifiableShortCharMap.this.m.iterator();
        
        public short key() {
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
  
  public char putIfAbsent(short key, char value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TCharFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TShortCharProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(short key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(short key, char amount) {
    throw new UnsupportedOperationException();
  }
  
  public char adjustOrPutValue(short key, char adjust_amount, char put_amount) {
    throw new UnsupportedOperationException();
  }
}
