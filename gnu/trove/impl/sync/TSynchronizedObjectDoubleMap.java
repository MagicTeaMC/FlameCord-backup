package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectDoubleMap<K> implements TObjectDoubleMap<K>, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TObjectDoubleMap<K> m;
  
  final Object mutex;
  
  public TSynchronizedObjectDoubleMap(TObjectDoubleMap<K> m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedObjectDoubleMap(TObjectDoubleMap<K> m, Object mutex) {
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
  
  public boolean containsValue(double value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public double get(Object key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public double put(K key, double value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public double remove(Object key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends K, ? extends Double> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TObjectDoubleMap<? extends K> map) {
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
  
  private transient TDoubleCollection values = null;
  
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
  
  public TDoubleCollection valueCollection() {
    synchronized (this.mutex) {
      if (this.values == null)
        this.values = new TSynchronizedDoubleCollection(this.m.valueCollection(), this.mutex); 
      return this.values;
    } 
  }
  
  public double[] values() {
    synchronized (this.mutex) {
      return this.m.values();
    } 
  }
  
  public double[] values(double[] array) {
    synchronized (this.mutex) {
      return this.m.values(array);
    } 
  }
  
  public TObjectDoubleIterator<K> iterator() {
    return this.m.iterator();
  }
  
  public double getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public double putIfAbsent(K key, double value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TObjectDoubleProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TDoubleFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TObjectDoubleProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(K key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(K key, double amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public double adjustOrPutValue(K key, double adjust_amount, double put_amount) {
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
