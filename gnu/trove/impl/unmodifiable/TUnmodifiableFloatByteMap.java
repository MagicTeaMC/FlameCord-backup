package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatByteMap implements TFloatByteMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TFloatByteMap m;
  
  public TUnmodifiableFloatByteMap(TFloatByteMap m) {
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
  
  public boolean containsKey(float key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(byte val) {
    return this.m.containsValue(val);
  }
  
  public byte get(float key) {
    return this.m.get(key);
  }
  
  public byte put(float key, byte value) {
    throw new UnsupportedOperationException();
  }
  
  public byte remove(float key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TFloatByteMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Float, ? extends Byte> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TFloatSet keySet = null;
  
  private transient TByteCollection values = null;
  
  public TFloatSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public float[] keys() {
    return this.m.keys();
  }
  
  public float[] keys(float[] array) {
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
  
  public float getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public byte getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TFloatProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TByteProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TFloatByteProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TFloatByteIterator iterator() {
    return new TFloatByteIterator() {
        TFloatByteIterator iter = TUnmodifiableFloatByteMap.this.m.iterator();
        
        public float key() {
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
  
  public byte putIfAbsent(float key, byte value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TByteFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TFloatByteProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(float key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(float key, byte amount) {
    throw new UnsupportedOperationException();
  }
  
  public byte adjustOrPutValue(float key, byte adjust_amount, byte put_amount) {
    throw new UnsupportedOperationException();
  }
}
