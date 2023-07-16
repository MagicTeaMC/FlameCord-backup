package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.map.TByteLongMap;
import gnu.trove.procedure.TByteLongProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteLongMap implements TByteLongMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TByteLongMap m;
  
  public TUnmodifiableByteLongMap(TByteLongMap m) {
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
  
  public boolean containsValue(long val) {
    return this.m.containsValue(val);
  }
  
  public long get(byte key) {
    return this.m.get(key);
  }
  
  public long put(byte key, long value) {
    throw new UnsupportedOperationException();
  }
  
  public long remove(byte key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TByteLongMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Byte, ? extends Long> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TByteSet keySet = null;
  
  private transient TLongCollection values = null;
  
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
  
  public TLongCollection valueCollection() {
    if (this.values == null)
      this.values = TCollections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public long[] values() {
    return this.m.values();
  }
  
  public long[] values(long[] array) {
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
  
  public long getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TByteProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TByteLongProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TByteLongIterator iterator() {
    return new TByteLongIterator() {
        TByteLongIterator iter = TUnmodifiableByteLongMap.this.m.iterator();
        
        public byte key() {
          return this.iter.key();
        }
        
        public long value() {
          return this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public long setValue(long val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public long putIfAbsent(byte key, long value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TLongFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TByteLongProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(byte key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(byte key, long amount) {
    throw new UnsupportedOperationException();
  }
  
  public long adjustOrPutValue(byte key, long adjust_amount, long put_amount) {
    throw new UnsupportedOperationException();
  }
}
