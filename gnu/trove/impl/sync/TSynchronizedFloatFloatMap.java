package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.procedure.TFloatFloatProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedFloatFloatMap implements TFloatFloatMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TFloatFloatMap m;
  
  final Object mutex;
  
  public TSynchronizedFloatFloatMap(TFloatFloatMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedFloatFloatMap(TFloatFloatMap m, Object mutex) {
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
  
  public boolean containsKey(float key) {
    synchronized (this.mutex) {
      return this.m.containsKey(key);
    } 
  }
  
  public boolean containsValue(float value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public float get(float key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public float put(float key, float value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public float remove(float key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Float, ? extends Float> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TFloatFloatMap map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.m.clear();
    } 
  }
  
  private transient TFloatSet keySet = null;
  
  private transient TFloatCollection values = null;
  
  public TFloatSet keySet() {
    synchronized (this.mutex) {
      if (this.keySet == null)
        this.keySet = new TSynchronizedFloatSet(this.m.keySet(), this.mutex); 
      return this.keySet;
    } 
  }
  
  public float[] keys() {
    synchronized (this.mutex) {
      return this.m.keys();
    } 
  }
  
  public float[] keys(float[] array) {
    synchronized (this.mutex) {
      return this.m.keys(array);
    } 
  }
  
  public TFloatCollection valueCollection() {
    synchronized (this.mutex) {
      if (this.values == null)
        this.values = new TSynchronizedFloatCollection(this.m.valueCollection(), this.mutex); 
      return this.values;
    } 
  }
  
  public float[] values() {
    synchronized (this.mutex) {
      return this.m.values();
    } 
  }
  
  public float[] values(float[] array) {
    synchronized (this.mutex) {
      return this.m.values(array);
    } 
  }
  
  public TFloatFloatIterator iterator() {
    return this.m.iterator();
  }
  
  public float getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public float getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public float putIfAbsent(float key, float value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TFloatProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TFloatProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TFloatFloatProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TFloatFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TFloatFloatProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(float key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(float key, float amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public float adjustOrPutValue(float key, float adjust_amount, float put_amount) {
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