package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TCharByteIterator;
import gnu.trove.map.TCharByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharByteMap implements TCharByteMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TCharByteMap m;
  
  public TUnmodifiableCharByteMap(TCharByteMap m) {
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
  
  public boolean containsValue(byte val) {
    return this.m.containsValue(val);
  }
  
  public byte get(char key) {
    return this.m.get(key);
  }
  
  public byte put(char key, byte value) {
    throw new UnsupportedOperationException();
  }
  
  public byte remove(char key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TCharByteMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Character, ? extends Byte> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TCharSet keySet = null;
  
  private transient TByteCollection values = null;
  
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
  
  public char getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public byte getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TByteProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TCharByteProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TCharByteIterator iterator() {
    return new TCharByteIterator() {
        TCharByteIterator iter = TUnmodifiableCharByteMap.this.m.iterator();
        
        public char key() {
          return this.iter.key();
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
  
  public byte putIfAbsent(char key, byte value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TByteFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TCharByteProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(char key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(char key, byte amount) {
    throw new UnsupportedOperationException();
  }
  
  public byte adjustOrPutValue(char key, byte adjust_amount, byte put_amount) {
    throw new UnsupportedOperationException();
  }
}
