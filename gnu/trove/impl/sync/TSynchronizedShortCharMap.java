package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.map.TShortCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TShortCharProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedShortCharMap implements TShortCharMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TShortCharMap m;
  
  final Object mutex;
  
  public TSynchronizedShortCharMap(TShortCharMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedShortCharMap(TShortCharMap m, Object mutex) {
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
  
  public boolean containsKey(short key) {
    synchronized (this.mutex) {
      return this.m.containsKey(key);
    } 
  }
  
  public boolean containsValue(char value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public char get(short key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public char put(short key, char value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public char remove(short key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Short, ? extends Character> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TShortCharMap map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.m.clear();
    } 
  }
  
  private transient TShortSet keySet = null;
  
  private transient TCharCollection values = null;
  
  public TShortSet keySet() {
    synchronized (this.mutex) {
      if (this.keySet == null)
        this.keySet = new TSynchronizedShortSet(this.m.keySet(), this.mutex); 
      return this.keySet;
    } 
  }
  
  public short[] keys() {
    synchronized (this.mutex) {
      return this.m.keys();
    } 
  }
  
  public short[] keys(short[] array) {
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
  
  public TShortCharIterator iterator() {
    return this.m.iterator();
  }
  
  public short getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public char getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public char putIfAbsent(short key, char value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TShortProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TShortCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TCharFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TShortCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(short key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(short key, char amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public char adjustOrPutValue(short key, char adjust_amount, char put_amount) {
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
