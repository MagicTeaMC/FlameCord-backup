package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TSynchronizedFloatObjectMap<V> implements TFloatObjectMap<V>, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TFloatObjectMap<V> m;
  
  final Object mutex;
  
  public TSynchronizedFloatObjectMap(TFloatObjectMap<V> m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedFloatObjectMap(TFloatObjectMap<V> m, Object mutex) {
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
  
  public boolean containsValue(Object value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public V get(float key) {
    synchronized (this.mutex) {
      return (V)this.m.get(key);
    } 
  }
  
  public V put(float key, V value) {
    synchronized (this.mutex) {
      return (V)this.m.put(key, value);
    } 
  }
  
  public V remove(float key) {
    synchronized (this.mutex) {
      return (V)this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Float, ? extends V> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TFloatObjectMap<? extends V> map) {
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
  
  private transient Collection<V> values = null;
  
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
  
  public Collection<V> valueCollection() {
    synchronized (this.mutex) {
      if (this.values == null)
        this.values = new SynchronizedCollection<V>(this.m.valueCollection(), this.mutex); 
      return this.values;
    } 
  }
  
  public Object[] values() {
    synchronized (this.mutex) {
      return this.m.values();
    } 
  }
  
  public V[] values(V[] array) {
    synchronized (this.mutex) {
      return (V[])this.m.values((Object[])array);
    } 
  }
  
  public TFloatObjectIterator<V> iterator() {
    return this.m.iterator();
  }
  
  public float getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public V putIfAbsent(float key, V value) {
    synchronized (this.mutex) {
      return (V)this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TFloatProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TFloatObjectProcedure<? super V> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TObjectFunction<V, V> function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TFloatObjectProcedure<? super V> procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
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
