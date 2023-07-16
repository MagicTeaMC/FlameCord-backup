package gnu.trove.iterator;

public interface TShortObjectIterator<V> extends TAdvancingIterator {
  short key();
  
  V value();
  
  V setValue(V paramV);
}
