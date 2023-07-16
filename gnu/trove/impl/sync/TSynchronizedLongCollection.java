package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedLongCollection implements TLongCollection, Serializable {
  private static final long serialVersionUID = 3053995032091335093L;
  
  final TLongCollection c;
  
  final Object mutex;
  
  public TSynchronizedLongCollection(TLongCollection c) {
    if (c == null)
      throw new NullPointerException(); 
    this.c = c;
    this.mutex = this;
  }
  
  public TSynchronizedLongCollection(TLongCollection c, Object mutex) {
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
  
  public boolean contains(long o) {
    synchronized (this.mutex) {
      return this.c.contains(o);
    } 
  }
  
  public long[] toArray() {
    synchronized (this.mutex) {
      return this.c.toArray();
    } 
  }
  
  public long[] toArray(long[] a) {
    synchronized (this.mutex) {
      return this.c.toArray(a);
    } 
  }
  
  public TLongIterator iterator() {
    return this.c.iterator();
  }
  
  public boolean add(long e) {
    synchronized (this.mutex) {
      return this.c.add(e);
    } 
  }
  
  public boolean remove(long o) {
    synchronized (this.mutex) {
      return this.c.remove(o);
    } 
  }
  
  public boolean containsAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.containsAll(coll);
    } 
  }
  
  public boolean containsAll(TLongCollection coll) {
    synchronized (this.mutex) {
      return this.c.containsAll(coll);
    } 
  }
  
  public boolean containsAll(long[] array) {
    synchronized (this.mutex) {
      return this.c.containsAll(array);
    } 
  }
  
  public boolean addAll(Collection<? extends Long> coll) {
    synchronized (this.mutex) {
      return this.c.addAll(coll);
    } 
  }
  
  public boolean addAll(TLongCollection coll) {
    synchronized (this.mutex) {
      return this.c.addAll(coll);
    } 
  }
  
  public boolean addAll(long[] array) {
    synchronized (this.mutex) {
      return this.c.addAll(array);
    } 
  }
  
  public boolean removeAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.removeAll(coll);
    } 
  }
  
  public boolean removeAll(TLongCollection coll) {
    synchronized (this.mutex) {
      return this.c.removeAll(coll);
    } 
  }
  
  public boolean removeAll(long[] array) {
    synchronized (this.mutex) {
      return this.c.removeAll(array);
    } 
  }
  
  public boolean retainAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.retainAll(coll);
    } 
  }
  
  public boolean retainAll(TLongCollection coll) {
    synchronized (this.mutex) {
      return this.c.retainAll(coll);
    } 
  }
  
  public boolean retainAll(long[] array) {
    synchronized (this.mutex) {
      return this.c.retainAll(array);
    } 
  }
  
  public long getNoEntryValue() {
    return this.c.getNoEntryValue();
  }
  
  public boolean forEach(TLongProcedure procedure) {
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
