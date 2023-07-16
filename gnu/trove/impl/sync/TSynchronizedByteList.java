package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;

public class TSynchronizedByteList extends TSynchronizedByteCollection implements TByteList {
  static final long serialVersionUID = -7754090372962971524L;
  
  final TByteList list;
  
  public TSynchronizedByteList(TByteList list) {
    super((TByteCollection)list);
    this.list = list;
  }
  
  public TSynchronizedByteList(TByteList list, Object mutex) {
    super((TByteCollection)list, mutex);
    this.list = list;
  }
  
  public boolean equals(Object o) {
    synchronized (this.mutex) {
      return this.list.equals(o);
    } 
  }
  
  public int hashCode() {
    synchronized (this.mutex) {
      return this.list.hashCode();
    } 
  }
  
  public byte get(int index) {
    synchronized (this.mutex) {
      return this.list.get(index);
    } 
  }
  
  public byte set(int index, byte element) {
    synchronized (this.mutex) {
      return this.list.set(index, element);
    } 
  }
  
  public void set(int offset, byte[] values) {
    synchronized (this.mutex) {
      this.list.set(offset, values);
    } 
  }
  
  public void set(int offset, byte[] values, int valOffset, int length) {
    synchronized (this.mutex) {
      this.list.set(offset, values, valOffset, length);
    } 
  }
  
  public byte replace(int offset, byte val) {
    synchronized (this.mutex) {
      return this.list.replace(offset, val);
    } 
  }
  
  public void remove(int offset, int length) {
    synchronized (this.mutex) {
      this.list.remove(offset, length);
    } 
  }
  
  public byte removeAt(int offset) {
    synchronized (this.mutex) {
      return this.list.removeAt(offset);
    } 
  }
  
  public void add(byte[] vals) {
    synchronized (this.mutex) {
      this.list.add(vals);
    } 
  }
  
  public void add(byte[] vals, int offset, int length) {
    synchronized (this.mutex) {
      this.list.add(vals, offset, length);
    } 
  }
  
  public void insert(int offset, byte value) {
    synchronized (this.mutex) {
      this.list.insert(offset, value);
    } 
  }
  
  public void insert(int offset, byte[] values) {
    synchronized (this.mutex) {
      this.list.insert(offset, values);
    } 
  }
  
  public void insert(int offset, byte[] values, int valOffset, int len) {
    synchronized (this.mutex) {
      this.list.insert(offset, values, valOffset, len);
    } 
  }
  
  public int indexOf(byte o) {
    synchronized (this.mutex) {
      return this.list.indexOf(o);
    } 
  }
  
  public int lastIndexOf(byte o) {
    synchronized (this.mutex) {
      return this.list.lastIndexOf(o);
    } 
  }
  
  public TByteList subList(int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return new TSynchronizedByteList(this.list.subList(fromIndex, toIndex), this.mutex);
    } 
  }
  
  public byte[] toArray(int offset, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(offset, len);
    } 
  }
  
  public byte[] toArray(byte[] dest, int offset, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(dest, offset, len);
    } 
  }
  
  public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(dest, source_pos, dest_pos, len);
    } 
  }
  
  public int indexOf(int offset, byte value) {
    synchronized (this.mutex) {
      return this.list.indexOf(offset, value);
    } 
  }
  
  public int lastIndexOf(int offset, byte value) {
    synchronized (this.mutex) {
      return this.list.lastIndexOf(offset, value);
    } 
  }
  
  public void fill(byte val) {
    synchronized (this.mutex) {
      this.list.fill(val);
    } 
  }
  
  public void fill(int fromIndex, int toIndex, byte val) {
    synchronized (this.mutex) {
      this.list.fill(fromIndex, toIndex, val);
    } 
  }
  
  public void reverse() {
    synchronized (this.mutex) {
      this.list.reverse();
    } 
  }
  
  public void reverse(int from, int to) {
    synchronized (this.mutex) {
      this.list.reverse(from, to);
    } 
  }
  
  public void shuffle(Random rand) {
    synchronized (this.mutex) {
      this.list.shuffle(rand);
    } 
  }
  
  public void sort() {
    synchronized (this.mutex) {
      this.list.sort();
    } 
  }
  
  public void sort(int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      this.list.sort(fromIndex, toIndex);
    } 
  }
  
  public int binarySearch(byte value) {
    synchronized (this.mutex) {
      return this.list.binarySearch(value);
    } 
  }
  
  public int binarySearch(byte value, int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return this.list.binarySearch(value, fromIndex, toIndex);
    } 
  }
  
  public TByteList grep(TByteProcedure condition) {
    synchronized (this.mutex) {
      return this.list.grep(condition);
    } 
  }
  
  public TByteList inverseGrep(TByteProcedure condition) {
    synchronized (this.mutex) {
      return this.list.inverseGrep(condition);
    } 
  }
  
  public byte max() {
    synchronized (this.mutex) {
      return this.list.max();
    } 
  }
  
  public byte min() {
    synchronized (this.mutex) {
      return this.list.min();
    } 
  }
  
  public byte sum() {
    synchronized (this.mutex) {
      return this.list.sum();
    } 
  }
  
  public boolean forEachDescending(TByteProcedure procedure) {
    synchronized (this.mutex) {
      return this.list.forEachDescending(procedure);
    } 
  }
  
  public void transformValues(TByteFunction function) {
    synchronized (this.mutex) {
      this.list.transformValues(function);
    } 
  }
  
  private Object readResolve() {
    return (this.list instanceof java.util.RandomAccess) ? new TSynchronizedRandomAccessByteList(this.list) : this;
  }
}
