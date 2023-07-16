package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
import java.util.Random;

public class TSynchronizedIntList extends TSynchronizedIntCollection implements TIntList {
  static final long serialVersionUID = -7754090372962971524L;
  
  final TIntList list;
  
  public TSynchronizedIntList(TIntList list) {
    super((TIntCollection)list);
    this.list = list;
  }
  
  public TSynchronizedIntList(TIntList list, Object mutex) {
    super((TIntCollection)list, mutex);
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
  
  public int get(int index) {
    synchronized (this.mutex) {
      return this.list.get(index);
    } 
  }
  
  public int set(int index, int element) {
    synchronized (this.mutex) {
      return this.list.set(index, element);
    } 
  }
  
  public void set(int offset, int[] values) {
    synchronized (this.mutex) {
      this.list.set(offset, values);
    } 
  }
  
  public void set(int offset, int[] values, int valOffset, int length) {
    synchronized (this.mutex) {
      this.list.set(offset, values, valOffset, length);
    } 
  }
  
  public int replace(int offset, int val) {
    synchronized (this.mutex) {
      return this.list.replace(offset, val);
    } 
  }
  
  public void remove(int offset, int length) {
    synchronized (this.mutex) {
      this.list.remove(offset, length);
    } 
  }
  
  public int removeAt(int offset) {
    synchronized (this.mutex) {
      return this.list.removeAt(offset);
    } 
  }
  
  public void add(int[] vals) {
    synchronized (this.mutex) {
      this.list.add(vals);
    } 
  }
  
  public void add(int[] vals, int offset, int length) {
    synchronized (this.mutex) {
      this.list.add(vals, offset, length);
    } 
  }
  
  public void insert(int offset, int value) {
    synchronized (this.mutex) {
      this.list.insert(offset, value);
    } 
  }
  
  public void insert(int offset, int[] values) {
    synchronized (this.mutex) {
      this.list.insert(offset, values);
    } 
  }
  
  public void insert(int offset, int[] values, int valOffset, int len) {
    synchronized (this.mutex) {
      this.list.insert(offset, values, valOffset, len);
    } 
  }
  
  public int indexOf(int o) {
    synchronized (this.mutex) {
      return this.list.indexOf(o);
    } 
  }
  
  public int lastIndexOf(int o) {
    synchronized (this.mutex) {
      return this.list.lastIndexOf(o);
    } 
  }
  
  public TIntList subList(int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return new TSynchronizedIntList(this.list.subList(fromIndex, toIndex), this.mutex);
    } 
  }
  
  public int[] toArray(int offset, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(offset, len);
    } 
  }
  
  public int[] toArray(int[] dest, int offset, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(dest, offset, len);
    } 
  }
  
  public int[] toArray(int[] dest, int source_pos, int dest_pos, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(dest, source_pos, dest_pos, len);
    } 
  }
  
  public int indexOf(int offset, int value) {
    synchronized (this.mutex) {
      return this.list.indexOf(offset, value);
    } 
  }
  
  public int lastIndexOf(int offset, int value) {
    synchronized (this.mutex) {
      return this.list.lastIndexOf(offset, value);
    } 
  }
  
  public void fill(int val) {
    synchronized (this.mutex) {
      this.list.fill(val);
    } 
  }
  
  public void fill(int fromIndex, int toIndex, int val) {
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
  
  public int binarySearch(int value) {
    synchronized (this.mutex) {
      return this.list.binarySearch(value);
    } 
  }
  
  public int binarySearch(int value, int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return this.list.binarySearch(value, fromIndex, toIndex);
    } 
  }
  
  public TIntList grep(TIntProcedure condition) {
    synchronized (this.mutex) {
      return this.list.grep(condition);
    } 
  }
  
  public TIntList inverseGrep(TIntProcedure condition) {
    synchronized (this.mutex) {
      return this.list.inverseGrep(condition);
    } 
  }
  
  public int max() {
    synchronized (this.mutex) {
      return this.list.max();
    } 
  }
  
  public int min() {
    synchronized (this.mutex) {
      return this.list.min();
    } 
  }
  
  public int sum() {
    synchronized (this.mutex) {
      return this.list.sum();
    } 
  }
  
  public boolean forEachDescending(TIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.list.forEachDescending(procedure);
    } 
  }
  
  public void transformValues(TIntFunction function) {
    synchronized (this.mutex) {
      this.list.transformValues(function);
    } 
  }
  
  private Object readResolve() {
    return (this.list instanceof java.util.RandomAccess) ? new TSynchronizedRandomAccessIntList(this.list) : this;
  }
}
