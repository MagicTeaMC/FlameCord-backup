package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.procedure.TIntLongProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntLongMap implements TIntLongMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TIntLongMap m;
  
  public TUnmodifiableIntLongMap(TIntLongMap m) {
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
  
  public boolean containsValue(long val) {
    return this.m.containsValue(val);
  }
  
  public long get(int key) {
    return this.m.get(key);
  }
  
  public long put(int key, long value) {
    throw new UnsupportedOperationException();
  }
  
  public long remove(int key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TIntLongMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Integer, ? extends Long> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TIntSet keySet = null;
  
  private transient TLongCollection values = null;
  
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
  
  public int getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public long getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TIntProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TIntLongProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TIntLongIterator iterator() {
    return new TIntLongIterator() {
        TIntLongIterator iter = TUnmodifiableIntLongMap.this.m.iterator();
        
        public int key() {
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
  
  public long putIfAbsent(int key, long value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TLongFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TIntLongProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(int key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(int key, long amount) {
    throw new UnsupportedOperationException();
  }
  
  public long adjustOrPutValue(int key, long adjust_amount, long put_amount) {
    throw new UnsupportedOperationException();
  }
}
