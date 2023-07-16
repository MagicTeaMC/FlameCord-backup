package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TByteFloatIterator;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.procedure.TByteFloatProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteFloatMap implements TByteFloatMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TByteFloatMap m;
  
  public TUnmodifiableByteFloatMap(TByteFloatMap m) {
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
  
  public boolean containsValue(float val) {
    return this.m.containsValue(val);
  }
  
  public float get(byte key) {
    return this.m.get(key);
  }
  
  public float put(byte key, float value) {
    throw new UnsupportedOperationException();
  }
  
  public float remove(byte key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TByteFloatMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Byte, ? extends Float> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TByteSet keySet = null;
  
  private transient TFloatCollection values = null;
  
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
  
  public TFloatCollection valueCollection() {
    if (this.values == null)
      this.values = TCollections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public float[] values() {
    return this.m.values();
  }
  
  public float[] values(float[] array) {
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
  
  public float getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TByteProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TFloatProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TByteFloatProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TByteFloatIterator iterator() {
    return new TByteFloatIterator() {
        TByteFloatIterator iter = TUnmodifiableByteFloatMap.this.m.iterator();
        
        public byte key() {
          return this.iter.key();
        }
        
        public float value() {
          return this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public float setValue(float val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public float putIfAbsent(byte key, float value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TFloatFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TByteFloatProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(byte key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(byte key, float amount) {
    throw new UnsupportedOperationException();
  }
  
  public float adjustOrPutValue(byte key, float adjust_amount, float put_amount) {
    throw new UnsupportedOperationException();
  }
}
