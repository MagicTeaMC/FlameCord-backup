package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectLongMap<K> implements TObjectLongMap<K>, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TObjectLongMap<K> m;
  
  final Object mutex;
  
  public TSynchronizedObjectLongMap(TObjectLongMap<K> m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedObjectLongMap(TObjectLongMap<K> m, Object mutex) {
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
  
  public boolean containsValue(long value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public long get(Object key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public long put(K key, long value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public long remove(Object key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends K, ? extends Long> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TObjectLongMap<? extends K> map) {
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
  
  private transient TLongCollection values = null;
  
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
  
  public TLongCollection valueCollection() {
    synchronized (this.mutex) {
      if (this.values == null)
        this.values = new TSynchronizedLongCollection(this.m.valueCollection(), this.mutex); 
      return this.values;
    } 
  }
  
  public long[] values() {
    synchronized (this.mutex) {
      return this.m.values();
    } 
  }
  
  public long[] values(long[] array) {
    synchronized (this.mutex) {
      return this.m.values(array);
    } 
  }
  
  public TObjectLongIterator<K> iterator() {
    return this.m.iterator();
  }
  
  public long getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public long putIfAbsent(K key, long value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TObjectLongProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TLongFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TObjectLongProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(K key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(K key, long amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public long adjustOrPutValue(K key, long adjust_amount, long put_amount) {
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
