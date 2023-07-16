package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedCharCollection implements TCharCollection, Serializable {
  private static final long serialVersionUID = 3053995032091335093L;
  
  final TCharCollection c;
  
  final Object mutex;
  
  public TSynchronizedCharCollection(TCharCollection c) {
    if (c == null)
      throw new NullPointerException(); 
    this.c = c;
    this.mutex = this;
  }
  
  public TSynchronizedCharCollection(TCharCollection c, Object mutex) {
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
  
  public boolean contains(char o) {
    synchronized (this.mutex) {
      return this.c.contains(o);
    } 
  }
  
  public char[] toArray() {
    synchronized (this.mutex) {
      return this.c.toArray();
    } 
  }
  
  public char[] toArray(char[] a) {
    synchronized (this.mutex) {
      return this.c.toArray(a);
    } 
  }
  
  public TCharIterator iterator() {
    return this.c.iterator();
  }
  
  public boolean add(char e) {
    synchronized (this.mutex) {
      return this.c.add(e);
    } 
  }
  
  public boolean remove(char o) {
    synchronized (this.mutex) {
      return this.c.remove(o);
    } 
  }
  
  public boolean containsAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.containsAll(coll);
    } 
  }
  
  public boolean containsAll(TCharCollection coll) {
    synchronized (this.mutex) {
      return this.c.containsAll(coll);
    } 
  }
  
  public boolean containsAll(char[] array) {
    synchronized (this.mutex) {
      return this.c.containsAll(array);
    } 
  }
  
  public boolean addAll(Collection<? extends Character> coll) {
    synchronized (this.mutex) {
      return this.c.addAll(coll);
    } 
  }
  
  public boolean addAll(TCharCollection coll) {
    synchronized (this.mutex) {
      return this.c.addAll(coll);
    } 
  }
  
  public boolean addAll(char[] array) {
    synchronized (this.mutex) {
      return this.c.addAll(array);
    } 
  }
  
  public boolean removeAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.removeAll(coll);
    } 
  }
  
  public boolean removeAll(TCharCollection coll) {
    synchronized (this.mutex) {
      return this.c.removeAll(coll);
    } 
  }
  
  public boolean removeAll(char[] array) {
    synchronized (this.mutex) {
      return this.c.removeAll(array);
    } 
  }
  
  public boolean retainAll(Collection<?> coll) {
    synchronized (this.mutex) {
      return this.c.retainAll(coll);
    } 
  }
  
  public boolean retainAll(TCharCollection coll) {
    synchronized (this.mutex) {
      return this.c.retainAll(coll);
    } 
  }
  
  public boolean retainAll(char[] array) {
    synchronized (this.mutex) {
      return this.c.retainAll(array);
    } 
  }
  
  public char getNoEntryValue() {
    return this.c.getNoEntryValue();
  }
  
  public boolean forEach(TCharProcedure procedure) {
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
