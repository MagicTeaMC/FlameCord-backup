package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.procedure.TShortShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedShortShortMap implements TShortShortMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TShortShortMap m;
  
  final Object mutex;
  
  public TSynchronizedShortShortMap(TShortShortMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedShortShortMap(TShortShortMap m, Object mutex) {
    this.m = m;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (this.mutex) {
      return this.m.size();
    } 
  }
  
  public boolean isEmpty() {
    synchronized (this.mutex) {
      return this.m.isEmpty();
    } 
  }
  
  public boolean containsKey(short key) {
    synchronized (this.mutex) {
      return this.m.containsKey(key);
    } 
  }
  
  public boolean containsValue(short value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public short get(short key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public short put(short key, short value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public short remove(short key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Short, ? extends Short> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TShortShortMap map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.m.clear();
    } 
  }
  
  private transient TShortSet keySet = null;
  
  private transient TShortCollection values = null;
  
  public TShortSet keySet() {
    synchronized (this.mutex) {
      if (this.keySet == null)
        this.keySet = new TSynchronizedShortSet(this.m.keySet(), this.mutex); 
      return this.keySet;
    } 
  }
  
  public short[] keys() {
    synchronized (this.mutex) {
      return this.m.keys();
    } 
  }
  
  public short[] keys(short[] array) {
    synchronized (this.mutex) {
      return this.m.keys(array);
    } 
  }
  
  public TShortCollection valueCollection() {
    synchronized (this.mutex) {
      if (this.values == null)
        this.values = new TSynchronizedShortCollection(this.m.valueCollection(), this.mutex); 
      return this.values;
    } 
  }
  
  public short[] values() {
    synchronized (this.mutex) {
      return this.m.values();
    } 
  }
  
  public short[] values(short[] array) {
    synchronized (this.mutex) {
      return this.m.values(array);
    } 
  }
  
  public TShortShortIterator iterator() {
    return this.m.iterator();
  }
  
  public short getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public short getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public short putIfAbsent(short key, short value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TShortProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TShortProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TShortShortProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TShortFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TShortShortProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(short key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(short key, short amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public short adjustOrPutValue(short key, short adjust_amount, short put_amount) {
    synchronized (this.mutex) {
      return this.m.adjustOrPutValue(key, adjust_amount, put_amount);
    } 
  }
  
  public boolean equals(Object o) {
    synchronized (this.mutex) {
      return this.m.equals(o);
    } 
  }
  
  public int hashCode() {
    synchronized (this.mutex) {
      return this.m.hashCode();
    } 
  }
  
  public String toString() {
    synchronized (this.mutex) {
      return this.m.toString();
    } 
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    synchronized (this.mutex) {
      s.defaultWriteObject();
    } 
  }
}
