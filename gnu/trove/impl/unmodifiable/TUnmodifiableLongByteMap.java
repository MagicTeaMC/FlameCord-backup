package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TLongByteIterator;
import gnu.trove.map.TLongByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongByteMap implements TLongByteMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TLongByteMap m;
  
  public TUnmodifiableLongByteMap(TLongByteMap m) {
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
  
  public boolean containsKey(long key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(byte val) {
    return this.m.containsValue(val);
  }
  
  public byte get(long key) {
    return this.m.get(key);
  }
  
  public byte put(long key, byte value) {
    throw new UnsupportedOperationException();
  }
  
  public byte remove(long key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TLongByteMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Long, ? extends Byte> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TLongSet keySet = null;
  
  private transient TByteCollection values = null;
  
  public TLongSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public long[] keys() {
    return this.m.keys();
  }
  
  public long[] keys(long[] array) {
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
  
  public long getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public byte getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TLongProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TByteProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TLongByteProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TLongByteIterator iterator() {
    return new TLongByteIterator() {
        TLongByteIterator iter = TUnmodifiableLongByteMap.this.m.iterator();
        
        public long key() {
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
  
  public byte putIfAbsent(long key, byte value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TByteFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TLongByteProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(long key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(long key, byte amount) {
    throw new UnsupportedOperationException();
  }
  
  public byte adjustOrPutValue(long key, byte adjust_amount, byte put_amount) {
    throw new UnsupportedOperationException();
  }
}
