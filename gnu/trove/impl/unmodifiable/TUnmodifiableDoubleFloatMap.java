package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.procedure.TDoubleFloatProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleFloatMap implements TDoubleFloatMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TDoubleFloatMap m;
  
  public TUnmodifiableDoubleFloatMap(TDoubleFloatMap m) {
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
  
  public boolean containsValue(float val) {
    return this.m.containsValue(val);
  }
  
  public float get(double key) {
    return this.m.get(key);
  }
  
  public float put(double key, float value) {
    throw new UnsupportedOperationException();
  }
  
  public float remove(double key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TDoubleFloatMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Double, ? extends Float> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TDoubleSet keySet = null;
  
  private transient TFloatCollection values = null;
  
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
  
  public double getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public float getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TDoubleProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TFloatProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TDoubleFloatProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TDoubleFloatIterator iterator() {
    return new TDoubleFloatIterator() {
        TDoubleFloatIterator iter = TUnmodifiableDoubleFloatMap.this.m.iterator();
        
        public double key() {
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
  
  public float putIfAbsent(double key, float value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TFloatFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TDoubleFloatProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(double key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(double key, float amount) {
    throw new UnsupportedOperationException();
  }
  
  public float adjustOrPutValue(double key, float adjust_amount, float put_amount) {
    throw new UnsupportedOperationException();
  }
}
