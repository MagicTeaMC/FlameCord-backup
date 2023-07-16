package gnu.trove.impl.unmodifiable;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableIntCollection implements TIntCollection, Serializable {
  private static final long serialVersionUID = 1820017752578914078L;
  
  final TIntCollection c;
  
  public TUnmodifiableIntCollection(TIntCollection c) {
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
  
  public boolean contains(int o) {
    return this.c.contains(o);
  }
  
  public int[] toArray() {
    return this.c.toArray();
  }
  
  public int[] toArray(int[] a) {
    return this.c.toArray(a);
  }
  
  public String toString() {
    return this.c.toString();
  }
  
  public int getNoEntryValue() {
    return this.c.getNoEntryValue();
  }
  
  public boolean forEach(TIntProcedure procedure) {
    return this.c.forEach(procedure);
  }
  
  public TIntIterator iterator() {
    return new TIntIterator() {
        TIntIterator i = TUnmodifiableIntCollection.this.c.iterator();
        
        public boolean hasNext() {
          return this.i.hasNext();
        }
        
        public int next() {
          return this.i.next();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public boolean add(int e) {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(int o) {
    throw new UnsupportedOperationException();
  }
  
  public boolean containsAll(Collection<?> coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(TIntCollection coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(int[] array) {
    return this.c.containsAll(array);
  }
  
  public boolean addAll(TIntCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection<? extends Integer> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(int[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(TIntCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(int[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(TIntCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(int[] array) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
}
