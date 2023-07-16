package gnu.trove.iterator;

public interface TObjectIntIterator<K> extends TAdvancingIterator {
  K key();
  
  int value();
  
  int setValue(int paramInt);
}
