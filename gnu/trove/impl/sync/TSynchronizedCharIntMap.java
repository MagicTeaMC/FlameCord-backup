package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.map.TCharIntMap;
import gnu.trove.procedure.TCharIntProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TCharSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedCharIntMap implements TCharIntMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TCharIntMap m;
  
  final Object mutex;
  
  public TSynchronizedCharIntMap(TCharIntMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedCharIntMap(TCharIntMap m, Object mutex) {
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
  
  public boolean containsKey(char key) {
    synchronized (this.mutex) {
      return this.m.containsKey(key);
    } 
  }
  
  public boolean containsValue(int value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public int get(char key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public int put(char key, int value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public int remove(char key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Character, ? extends Integer> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TCharIntMap map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.m.clear();
    } 
  }
  
  private transient TCharSet keySet = null;
  
  private transient TIntCollection values = null;
  
  public TCharSet keySet() {
    synchronized (this.mutex) {
      if (this.keySet == null)
        this.keySet = new TSynchronizedCharSet(this.m.keySet(), this.mutex); 
      return this.keySet;
    } 
  }
  
  public char[] keys() {
    synchronized (this.mutex) {
      return this.m.keys();
    } 
  }
  
  public char[] keys(char[] array) {
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
  
  public TCharIntIterator iterator() {
    return this.m.iterator();
  }
  
  public char getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public int getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public int putIfAbsent(char key, int value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TCharIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TIntFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TCharIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(char key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(char key, int amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public int adjustOrPutValue(char key, int adjust_amount, int put_amount) {
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
