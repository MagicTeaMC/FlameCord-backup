package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedFloatCharMap implements TFloatCharMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TFloatCharMap m;
  
  final Object mutex;
  
  public TSynchronizedFloatCharMap(TFloatCharMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedFloatCharMap(TFloatCharMap m, Object mutex) {
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
  
  public boolean containsKey(float key) {
    synchronized (this.mutex) {
      return this.m.containsKey(key);
    } 
  }
  
  public boolean containsValue(char value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public char get(float key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public char put(float key, char value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public char remove(float key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Float, ? extends Character> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TFloatCharMap map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.m.clear();
    } 
  }
  
  private transient TFloatSet keySet = null;
  
  private transient TCharCollection values = null;
  
  public TFloatSet keySet() {
    synchronized (this.mutex) {
      if (this.keySet == null)
        this.keySet = new TSynchronizedFloatSet(this.m.keySet(), this.mutex); 
      return this.keySet;
    } 
  }
  
  public float[] keys() {
    synchronized (this.mutex) {
      return this.m.keys();
    } 
  }
  
  public float[] keys(float[] array) {
    synchronized (this.mutex) {
      return this.m.keys(array);
    } 
  }
  
  public TCharCollection valueCollection() {
    synchronized (this.mutex) {
      if (this.values == null)
        this.values = new TSynchronizedCharCollection(this.m.valueCollection(), this.mutex); 
      return this.values;
    } 
  }
  
  public char[] values() {
    synchronized (this.mutex) {
      return this.m.values();
    } 
  }
  
  public char[] values(char[] array) {
    synchronized (this.mutex) {
      return this.m.values(array);
    } 
  }
  
  public TFloatCharIterator iterator() {
    return this.m.iterator();
  }
  
  public float getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public char getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public char putIfAbsent(float key, char value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TFloatProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TFloatCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TCharFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TFloatCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(float key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(float key, char amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public char adjustOrPutValue(float key, char adjust_amount, char put_amount) {
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
