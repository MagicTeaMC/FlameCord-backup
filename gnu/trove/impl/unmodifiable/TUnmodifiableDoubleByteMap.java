package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TDoubleByteIterator;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleByteMap implements TDoubleByteMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TDoubleByteMap m;
  
  public TUnmodifiableDoubleByteMap(TDoubleByteMap m) {
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
  
  public boolean containsKey(double key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(byte val) {
    return this.m.containsValue(val);
  }
  
  public byte get(double key) {
    return this.m.get(key);
  }
  
  public byte put(double key, byte value) {
    throw new UnsupportedOperationException();
  }
  
  public byte remove(double key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TDoubleByteMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Double, ? extends Byte> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TDoubleSet keySet = null;
  
  private transient TByteCollection values = null;
  
  public TDoubleSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public double[] keys() {
    return this.m.keys();
  }
  
  public double[] keys(double[] array) {
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
  
  public double getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public byte getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TDoubleProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TByteProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TDoubleByteProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TDoubleByteIterator iterator() {
    return new TDoubleByteIterator() {
        TDoubleByteIterator iter = TUnmodifiableDoubleByteMap.this.m.iterator();
        
        public double key() {
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
  
  public byte putIfAbsent(double key, byte value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TByteFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TDoubleByteProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(double key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(double key, byte amount) {
    throw new UnsupportedOperationException();
  }
  
  public byte adjustOrPutValue(double key, byte adjust_amount, byte put_amount) {
    throw new UnsupportedOperationException();
  }
}
