package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatDoubleMap implements TFloatDoubleMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TFloatDoubleMap m;
  
  public TUnmodifiableFloatDoubleMap(TFloatDoubleMap m) {
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
  
  public boolean containsValue(double val) {
    return this.m.containsValue(val);
  }
  
  public double get(float key) {
    return this.m.get(key);
  }
  
  public double put(float key, double value) {
    throw new UnsupportedOperationException();
  }
  
  public double remove(float key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TFloatDoubleMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Float, ? extends Double> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TFloatSet keySet = null;
  
  private transient TDoubleCollection values = null;
  
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
  
  public TDoubleCollection valueCollection() {
    if (this.values == null)
      this.values = TCollections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public double[] values() {
    return this.m.values();
  }
  
  public double[] values(double[] array) {
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
  
  public double getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TFloatProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TDoubleProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TFloatDoubleProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TFloatDoubleIterator iterator() {
    return new TFloatDoubleIterator() {
        TFloatDoubleIterator iter = TUnmodifiableFloatDoubleMap.this.m.iterator();
        
        public float key() {
          return this.iter.key();
        }
        
        public double value() {
          return this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public double setValue(double val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public double putIfAbsent(float key, double value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TDoubleFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TFloatDoubleProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(float key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(float key, double amount) {
    throw new UnsupportedOperationException();
  }
  
  public double adjustOrPutValue(float key, double adjust_amount, double put_amount) {
    throw new UnsupportedOperationException();
  }
}
