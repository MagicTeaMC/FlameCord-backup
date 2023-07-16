package gnu.trove.iterator;

public interface TObjectLongIterator<K> extends TAdvancingIterator {
  K key();
  
  long value();
  
  long setValue(long paramLong);
}
