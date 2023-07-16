package gnu.trove.impl.unmodifiable;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.procedure.TShortProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableShortCollection implements TShortCollection, Serializable {
  private static final long serialVersionUID = 1820017752578914078L;
  
  final TShortCollection c;
  
  public TUnmodifiableShortCollection(TShortCollection c) {
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
  
  public boolean contains(short o) {
    return this.c.contains(o);
  }
  
  public short[] toArray() {
    return this.c.toArray();
  }
  
  public short[] toArray(short[] a) {
    return this.c.toArray(a);
  }
  
  public String toString() {
    return this.c.toString();
  }
  
  public short getNoEntryValue() {
    return this.c.getNoEntryValue();
  }
  
  public boolean forEach(TShortProcedure procedure) {
    return this.c.forEach(procedure);
  }
  
  public TShortIterator iterator() {
    return new TShortIterator() {
        TShortIterator i = TUnmodifiableShortCollection.this.c.iterator();
        
        public boolean hasNext() {
          return this.i.hasNext();
        }
        
        public short next() {
          return this.i.next();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public boolean add(short e) {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(short o) {
    throw new UnsupportedOperationException();
  }
  
  public boolean containsAll(Collection<?> coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(TShortCollection coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(short[] array) {
    return this.c.containsAll(array);
  }
  
  public boolean addAll(TShortCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection<? extends Short> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(short[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(TShortCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(short[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(TShortCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(short[] array) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
}
