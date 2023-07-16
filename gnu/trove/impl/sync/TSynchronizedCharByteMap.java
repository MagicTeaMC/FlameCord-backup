package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TCharByteIterator;
import gnu.trove.map.TCharByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedCharByteMap implements TCharByteMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TCharByteMap m;
  
  final Object mutex;
  
  public TSynchronizedCharByteMap(TCharByteMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedCharByteMap(TCharByteMap m, Object mutex) {
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
  
  public boolean containsValue(byte value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public byte get(char key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public byte put(char key, byte value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public byte remove(char key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Character, ? extends Byte> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TCharByteMap map) {
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
  
  private transient TByteCollection values = null;
  
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
  
  public TByteCollection valueCollection() {
    synchronized (this.mutex) {
      if (this.values == null)
        this.values = new TSynchronizedByteCollection(this.m.valueCollection(), this.mutex); 
      return this.values;
    } 
  }
  
  public byte[] values() {
    synchronized (this.mutex) {
      return this.m.values();
    } 
  }
  
  public byte[] values(byte[] array) {
    synchronized (this.mutex) {
      return this.m.values(array);
    } 
  }
  
  public TCharByteIterator iterator() {
    return this.m.iterator();
  }
  
  public char getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public byte getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public byte putIfAbsent(char key, byte value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TByteProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TCharByteProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TByteFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TCharByteProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(char key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(char key, byte amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public byte adjustOrPutValue(char key, byte adjust_amount, byte put_amount) {
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
