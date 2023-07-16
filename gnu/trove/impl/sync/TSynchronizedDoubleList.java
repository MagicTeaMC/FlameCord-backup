package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;

public class TSynchronizedDoubleList extends TSynchronizedDoubleCollection implements TDoubleList {
  static final long serialVersionUID = -7754090372962971524L;
  
  final TDoubleList list;
  
  public TSynchronizedDoubleList(TDoubleList list) {
    super((TDoubleCollection)list);
    this.list = list;
  }
  
  public TSynchronizedDoubleList(TDoubleList list, Object mutex) {
    super((TDoubleCollection)list, mutex);
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
  
  public double get(int index) {
    synchronized (this.mutex) {
      return this.list.get(index);
    } 
  }
  
  public double set(int index, double element) {
    synchronized (this.mutex) {
      return this.list.set(index, element);
    } 
  }
  
  public void set(int offset, double[] values) {
    synchronized (this.mutex) {
      this.list.set(offset, values);
    } 
  }
  
  public void set(int offset, double[] values, int valOffset, int length) {
    synchronized (this.mutex) {
      this.list.set(offset, values, valOffset, length);
    } 
  }
  
  public double replace(int offset, double val) {
    synchronized (this.mutex) {
      return this.list.replace(offset, val);
    } 
  }
  
  public void remove(int offset, int length) {
    synchronized (this.mutex) {
      this.list.remove(offset, length);
    } 
  }
  
  public double removeAt(int offset) {
    synchronized (this.mutex) {
      return this.list.removeAt(offset);
    } 
  }
  
  public void add(double[] vals) {
    synchronized (this.mutex) {
      this.list.add(vals);
    } 
  }
  
  public void add(double[] vals, int offset, int length) {
    synchronized (this.mutex) {
      this.list.add(vals, offset, length);
    } 
  }
  
  public void insert(int offset, double value) {
    synchronized (this.mutex) {
      this.list.insert(offset, value);
    } 
  }
  
  public void insert(int offset, double[] values) {
    synchronized (this.mutex) {
      this.list.insert(offset, values);
    } 
  }
  
  public void insert(int offset, double[] values, int valOffset, int len) {
    synchronized (this.mutex) {
      this.list.insert(offset, values, valOffset, len);
    } 
  }
  
  public int indexOf(double o) {
    synchronized (this.mutex) {
      return this.list.indexOf(o);
    } 
  }
  
  public int lastIndexOf(double o) {
    synchronized (this.mutex) {
      return this.list.lastIndexOf(o);
    } 
  }
  
  public TDoubleList subList(int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return new TSynchronizedDoubleList(this.list.subList(fromIndex, toIndex), this.mutex);
    } 
  }
  
  public double[] toArray(int offset, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(offset, len);
    } 
  }
  
  public double[] toArray(double[] dest, int offset, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(dest, offset, len);
    } 
  }
  
  public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) {
    synchronized (this.mutex) {
      return this.list.toArray(dest, source_pos, dest_pos, len);
    } 
  }
  
  public int indexOf(int offset, double value) {
    synchronized (this.mutex) {
      return this.list.indexOf(offset, value);
    } 
  }
  
  public int lastIndexOf(int offset, double value) {
    synchronized (this.mutex) {
      return this.list.lastIndexOf(offset, value);
    } 
  }
  
  public void fill(double val) {
    synchronized (this.mutex) {
      this.list.fill(val);
    } 
  }
  
  public void fill(int fromIndex, int toIndex, double val) {
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
  
  public int binarySearch(double value) {
    synchronized (this.mutex) {
      return this.list.binarySearch(value);
    } 
  }
  
  public int binarySearch(double value, int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return this.list.binarySearch(value, fromIndex, toIndex);
    } 
  }
  
  public TDoubleList grep(TDoubleProcedure condition) {
    synchronized (this.mutex) {
      return this.list.grep(condition);
    } 
  }
  
  public TDoubleList inverseGrep(TDoubleProcedure condition) {
    synchronized (this.mutex) {
      return this.list.inverseGrep(condition);
    } 
  }
  
  public double max() {
    synchronized (this.mutex) {
      return this.list.max();
    } 
  }
  
  public double min() {
    synchronized (this.mutex) {
      return this.list.min();
    } 
  }
  
  public double sum() {
    synchronized (this.mutex) {
      return this.list.sum();
    } 
  }
  
  public boolean forEachDescending(TDoubleProcedure procedure) {
    synchronized (this.mutex) {
      return this.list.forEachDescending(procedure);
    } 
  }
  
  public void transformValues(TDoubleFunction function) {
    synchronized (this.mutex) {
      this.list.transformValues(function);
    } 
  }
  
  private Object readResolve() {
    return (this.list instanceof java.util.RandomAccess) ? new TSynchronizedRandomAccessDoubleList(this.list) : this;
  }
}
