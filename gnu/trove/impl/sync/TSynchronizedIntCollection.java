package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedIntCollection implements TIntCollection, Serializable {
  private static final long serialVersionUID = 3053995032091335093L;
  
  final TIntCollection c;
  
  final Object mutex;
  
  public TSynchronizedIntCollection(TIntCollection c) {
    if (c == null)
      throw new NullPointerException(); 
    this.c = c;
    this.mutex = this;
  }
  
  public TSynchronizedIntCollection(TIntCollection c, Object mutex) {
    this.c = c;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (this.mutex) {
      return this.c.size();
    } 
  }
  
  public boolean isEmpty() {
    synchronized (this.mutex) {
      return this.c.isEmpty();
    } 
  }
  
  public boolean contains(int o) {
    synchronized (this.mutex) {
      return this.c.contains(o);
    } 
  }
  
  public int[] toArray() {
    synchronized (this.mutex) {
      return this.c.toArray();
    } 
  }
  
  public int[] toArray(int[] a) {
    synchronized (this.mutex) {
      return this.c.toArray(a);
    } 
  }
  
  public TIntIterator iterator() {
    return this.c.iterator();
  }
  
  public boolean add(int e) {
    synchronized (this.mutex) {
      return this.c.add(e);
    } 
  }
  
  public boolean remove(int o) {
    synchronized (this.mutex) {
      return this.c.remove(o);
    } 
  }
  
  public boolean containsAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.containsAll(coll);
    } 
  }
  
  public boolean containsAll(TIntCollection coll) {
    synchronized (this.mutex) {
      return this.c.containsAll(coll);
    } 
  }
  
  public boolean containsAll(int[] array) {
    synchronized (this.mutex) {
      return this.c.containsAll(array);
    } 
  }
  
  public boolean addAll(Collection<? extends Integer> coll) {
    synchronized (this.mutex) {
      return this.c.addAll(coll);
    } 
  }
  
  public boolean addAll(TIntCollection coll) {
    synchronized (this.mutex) {
      return this.c.addAll(coll);
    } 
  }
  
  public boolean addAll(int[] array) {
    synchronized (this.mutex) {
      return this.c.addAll(array);
    } 
  }
  
  public boolean removeAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.removeAll(coll);
    } 
  }
  
  public boolean removeAll(TIntCollection coll) {
    synchronized (this.mutex) {
      return this.c.removeAll(coll);
    } 
  }
  
  public boolean removeAll(int[] array) {
    synchronized (this.mutex) {
      return this.c.removeAll(array);
    } 
  }
  
  public boolean retainAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.retainAll(coll);
    } 
  }
  
  public boolean retainAll(TIntCollection coll) {
    synchronized (this.mutex) {
      return this.c.retainAll(coll);
    } 
  }
  
  public boolean retainAll(int[] array) {
    synchronized (this.mutex) {
      return this.c.retainAll(array);
    } 
  }
  
  public int getNoEntryValue() {
    return this.c.getNoEntryValue();
  }
  
  public boolean forEach(TIntProcedure procedure) {
    synchronized (this.mutex) {
      return this.c.forEach(procedure);
    } 
  }
  
  public void clear() {
    synchronized (this.mutex) {
      this.c.clear();
    } 
  }
  
  public String toString() {
    synchronized (this.mutex) {
      return this.c.toString();
    } 
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    synchronized (this.mutex) {
      s.defaultWriteObject();
    } 
  }
}
