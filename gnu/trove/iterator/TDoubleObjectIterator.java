package gnu.trove.iterator;

public interface TDoubleObjectIterator<V> extends TAdvancingIterator {
  double key();
  
  V value();
  
  V setValue(V paramV);
}
