package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TDoubleShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleShortMap implements TDoubleShortMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TDoubleShortMap m;
  
  public TUnmodifiableDoubleShortMap(TDoubleShortMap m) {
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
  
  public boolean containsValue(short val) {
    return this.m.containsValue(val);
  }
  
  public short get(double key) {
    return this.m.get(key);
  }
  
  public short put(double key, short value) {
    throw new UnsupportedOperationException();
  }
  
  public short remove(double key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TDoubleShortMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Double, ? extends Short> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TDoubleSet keySet = null;
  
  private transient TShortCollection values = null;
  
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
  
  public TShortCollection valueCollection() {
    if (this.values == null)
      this.values = TCollections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public short[] values() {
    return this.m.values();
  }
  
  public short[] values(short[] array) {
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
  
  public short getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TDoubleProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TShortProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TDoubleShortProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TDoubleShortIterator iterator() {
    return new TDoubleShortIterator() {
        TDoubleShortIterator iter = TUnmodifiableDoubleShortMap.this.m.iterator();
        
        public double key() {
          return this.iter.key();
        }
        
        public short value() {
          return this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public short setValue(short val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public short putIfAbsent(double key, short value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TShortFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TDoubleShortProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(double key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(double key, short amount) {
    throw new UnsupportedOperationException();
  }
  
  public short adjustOrPutValue(double key, short adjust_amount, short put_amount) {
    throw new UnsupportedOperationException();
  }
}
