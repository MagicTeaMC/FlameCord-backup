package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntDoubleMap implements TIntDoubleMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TIntDoubleMap m;
  
  public TUnmodifiableIntDoubleMap(TIntDoubleMap m) {
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
  
  public boolean containsKey(int key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(double val) {
    return this.m.containsValue(val);
  }
  
  public double get(int key) {
    return this.m.get(key);
  }
  
  public double put(int key, double value) {
    throw new UnsupportedOperationException();
  }
  
  public double remove(int key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TIntDoubleMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Integer, ? extends Double> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TIntSet keySet = null;
  
  private transient TDoubleCollection values = null;
  
  public TIntSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public int[] keys() {
    return this.m.keys();
  }
  
  public int[] keys(int[] array) {
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
  
  public int getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public double getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TIntProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TDoubleProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TIntDoubleProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TIntDoubleIterator iterator() {
    return new TIntDoubleIterator() {
        TIntDoubleIterator iter = TUnmodifiableIntDoubleMap.this.m.iterator();
        
        public int key() {
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
  
  public double putIfAbsent(int key, double value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TDoubleFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TIntDoubleProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(int key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(int key, double amount) {
    throw new UnsupportedOperationException();
  }
  
  public double adjustOrPutValue(int key, double adjust_amount, double put_amount) {
    throw new UnsupportedOperationException();
  }
}
