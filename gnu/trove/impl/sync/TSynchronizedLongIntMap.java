package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedLongIntMap implements TLongIntMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TLongIntMap m;
  
  final Object mutex;
  
  public TSynchronizedLongIntMap(TLongIntMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedLongIntMap(TLongIntMap m, Object mutex) {
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
  
  public boolean containsValue(int value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public int get(long key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public int put(long key, int value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public int remove(long key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Long, ? extends Integer> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TLongIntMap map) {
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
  
  private transient TIntCollection values = null;
  
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
  
  public TLongIntIterator iterator() {
    return this.m.iterator();
  }
  
  public long getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public int getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public int putIfAbsent(long key, int value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TLongIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TIntFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TLongIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(long key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(long key, int amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public int adjustOrPutValue(long key, int adjust_amount, int put_amount) {
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
