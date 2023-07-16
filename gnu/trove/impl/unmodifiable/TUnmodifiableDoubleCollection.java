package gnu.trove.impl.unmodifiable;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableDoubleCollection implements TDoubleCollection, Serializable {
  private static final long serialVersionUID = 1820017752578914078L;
  
  final TDoubleCollection c;
  
  public TUnmodifiableDoubleCollection(TDoubleCollection c) {
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
  
  public boolean contains(double o) {
    return this.c.contains(o);
  }
  
  public double[] toArray() {
    return this.c.toArray();
  }
  
  public double[] toArray(double[] a) {
    return this.c.toArray(a);
  }
  
  public String toString() {
    return this.c.toString();
  }
  
  public double getNoEntryValue() {
    return this.c.getNoEntryValue();
  }
  
  public boolean forEach(TDoubleProcedure procedure) {
    return this.c.forEach(procedure);
  }
  
  public TDoubleIterator iterator() {
    return new TDoubleIterator() {
        TDoubleIterator i = TUnmodifiableDoubleCollection.this.c.iterator();
        
        public boolean hasNext() {
          return this.i.hasNext();
        }
        
        public double next() {
          return this.i.next();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public boolean add(double e) {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(double o) {
    throw new UnsupportedOperationException();
  }
  
  public boolean containsAll(Collection<?> coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(TDoubleCollection coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(double[] array) {
    return this.c.containsAll(array);
  }
  
  public boolean addAll(TDoubleCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection<? extends Double> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(double[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(TDoubleCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(double[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(TDoubleCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(double[] array) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
}
