package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedByteCollection implements TByteCollection, Serializable {
  private static final long serialVersionUID = 3053995032091335093L;
  
  final TByteCollection c;
  
  final Object mutex;
  
  public TSynchronizedByteCollection(TByteCollection c) {
    if (c == null)
      throw new NullPointerException(); 
    this.c = c;
    this.mutex = this;
  }
  
  public TSynchronizedByteCollection(TByteCollection c, Object mutex) {
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
  
  public boolean contains(byte o) {
    synchronized (this.mutex) {
      return this.c.contains(o);
    } 
  }
  
  public byte[] toArray() {
    synchronized (this.mutex) {
      return this.c.toArray();
    } 
  }
  
  public byte[] toArray(byte[] a) {
    synchronized (this.mutex) {
      return this.c.toArray(a);
    } 
  }
  
  public TByteIterator iterator() {
    return this.c.iterator();
  }
  
  public boolean add(byte e) {
    synchronized (this.mutex) {
      return this.c.add(e);
    } 
  }
  
  public boolean remove(byte o) {
    synchronized (this.mutex) {
      return this.c.remove(o);
    } 
  }
  
  public boolean containsAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.containsAll(coll);
    } 
  }
  
  public boolean containsAll(TByteCollection coll) {
    synchronized (this.mutex) {
      return this.c.containsAll(coll);
    } 
  }
  
  public boolean containsAll(byte[] array) {
    synchronized (this.mutex) {
      return this.c.containsAll(array);
    } 
  }
  
  public boolean addAll(Collection<? extends Byte> coll) {
    synchronized (this.mutex) {
      return this.c.addAll(coll);
    } 
  }
  
  public boolean addAll(TByteCollection coll) {
    synchronized (this.mutex) {
      return this.c.addAll(coll);
    } 
  }
  
  public boolean addAll(byte[] array) {
    synchronized (this.mutex) {
      return this.c.addAll(array);
    } 
  }
  
  public boolean removeAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.removeAll(coll);
    } 
  }
  
  public boolean removeAll(TByteCollection coll) {
    synchronized (this.mutex) {
      return this.c.removeAll(coll);
    } 
  }
  
  public boolean removeAll(byte[] array) {
    synchronized (this.mutex) {
      return this.c.removeAll(array);
    } 
  }
  
  public boolean retainAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.retainAll(coll);
    } 
  }
  
  public boolean retainAll(TByteCollection coll) {
    synchronized (this.mutex) {
      return this.c.retainAll(coll);
    } 
  }
  
  public boolean retainAll(byte[] array) {
    synchronized (this.mutex) {
      return this.c.retainAll(array);
    } 
  }
  
  public byte getNoEntryValue() {
    return this.c.getNoEntryValue();
  }
  
  public boolean forEach(TByteProcedure procedure) {
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
