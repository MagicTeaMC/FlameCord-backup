package gnu.trove.iterator;

public interface TObjectByteIterator<K> extends TAdvancingIterator {
  K key();
  
  byte value();
  
  byte setValue(byte paramByte);
}
