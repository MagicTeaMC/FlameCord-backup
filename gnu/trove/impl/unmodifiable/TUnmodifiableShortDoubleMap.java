package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TShortDoubleProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortDoubleMap implements TShortDoubleMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TShortDoubleMap m;
  
  public TUnmodifiableShortDoubleMap(TShortDoubleMap m) {
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
  
  public boolean containsKey(short key) {
    return this.m.containsKey(key);
  }
  
  public boolean containsValue(double val) {
    return this.m.containsValue(val);
  }
  
  public double get(short key) {
    return this.m.get(key);
  }
  
  public double put(short key, double value) {
    throw new UnsupportedOperationException();
  }
  
  public double remove(short key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TShortDoubleMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Short, ? extends Double> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TShortSet keySet = null;
  
  private transient TDoubleCollection values = null;
  
  public TShortSet keySet() {
    if (this.keySet == null)
      this.keySet = TCollections.unmodifiableSet(this.m.keySet()); 
    return this.keySet;
  }
  
  public short[] keys() {
    return this.m.keys();
  }
  
  public short[] keys(short[] array) {
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
  
  public short getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public double getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TShortProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TDoubleProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TShortDoubleProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TShortDoubleIterator iterator() {
    return new TShortDoubleIterator() {
        TShortDoubleIterator iter = TUnmodifiableShortDoubleMap.this.m.iterator();
        
        public short key() {
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
  
  public double putIfAbsent(short key, double value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TDoubleFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TShortDoubleProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(short key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(short key, double amount) {
    throw new UnsupportedOperationException();
  }
  
  public double adjustOrPutValue(short key, double adjust_amount, double put_amount) {
    throw new UnsupportedOperationException();
  }
}
