package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TByteCharIterator;
import gnu.trove.map.TByteCharMap;
import gnu.trove.procedure.TByteCharProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteCharMap implements TByteCharMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TByteCharMap m;
  
  public TUnmodifiableByteCharMap(TByteCharMap m) {
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
  
  public boolean containsKey(byte key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(char val) {
    return this.m.containsValue(val);
  }
  
  public char get(byte key) {
    return this.m.get(key);
  }
  
  public char put(byte key, char value) {
    throw new UnsupportedOperationException();
  }
  
  public char remove(byte key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TByteCharMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Byte, ? extends Character> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TByteSet keySet = null;
  
  private transient TCharCollection values = null;
  
  public TByteSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public byte[] keys() {
    return this.m.keys();
  }
  
  public byte[] keys(byte[] array) {
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
  
  public byte getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public char getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TByteProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TCharProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TByteCharProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TByteCharIterator iterator() {
    return new TByteCharIterator() {
        TByteCharIterator iter = TUnmodifiableByteCharMap.this.m.iterator();
        
        public byte key() {
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
  
  public char putIfAbsent(byte key, char value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TCharFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TByteCharProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(byte key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(byte key, char amount) {
    throw new UnsupportedOperationException();
  }
  
  public char adjustOrPutValue(byte key, char adjust_amount, char put_amount) {
    throw new UnsupportedOperationException();
  }
}
