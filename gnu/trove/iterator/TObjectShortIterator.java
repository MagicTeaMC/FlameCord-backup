package gnu.trove.iterator;

public interface TObjectShortIterator<K> extends TAdvancingIterator {
  K key();
  
  short value();
  
  short setValue(short paramShort);
}
