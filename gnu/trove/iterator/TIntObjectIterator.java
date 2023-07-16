package gnu.trove.iterator;

public interface TIntObjectIterator<V> extends TAdvancingIterator {
  int key();
  
  V value();
  
  V setValue(V paramV);
}
