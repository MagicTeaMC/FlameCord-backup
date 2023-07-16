package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.map.TByteShortMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TByteShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TByteSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedByteShortMap implements TByteShortMap, Serializable {
  private static final long serialVersionUID = 1978198479659022715L;
  
  private final TByteShortMap m;
  
  final Object mutex;
  
  public TSynchronizedByteShortMap(TByteShortMap m) {
    if (m == null)
      throw new NullPointerException(); 
    this.m = m;
    this.mutex = this;
  }
  
  public TSynchronizedByteShortMap(TByteShortMap m, Object mutex) {
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
  
  public boolean containsKey(byte key) {
    synchronized (this.mutex) {
      return this.m.containsKey(key);
    } 
  }
  
  public boolean containsValue(short value) {
    synchronized (this.mutex) {
      return this.m.containsValue(value);
    } 
  }
  
  public short get(byte key) {
    synchronized (this.mutex) {
      return this.m.get(key);
    } 
  }
  
  public short put(byte key, short value) {
    synchronized (this.mutex) {
      return this.m.put(key, value);
    } 
  }
  
  public short remove(byte key) {
    synchronized (this.mutex) {
      return this.m.remove(key);
    } 
  }
  
  public void putAll(Map<? extends Byte, ? extends Short> map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void putAll(TByteShortMap map) {
    synchronized (this.mutex) {
      this.m.putAll(map);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.m.clear();
    } 
  }
  
  private transient TByteSet keySet = null;
  
  private transient TShortCollection values = null;
  
  public TByteSet keySet() {
    synchronized (this.mutex) {
      if (this.keySet == null)
        this.keySet = new TSynchronizedByteSet(this.m.keySet(), this.mutex); 
      return this.keySet;
    } 
  }
  
  public byte[] keys() {
    synchronized (this.mutex) {
      return this.m.keys();
    } 
  }
  
  public byte[] keys(byte[] array) {
    synchronized (this.mutex) {
      return this.m.keys(array);
    } 
  }
  
  public TShortCollection valueCollection() {
    synchronized (this.mutex) {
      if (this.values == null)
        this.values = new TSynchronizedShortCollection(this.m.valueCollection(), this.mutex); 
      return this.values;
    } 
  }
  
  public short[] values() {
    synchronized (this.mutex) {
      return this.m.values();
    } 
  }
  
  public short[] values(short[] array) {
    synchronized (this.mutex) {
      return this.m.values(array);
    } 
  }
  
  public TByteShortIterator iterator() {
    return this.m.iterator();
  }
  
  public byte getNoEntryKey() {
    return this.m.getNoEntryKey();
  }
  
  public short getNoEntryValue() {
    return this.m.getNoEntryValue();
  }
  
  public short putIfAbsent(byte key, short value) {
    synchronized (this.mutex) {
      return this.m.putIfAbsent(key, value);
    } 
  }
  
  public boolean forEachKey(TByteProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachKey(procedure);
    } 
  }
  
  public boolean forEachValue(TShortProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachValue(procedure);
    } 
  }
  
  public boolean forEachEntry(TByteShortProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.forEachEntry(procedure);
    } 
  }
  
  public void transformValues(TShortFunction function) {
    synchronized (this.mutex) {
      this.m.transformValues(function);
    } 
  }
  
  public boolean retainEntries(TByteShortProcedure procedure) {
    synchronized (this.mutex) {
      return this.m.retainEntries(procedure);
    } 
  }
  
  public boolean increment(byte key) {
    synchronized (this.mutex) {
      return this.m.increment(key);
    } 
  }
  
  public boolean adjustValue(byte key, short amount) {
    synchronized (this.mutex) {
      return this.m.adjustValue(key, amount);
    } 
  }
  
  public short adjustOrPutValue(byte key, short adjust_amount, short put_amount) {
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
