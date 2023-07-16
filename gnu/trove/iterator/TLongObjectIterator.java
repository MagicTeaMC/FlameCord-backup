package gnu.trove.iterator;

public interface TLongObjectIterator<V> extends TAdvancingIterator {
  long key();
  
  V value();
  
  V setValue(V paramV);
}
