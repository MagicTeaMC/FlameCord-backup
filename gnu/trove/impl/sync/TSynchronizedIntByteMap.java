package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.map.TIntByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedIntByteMap implements TIntByteMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TIntByteMap m;
  
  final Object mutex;
  
  public TSynchronizedIntByteMap(TIntByteMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedIntByteMap(TIntByteMap m, Object mutex) {
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
  
  public boolean containsKey(int key) {
    synchronized (this.mutex) {
      return this.m.containsKey(key);
    } 
  }
  
  public boolean containsValue(byte value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public byte get(int key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public byte put(int key, byte value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public byte remove(int key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Integer, ? extends Byte> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TIntByteMap map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.m.clear();
    } 
  }
  
  private transient TIntSet keySet = null;
  
  private transient TByteCollection values = null;
  
  public TIntSet keySet() {
    synchronized (this.mutex) {
      if (this.keySet == null)
        this.keySet = new TSynchronizedIntSet(this.m.keySet(), this.mutex); 
      return this.keySet;
    } 
  }
  
  public int[] keys() {
    synchronized (this.mutex) {
      return this.m.keys();
    } 
  }
  
  public int[] keys(int[] array) {
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
  
  public TIntByteIterator iterator() {
    return this.m.iterator();
  }
  
  public int getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public byte getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public byte putIfAbsent(int key, byte value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TByteProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TIntByteProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TByteFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TIntByteProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(int key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(int key, byte amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public byte adjustOrPutValue(int key, byte adjust_amount, byte put_amount) {
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
