package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectFloatMap<K> implements TObjectFloatMap<K>, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TObjectFloatMap<K> m;
  
  final Object mutex;
  
  public TSynchronizedObjectFloatMap(TObjectFloatMap<K> m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedObjectFloatMap(TObjectFloatMap<K> m, Object mutex) {
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
  
  public boolean containsKey(Object key) {
    synchronized (this.mutex) {
      return this.m.containsKey(key);
    } 
  }
  
  public boolean containsValue(float value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public float get(Object key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public float put(K key, float value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public float remove(Object key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends K, ? extends Float> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TObjectFloatMap<? extends K> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.m.clear();
    } 
  }
  
  private transient Set<K> keySet = null;
  
  private transient TFloatCollection values = null;
  
  public Set<K> keySet() {
    synchronized (this.mutex) {
      if (this.keySet == null)
        this.keySet = new SynchronizedSet<K>(this.m.keySet(), this.mutex); 
      return this.keySet;
    } 
  }
  
  public Object[] keys() {
    synchronized (this.mutex) {
      return this.m.keys();
    } 
  }
  
  public K[] keys(K[] array) {
    synchronized (this.mutex) {
      return (K[])this.m.keys((Object[])array);
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
  
  public TObjectFloatIterator<K> iterator() {
    return this.m.iterator();
  }
  
  public float getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public float putIfAbsent(K key, float value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TFloatProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TObjectFloatProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TFloatFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TObjectFloatProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(K key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(K key, float amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public float adjustOrPutValue(K key, float adjust_amount, float put_amount) {
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
