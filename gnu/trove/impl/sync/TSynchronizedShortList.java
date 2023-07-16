package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.list.TShortList;
import gnu.trove.procedure.TShortProcedure;
import java.util.Random;

public class TSynchronizedShortList extends TSynchronizedShortCollection implements TShortList {
  static final long serialVersionUID = -7754090372962971524L;
  
  final TShortList list;
  
  public TSynchronizedShortList(TShortList list) {
    super((TShortCollection)list);
    this.list = list;
  }
  
  public TSynchronizedShortList(TShortList list, Object mutex) {
    super((TShortCollection)list, mutex);
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
  
  public short get(int index) {
    synchronized (this.mutex) {
      return this.list.get(index);
    } 
  }
  
  public short set(int index, short element) {
    synchronized (this.mutex) {
      return this.list.set(index, element);
    } 
  }
  
  public void set(int offset, short[] values) {
    synchronized (this.mutex) {
      this.list.set(offset, values);
    } 
  }
  
  public void set(int offset, short[] values, int valOffset, int length) {
    synchronized (this.mutex) {
      this.list.set(offset, values, valOffset, length);
    } 
  }
  
  public short replace(int offset, short val) {
    synchronized (this.mutex) {
      return this.list.replace(offset, val);
    } 
  }
  
  public void remove(int offset, int length) {
    synchronized (this.mutex) {
      this.list.remove(offset, length);
    } 
  }
  
  public short removeAt(int offset) {
    synchronized (this.mutex) {
      return this.list.removeAt(offset);
    } 
  }
  
  public void add(short[] vals) {
    synchronized (this.mutex) {
      this.list.add(vals);
    } 
  }
  
  public void add(short[] vals, int offset, int length) {
    synchronized (this.mutex) {
      this.list.add(vals, offset, length);
    } 
  }
  
  public void insert(int offset, short value) {
    synchronized (this.mutex) {
      this.list.insert(offset, value);
    } 
  }
  
  public void insert(int offset, short[] values) {
    synchronized (this.mutex) {
      this.list.insert(offset, values);
    } 
  }
  
  public void insert(int offset, short[] values, int valOffset, int len) {
    synchronized (this.mutex) {
      this.list.insert(offset, values, valOffset, len);
    } 
  }
  
  public int indexOf(short o) {
    synchronized (this.mutex) {
      return this.list.indexOf(o);
    } 
  }
  
  public int lastIndexOf(short o) {
    synchronized (this.mutex) {
      return this.list.lastIndexOf(o);
    } 
  }
  
  public TShortList subList(int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return new TSynchronizedShortList(this.list.subList(fromIndex, toIndex), this.mutex);
    } 
  }
  
  public short[] toArray(int offset, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(offset, len);
    } 
  }
  
  public short[] toArray(short[] dest, int offset, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(dest, offset, len);
    } 
  }
  
  public short[] toArray(short[] dest, int source_pos, int dest_pos, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(dest, source_pos, dest_pos, len);
    } 
  }
  
  public int indexOf(int offset, short value) {
    synchronized (this.mutex) {
      return this.list.indexOf(offset, value);
    } 
  }
  
  public int lastIndexOf(int offset, short value) {
    synchronized (this.mutex) {
      return this.list.lastIndexOf(offset, value);
    } 
  }
  
  public void fill(short val) {
    synchronized (this.mutex) {
      this.list.fill(val);
    } 
  }
  
  public void fill(int fromIndex, int toIndex, short val) {
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
  
  public int binarySearch(short value) {
    synchronized (this.mutex) {
      return this.list.binarySearch(value);
    } 
  }
  
  public int binarySearch(short value, int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return this.list.binarySearch(value, fromIndex, toIndex);
    } 
  }
  
  public TShortList grep(TShortProcedure condition) {
    synchronized (this.mutex) {
      return this.list.grep(condition);
    } 
  }
  
  public TShortList inverseGrep(TShortProcedure condition) {
    synchronized (this.mutex) {
      return this.list.inverseGrep(condition);
    } 
  }
  
  public short max() {
    synchronized (this.mutex) {
      return this.list.max();
    } 
  }
  
  public short min() {
    synchronized (this.mutex) {
      return this.list.min();
    } 
  }
  
  public short sum() {
    synchronized (this.mutex) {
      return this.list.sum();
    } 
  }
  
  public boolean forEachDescending(TShortProcedure procedure) {
    synchronized (this.mutex) {
      return this.list.forEachDescending(procedure);
    } 
  }
  
  public void transformValues(TShortFunction function) {
    synchronized (this.mutex) {
      this.list.transformValues(function);
    } 
  }
  
  private Object readResolve() {
    return (this.list instanceof java.util.RandomAccess) ? new TSynchronizedRandomAccessShortList(this.list) : this;
  }
}
