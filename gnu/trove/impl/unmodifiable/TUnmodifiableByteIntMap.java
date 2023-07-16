package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TByteIntIterator;
import gnu.trove.map.TByteIntMap;
import gnu.trove.procedure.TByteIntProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteIntMap implements TByteIntMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TByteIntMap m;
  
  public TUnmodifiableByteIntMap(TByteIntMap m) {
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
  
  public boolean containsValue(int val) {
    return this.m.containsValue(val);
  }
  
  public int get(byte key) {
    return this.m.get(key);
  }
  
  public int put(byte key, int value) {
    throw new UnsupportedOperationException();
  }
  
  public int remove(byte key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TByteIntMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Byte, ? extends Integer> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TByteSet keySet = null;
  
  private transient TIntCollection values = null;
  
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
  
  public byte getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public int getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TByteProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TIntProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TByteIntProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TByteIntIterator iterator() {
    return new TByteIntIterator() {
        TByteIntIterator iter = TUnmodifiableByteIntMap.this.m.iterator();
        
        public byte key() {
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
  
  public int putIfAbsent(byte key, int value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TIntFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TByteIntProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(byte key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(byte key, int amount) {
    throw new UnsupportedOperationException();
  }
  
  public int adjustOrPutValue(byte key, int adjust_amount, int put_amount) {
    throw new UnsupportedOperationException();
  }
}
