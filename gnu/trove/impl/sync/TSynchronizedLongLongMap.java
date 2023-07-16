package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.procedure.TLongLongProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedLongLongMap implements TLongLongMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TLongLongMap m;
  
  final Object mutex;
  
  public TSynchronizedLongLongMap(TLongLongMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedLongLongMap(TLongLongMap m, Object mutex) {
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
  
  public boolean containsValue(long value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public long get(long key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public long put(long key, long value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public long remove(long key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Long, ? extends Long> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TLongLongMap map) {
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
  
  private transient TLongCollection values = null;
  
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
  
  public TLongLongIterator iterator() {
    return this.m.iterator();
  }
  
  public long getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public long getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public long putIfAbsent(long key, long value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TLongLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TLongFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TLongLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(long key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(long key, long amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public long adjustOrPutValue(long key, long adjust_amount, long put_amount) {
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
