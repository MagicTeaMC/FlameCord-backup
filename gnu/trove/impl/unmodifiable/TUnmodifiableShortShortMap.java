package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.procedure.TShortShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortShortMap implements TShortShortMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TShortShortMap m;
  
  public TUnmodifiableShortShortMap(TShortShortMap m) {
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
  
  public boolean containsValue(short val) {
    return this.m.containsValue(val);
  }
  
  public short get(short key) {
    return this.m.get(key);
  }
  
  public short put(short key, short value) {
    throw new UnsupportedOperationException();
  }
  
  public short remove(short key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TShortShortMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Short, ? extends Short> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TShortSet keySet = null;
  
  private transient TShortCollection values = null;
  
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
  
  public short getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public short getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TShortProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TShortProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TShortShortProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TShortShortIterator iterator() {
    return new TShortShortIterator() {
        TShortShortIterator iter = TUnmodifiableShortShortMap.this.m.iterator();
        
        public short key() {
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
  
  public short putIfAbsent(short key, short value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TShortFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TShortShortProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(short key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(short key, short amount) {
    throw new UnsupportedOperationException();
  }
  
  public short adjustOrPutValue(short key, short adjust_amount, short put_amount) {
    throw new UnsupportedOperationException();
  }
}
