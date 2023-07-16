package gnu.trove.iterator;

public interface TObjectCharIterator<K> extends TAdvancingIterator {
  K key();
  
  char value();
  
  char setValue(char paramChar);
}
