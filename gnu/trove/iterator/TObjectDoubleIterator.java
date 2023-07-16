package gnu.trove.iterator;

public interface TObjectDoubleIterator<K> extends TAdvancingIterator {
  K key();
  
  double value();
  
  double setValue(double paramDouble);
}
