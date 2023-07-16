package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.procedure.TDoubleIntProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleIntMap implements TDoubleIntMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TDoubleIntMap m;
  
  final Object mutex;
  
  public TSynchronizedDoubleIntMap(TDoubleIntMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedDoubleIntMap(TDoubleIntMap m, Object mutex) {
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
  
  public boolean containsValue(int value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public int get(double key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public int put(double key, int value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public int remove(double key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Double, ? extends Integer> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TDoubleIntMap map) {
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
  
  private transient TIntCollection values = null;
  
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
  
  public TDoubleIntIterator iterator() {
    return this.m.iterator();
  }
  
  public double getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public int getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public int putIfAbsent(double key, int value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TDoubleIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TIntFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TDoubleIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(double key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(double key, int amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public int adjustOrPutValue(double key, int adjust_amount, int put_amount) {
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
