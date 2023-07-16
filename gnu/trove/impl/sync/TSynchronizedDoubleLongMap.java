package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.procedure.TDoubleLongProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleLongMap implements TDoubleLongMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TDoubleLongMap m;
  
  final Object mutex;
  
  public TSynchronizedDoubleLongMap(TDoubleLongMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedDoubleLongMap(TDoubleLongMap m, Object mutex) {
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
  
  public boolean containsKey(double key) {
    synchronized (this.mutex) {
      return this.m.containsKey(key);
    } 
  }
  
  public boolean containsValue(long value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public long get(double key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public long put(double key, long value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public long remove(double key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Double, ? extends Long> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TDoubleLongMap map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.m.clear();
    } 
  }
  
  private transient TDoubleSet keySet = null;
  
  private transient TLongCollection values = null;
  
  public TDoubleSet keySet() {
    synchronized (this.mutex) {
      if (this.keySet == null)
        this.keySet = new TSynchronizedDoubleSet(this.m.keySet(), this.mutex); 
      return this.keySet;
    } 
  }
  
  public double[] keys() {
    synchronized (this.mutex) {
      return this.m.keys();
    } 
  }
  
  public double[] keys(double[] array) {
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
  
  public TDoubleLongIterator iterator() {
    return this.m.iterator();
  }
  
  public double getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public long getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public long putIfAbsent(double key, long value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TDoubleLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TLongFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TDoubleLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(double key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(double key, long amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public long adjustOrPutValue(double key, long adjust_amount, long put_amount) {
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
