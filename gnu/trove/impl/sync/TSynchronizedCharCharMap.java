package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.map.TCharCharMap;
import gnu.trove.procedure.TCharCharProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedCharCharMap implements TCharCharMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TCharCharMap m;
  
  final Object mutex;
  
  public TSynchronizedCharCharMap(TCharCharMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedCharCharMap(TCharCharMap m, Object mutex) {
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
  
  public boolean containsValue(char value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public char get(char key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public char put(char key, char value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public char remove(char key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Character, ? extends Character> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TCharCharMap map) {
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
  
  private transient TCharCollection values = null;
  
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
  
  public TCharCharIterator iterator() {
    return this.m.iterator();
  }
  
  public char getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public char getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public char putIfAbsent(char key, char value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TCharCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TCharFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TCharCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(char key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(char key, char amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public char adjustOrPutValue(char key, char adjust_amount, char put_amount) {
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
