package gnu.trove.iterator;

public interface TObjectFloatIterator<K> extends TAdvancingIterator {
  K key();
  
  float value();
  
  float setValue(float paramFloat);
}
