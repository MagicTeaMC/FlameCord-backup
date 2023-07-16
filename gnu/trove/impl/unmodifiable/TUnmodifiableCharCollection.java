package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableCharCollection implements TCharCollection, Serializable {
  private static final long serialVersionUID = 1820017752578914078L;
  
  final TCharCollection c;
  
  public TUnmodifiableCharCollection(TCharCollection c) {
    if (c == null)
      throw new NullPointerException(); 
    this.c = c;
  }
  
  public int size() {
    return this.c.size();
  }
  
  public boolean isEmpty() {
    return this.c.isEmpty();
  }
  
  public boolean contains(char o) {
    return this.c.contains(o);
  }
  
  public char[] toArray() {
    return this.c.toArray();
  }
  
  public char[] toArray(char[] a) {
    return this.c.toArray(a);
  }
  
  public String toString() {
    return this.c.toString();
  }
  
  public char getNoEntryValue() {
    return this.c.getNoEntryValue();
  }
  
  public boolean forEach(TCharProcedure procedure) {
    return this.c.forEach(procedure);
  }
  
  public TCharIterator iterator() {
    return new TCharIterator() {
        TCharIterator i = TUnmodifiableCharCollection.this.c.iterator();
        
        public boolean hasNext() {
          return this.i.hasNext();
        }
        
        public char next() {
          return this.i.next();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public boolean add(char e) {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(char o) {
    throw new UnsupportedOperationException();
  }
  
  public boolean containsAll(Collection<?> coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(TCharCollection coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(char[] array) {
    return this.c.containsAll(array);
  }
  
  public boolean addAll(TCharCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection<? extends Character> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(char[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(TCharCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(char[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(TCharCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(char[] array) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
}
