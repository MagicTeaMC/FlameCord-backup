package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.procedure.TDoubleDoubleProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleDoubleMap implements TDoubleDoubleMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TDoubleDoubleMap m;
  
  final Object mutex;
  
  public TSynchronizedDoubleDoubleMap(TDoubleDoubleMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedDoubleDoubleMap(TDoubleDoubleMap m, Object mutex) {
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
  
  public boolean containsValue(double value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public double get(double key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public double put(double key, double value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public double remove(double key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Double, ? extends Double> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TDoubleDoubleMap map) {
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
  
  private transient TDoubleCollection values = null;
  
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
  
  public TDoubleDoubleIterator iterator() {
    return this.m.iterator();
  }
  
  public double getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public double getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public double putIfAbsent(double key, double value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TDoubleDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TDoubleFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TDoubleDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(double key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(double key, double amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public double adjustOrPutValue(double key, double adjust_amount, double put_amount) {
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
