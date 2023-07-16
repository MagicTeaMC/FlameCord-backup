package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TSynchronizedLongObjectMap<V> implements TLongObjectMap<V>, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TLongObjectMap<V> m;
  
  final Object mutex;
  
  public TSynchronizedLongObjectMap(TLongObjectMap<V> m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedLongObjectMap(TLongObjectMap<V> m, Object mutex) {
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
  
  public boolean containsKey(long key) {
    synchronized (this.mutex) {
      return this.m.containsKey(key);
    } 
  }
  
  public boolean containsValue(Object value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public V get(long key) {
    synchronized (this.mutex) {
      return (V)this.m.get(key);
    } 
  }
  
  public V put(long key, V value) {
    synchronized (this.mutex) {
      return (V)this.m.put(key, value);
    } 
  }
  
  public V remove(long key) {
    synchronized (this.mutex) {
      return (V)this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Long, ? extends V> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TLongObjectMap<? extends V> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.m.clear();
    } 
  }
  
  private transient TLongSet keySet = null;
  
  private transient Collection<V> values = null;
  
  public TLongSet keySet() {
    synchronized (this.mutex) {
      if (this.keySet == null)
        this.keySet = new TSynchronizedLongSet(this.m.keySet(), this.mutex); 
      return this.keySet;
    } 
  }
  
  public long[] keys() {
    synchronized (this.mutex) {
      return this.m.keys();
    } 
  }
  
  public long[] keys(long[] array) {
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
  
  public TLongObjectIterator<V> iterator() {
    return this.m.iterator();
  }
  
  public long getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public V putIfAbsent(long key, V value) {
    synchronized (this.mutex) {
      return (V)this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TLongObjectProcedure<? super V> procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TObjectFunction<V, V> function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TLongObjectProcedure<? super V> procedure) {
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
