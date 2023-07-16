package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TLongShortIterator;
import gnu.trove.map.TLongShortMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TLongShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongShortMap implements TLongShortMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TLongShortMap m;
  
  public TUnmodifiableLongShortMap(TLongShortMap m) {
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
  
  public boolean containsValue(short val) {
    return this.m.containsValue(val);
  }
  
  public short get(long key) {
    return this.m.get(key);
  }
  
  public short put(long key, short value) {
    throw new UnsupportedOperationException();
  }
  
  public short remove(long key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TLongShortMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Long, ? extends Short> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TLongSet keySet = null;
  
  private transient TShortCollection values = null;
  
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
  
  public long getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public short getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TLongProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TShortProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TLongShortProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TLongShortIterator iterator() {
    return new TLongShortIterator() {
        TLongShortIterator iter = TUnmodifiableLongShortMap.this.m.iterator();
        
        public long key() {
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
  
  public short putIfAbsent(long key, short value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TShortFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TLongShortProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(long key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(long key, short amount) {
    throw new UnsupportedOperationException();
  }
  
  public short adjustOrPutValue(long key, short adjust_amount, short put_amount) {
    throw new UnsupportedOperationException();
  }
}
