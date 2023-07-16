package gnu.trove.iterator;

public interface TCharObjectIterator<V> extends TAdvancingIterator {
  char key();
  
  V value();
  
  V setValue(V paramV);
}
