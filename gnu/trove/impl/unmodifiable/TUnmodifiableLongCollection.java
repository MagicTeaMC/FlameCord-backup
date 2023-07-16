package gnu.trove.impl.unmodifiable;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableLongCollection implements TLongCollection, Serializable {
  private static final long serialVersionUID = 1820017752578914078L;
  
  final TLongCollection c;
  
  public TUnmodifiableLongCollection(TLongCollection c) {
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
  
  public boolean contains(long o) {
    return this.c.contains(o);
  }
  
  public long[] toArray() {
    return this.c.toArray();
  }
  
  public long[] toArray(long[] a) {
    return this.c.toArray(a);
  }
  
  public String toString() {
    return this.c.toString();
  }
  
  public long getNoEntryValue() {
    return this.c.getNoEntryValue();
  }
  
  public boolean forEach(TLongProcedure procedure) {
    return this.c.forEach(procedure);
  }
  
  public TLongIterator iterator() {
    return new TLongIterator() {
        TLongIterator i = TUnmodifiableLongCollection.this.c.iterator();
        
        public boolean hasNext() {
          return this.i.hasNext();
        }
        
        public long next() {
          return this.i.next();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public boolean add(long e) {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(long o) {
    throw new UnsupportedOperationException();
  }
  
  public boolean containsAll(Collection<?> coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(TLongCollection coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(long[] array) {
    return this.c.containsAll(array);
  }
  
  public boolean addAll(TLongCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection<? extends Long> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(long[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(TLongCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(long[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(TLongCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(long[] array) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
}
