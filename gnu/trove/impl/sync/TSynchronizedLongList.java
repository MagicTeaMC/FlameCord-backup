package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;
import java.util.Random;

public class TSynchronizedLongList extends TSynchronizedLongCollection implements TLongList {
  static final long serialVersionUID = -7754090372962971524L;
  
  final TLongList list;
  
  public TSynchronizedLongList(TLongList list) {
    super((TLongCollection)list);
    this.list = list;
  }
  
  public TSynchronizedLongList(TLongList list, Object mutex) {
    super((TLongCollection)list, mutex);
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
  
  public long get(int index) {
    synchronized (this.mutex) {
      return this.list.get(index);
    } 
  }
  
  public long set(int index, long element) {
    synchronized (this.mutex) {
      return this.list.set(index, element);
    } 
  }
  
  public void set(int offset, long[] values) {
    synchronized (this.mutex) {
      this.list.set(offset, values);
    } 
  }
  
  public void set(int offset, long[] values, int valOffset, int length) {
    synchronized (this.mutex) {
      this.list.set(offset, values, valOffset, length);
    } 
  }
  
  public long replace(int offset, long val) {
    synchronized (this.mutex) {
      return this.list.replace(offset, val);
    } 
  }
  
  public void remove(int offset, int length) {
    synchronized (this.mutex) {
      this.list.remove(offset, length);
    } 
  }
  
  public long removeAt(int offset) {
    synchronized (this.mutex) {
      return this.list.removeAt(offset);
    } 
  }
  
  public void add(long[] vals) {
    synchronized (this.mutex) {
      this.list.add(vals);
    } 
  }
  
  public void add(long[] vals, int offset, int length) {
    synchronized (this.mutex) {
      this.list.add(vals, offset, length);
    } 
  }
  
  public void insert(int offset, long value) {
    synchronized (this.mutex) {
      this.list.insert(offset, value);
    } 
  }
  
  public void insert(int offset, long[] values) {
    synchronized (this.mutex) {
      this.list.insert(offset, values);
    } 
  }
  
  public void insert(int offset, long[] values, int valOffset, int len) {
    synchronized (this.mutex) {
      this.list.insert(offset, values, valOffset, len);
    } 
  }
  
  public int indexOf(long o) {
    synchronized (this.mutex) {
      return this.list.indexOf(o);
    } 
  }
  
  public int lastIndexOf(long o) {
    synchronized (this.mutex) {
      return this.list.lastIndexOf(o);
    } 
  }
  
  public TLongList subList(int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return new TSynchronizedLongList(this.list.subList(fromIndex, toIndex), this.mutex);
    } 
  }
  
  public long[] toArray(int offset, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(offset, len);
    } 
  }
  
  public long[] toArray(long[] dest, int offset, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(dest, offset, len);
    } 
  }
  
  public long[] toArray(long[] dest, int source_pos, int dest_pos, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(dest, source_pos, dest_pos, len);
    } 
  }
  
  public int indexOf(int offset, long value) {
    synchronized (this.mutex) {
      return this.list.indexOf(offset, value);
    } 
  }
  
  public int lastIndexOf(int offset, long value) {
    synchronized (this.mutex) {
      return this.list.lastIndexOf(offset, value);
    } 
  }
  
  public void fill(long val) {
    synchronized (this.mutex) {
      this.list.fill(val);
    } 
  }
  
  public void fill(int fromIndex, int toIndex, long val) {
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
  
  public int binarySearch(long value) {
    synchronized (this.mutex) {
      return this.list.binarySearch(value);
    } 
  }
  
  public int binarySearch(long value, int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return this.list.binarySearch(value, fromIndex, toIndex);
    } 
  }
  
  public TLongList grep(TLongProcedure condition) {
    synchronized (this.mutex) {
      return this.list.grep(condition);
    } 
  }
  
  public TLongList inverseGrep(TLongProcedure condition) {
    synchronized (this.mutex) {
      return this.list.inverseGrep(condition);
    } 
  }
  
  public long max() {
    synchronized (this.mutex) {
      return this.list.max();
    } 
  }
  
  public long min() {
    synchronized (this.mutex) {
      return this.list.min();
    } 
  }
  
  public long sum() {
    synchronized (this.mutex) {
      return this.list.sum();
    } 
  }
  
  public boolean forEachDescending(TLongProcedure procedure) {
    synchronized (this.mutex) {
      return this.list.forEachDescending(procedure);
    } 
  }
  
  public void transformValues(TLongFunction function) {
    synchronized (this.mutex) {
      this.list.transformValues(function);
    } 
  }
  
  private Object readResolve() {
    return (this.list instanceof java.util.RandomAccess) ? new TSynchronizedRandomAccessLongList(this.list) : this;
  }
}
