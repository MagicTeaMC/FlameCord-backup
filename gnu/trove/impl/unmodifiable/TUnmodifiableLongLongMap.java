package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.procedure.TLongLongProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongLongMap implements TLongLongMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TLongLongMap m;
  
  public TUnmodifiableLongLongMap(TLongLongMap m) {
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
  
  public boolean containsValue(long val) {
    return this.m.containsValue(val);
  }
  
  public long get(long key) {
    return this.m.get(key);
  }
  
  public long put(long key, long value) {
    throw new UnsupportedOperationException();
  }
  
  public long remove(long key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TLongLongMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Long, ? extends Long> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TLongSet keySet = null;
  
  private transient TLongCollection values = null;
  
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
  
  public long getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public long getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TLongProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TLongLongProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TLongLongIterator iterator() {
    return new TLongLongIterator() {
        TLongLongIterator iter = TUnmodifiableLongLongMap.this.m.iterator();
        
        public long key() {
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
  
  public long putIfAbsent(long key, long value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TLongFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TLongLongProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(long key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(long key, long amount) {
    throw new UnsupportedOperationException();
  }
  
  public long adjustOrPutValue(long key, long adjust_amount, long put_amount) {
    throw new UnsupportedOperationException();
  }
}
