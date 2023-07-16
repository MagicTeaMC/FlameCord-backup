package gnu.trove.iterator;

public interface TFloatObjectIterator<V> extends TAdvancingIterator {
  float key();
  
  V value();
  
  V setValue(V paramV);
}
