package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.procedure.TDoubleLongProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleLongMap implements TDoubleLongMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TDoubleLongMap m;
  
  public TUnmodifiableDoubleLongMap(TDoubleLongMap m) {
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
  
  public boolean containsValue(long val) {
    return this.m.containsValue(val);
  }
  
  public long get(double key) {
    return this.m.get(key);
  }
  
  public long put(double key, long value) {
    throw new UnsupportedOperationException();
  }
  
  public long remove(double key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TDoubleLongMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Double, ? extends Long> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TDoubleSet keySet = null;
  
  private transient TLongCollection values = null;
  
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
  
  public double getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public long getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TDoubleProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TDoubleLongProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TDoubleLongIterator iterator() {
    return new TDoubleLongIterator() {
        TDoubleLongIterator iter = TUnmodifiableDoubleLongMap.this.m.iterator();
        
        public double key() {
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
  
  public long putIfAbsent(double key, long value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TLongFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TDoubleLongProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(double key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(double key, long amount) {
    throw new UnsupportedOperationException();
  }
  
  public long adjustOrPutValue(double key, long adjust_amount, long put_amount) {
    throw new UnsupportedOperationException();
  }
}
