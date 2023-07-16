package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TShortFloatIterator;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TShortFloatProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortFloatMap implements TShortFloatMap, Serializable {
  private static final long serialVersionUID = -1034234728574286014L;
  
  private final TShortFloatMap m;
  
  public TUnmodifiableShortFloatMap(TShortFloatMap m) {
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
  
  public boolean containsValue(float val) {
    return this.m.containsValue(val);
  }
  
  public float get(short key) {
    return this.m.get(key);
  }
  
  public float put(short key, float value) {
    throw new UnsupportedOperationException();
  }
  
  public float remove(short key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(TShortFloatMap m) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends Short, ? extends Float> map) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  private transient TShortSet keySet = null;
  
  private transient TFloatCollection values = null;
  
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
  
  public TFloatCollection valueCollection() {
    if (this.values == null)
      this.values = TCollections.unmodifiableCollection(this.m.valueCollection()); 
    return this.values;
  }
  
  public float[] values() {
    return this.m.values();
  }
  
  public float[] values(float[] array) {
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
  
  public float getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public boolean forEachKey(TShortProcedure procedure) {
    return this.m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TFloatProcedure procedure) {
    return this.m.forEachValue(procedure);
  }
  
  public boolean forEachEntry(TShortFloatProcedure procedure) {
    return this.m.forEachEntry(procedure);
  }
  
  public TShortFloatIterator iterator() {
    return new TShortFloatIterator() {
        TShortFloatIterator iter = TUnmodifiableShortFloatMap.this.m.iterator();
        
        public short key() {
          return this.iter.key();
        }
        
        public float value() {
          return this.iter.value();
        }
        
        public void advance() {
          this.iter.advance();
        }
        
        public boolean hasNext() {
          return this.iter.hasNext();
        }
        
        public float setValue(float val) {
          throw new UnsupportedOperationException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public float putIfAbsent(short key, float value) {
    throw new UnsupportedOperationException();
  }
  
  public void transformValues(TFloatFunction function) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainEntries(TShortFloatProcedure procedure) {
    throw new UnsupportedOperationException();
  }
  
  public boolean increment(short key) {
    throw new UnsupportedOperationException();
  }
  
  public boolean adjustValue(short key, float amount) {
    throw new UnsupportedOperationException();
  }
  
  public float adjustOrPutValue(short key, float adjust_amount, float put_amount) {
    throw new UnsupportedOperationException();
  }
}
