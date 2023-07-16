package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedLongDoubleMap implements TLongDoubleMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TLongDoubleMap m;
  
  final Object mutex;
  
  public TSynchronizedLongDoubleMap(TLongDoubleMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedLongDoubleMap(TLongDoubleMap m, Object mutex) {
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
  
  public boolean containsValue(double value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public double get(long key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public double put(long key, double value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public double remove(long key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Long, ? extends Double> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TLongDoubleMap map) {
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
  
  private transient TDoubleCollection values = null;
  
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
  
  public TLongDoubleIterator iterator() {
    return this.m.iterator();
  }
  
  public long getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public double getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public double putIfAbsent(long key, double value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TLongDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TDoubleFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TLongDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(long key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(long key, double amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public double adjustOrPutValue(long key, double adjust_amount, double put_amount) {
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
