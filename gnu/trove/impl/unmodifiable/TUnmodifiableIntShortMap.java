package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.map.TIntShortMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TIntShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntShortMap implements TIntShortMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TIntShortMap m;
  
  public TUnmodifiableIntShortMap(TIntShortMap m) {
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
  
  public boolean containsValue(short val) {
    return this.m.containsValue(val);
  }
  
  public short get(int key) {
    return this.m.get(key);
  }
  
  public short put(int key, short value) {
    throw new UnsupportedOperationException();
  }
  
  public short remove(int key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TIntShortMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Integer, ? extends Short> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TIntSet keySet = null;
  
  private transient TShortCollection values = null;
  
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
  
  public int getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public short getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TIntProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TShortProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TIntShortProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TIntShortIterator iterator() {
    return new TIntShortIterator() {
        TIntShortIterator iter = TUnmodifiableIntShortMap.this.m.iterator();
        
        public int key() {
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
  
  public short putIfAbsent(int key, short value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TShortFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TIntShortProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(int key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(int key, short amount) {
    throw new UnsupportedOperationException();
  }
  
  public short adjustOrPutValue(int key, short adjust_amount, short put_amount) {
    throw new UnsupportedOperationException();
  }
}
