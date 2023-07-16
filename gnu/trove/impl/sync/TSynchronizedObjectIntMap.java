package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectIntMap<K> implements TObjectIntMap<K>, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TObjectIntMap<K> m;
  
  final Object mutex;
  
  public TSynchronizedObjectIntMap(TObjectIntMap<K> m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedObjectIntMap(TObjectIntMap<K> m, Object mutex) {
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
  
  public boolean containsValue(int value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public int get(Object key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public int put(K key, int value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public int remove(Object key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends K, ? extends Integer> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TObjectIntMap<? extends K> map) {
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
  
  private transient TIntCollection values = null;
  
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
  
  public TIntCollection valueCollection() {
    synchronized (this.mutex) {
      if (this.values == null)
        this.values = new TSynchronizedIntCollection(this.m.valueCollection(), this.mutex); 
      return this.values;
    } 
  }
  
  public int[] values() {
    synchronized (this.mutex) {
      return this.m.values();
    } 
  }
  
  public int[] values(int[] array) {
    synchronized (this.mutex) {
      return this.m.values(array);
    } 
  }
  
  public TObjectIntIterator<K> iterator() {
    return this.m.iterator();
  }
  
  public int getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public int putIfAbsent(K key, int value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TObjectIntProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TIntFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TObjectIntProcedure<? super K> procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(K key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(K key, int amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public int adjustOrPutValue(K key, int adjust_amount, int put_amount) {
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
