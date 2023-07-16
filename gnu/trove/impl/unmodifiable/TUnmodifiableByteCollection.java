package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableByteCollection implements TByteCollection, Serializable {
  private static final long serialVersionUID = 1820017752578914078L;
  
  final TByteCollection c;
  
  public TUnmodifiableByteCollection(TByteCollection c) {
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
  
  public boolean contains(byte o) {
    return this.c.contains(o);
  }
  
  public byte[] toArray() {
    return this.c.toArray();
  }
  
  public byte[] toArray(byte[] a) {
    return this.c.toArray(a);
  }
  
  public String toString() {
    return this.c.toString();
  }
  
  public byte getNoEntryValue() {
    return this.c.getNoEntryValue();
  }
  
  public boolean forEach(TByteProcedure procedure) {
    return this.c.forEach(procedure);
  }
  
  public TByteIterator iterator() {
    return new TByteIterator() {
        TByteIterator i = TUnmodifiableByteCollection.this.c.iterator();
        
        public boolean hasNext() {
          return this.i.hasNext();
        }
        
        public byte next() {
          return this.i.next();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public boolean add(byte e) {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(byte o) {
    throw new UnsupportedOperationException();
  }
  
  public boolean containsAll(Collection<?> coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(TByteCollection coll) {
    return this.c.containsAll(coll);
  }
  
  public boolean containsAll(byte[] array) {
    return this.c.containsAll(array);
  }
  
  public boolean addAll(TByteCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection<? extends Byte> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(byte[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(TByteCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(byte[] array) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(TByteCollection coll) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(byte[] array) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
}
