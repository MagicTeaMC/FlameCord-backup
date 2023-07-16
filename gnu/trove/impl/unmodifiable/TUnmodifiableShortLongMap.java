package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.map.TShortLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TShortLongProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortLongMap implements TShortLongMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TShortLongMap m;
  
  public TUnmodifiableShortLongMap(TShortLongMap m) {
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
  
  public boolean containsValue(long val) {
    return this.m.containsValue(val);
  }
  
  public long get(short key) {
    return this.m.get(key);
  }
  
  public long put(short key, long value) {
    throw new UnsupportedOperationException();
  }
  
  public long remove(short key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TShortLongMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Short, ? extends Long> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TShortSet keySet = null;
  
  private transient TLongCollection values = null;
  
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
  
  public short getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public long getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TShortProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TShortLongProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TShortLongIterator iterator() {
    return new TShortLongIterator() {
        TShortLongIterator iter = TUnmodifiableShortLongMap.this.m.iterator();
        
        public short key() {
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
  
  public long putIfAbsent(short key, long value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TLongFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TShortLongProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(short key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(short key, long amount) {
    throw new UnsupportedOperationException();
  }
  
  public long adjustOrPutValue(short key, long adjust_amount, long put_amount) {
    throw new UnsupportedOperationException();
  }
}
