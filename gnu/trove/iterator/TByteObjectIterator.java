package gnu.trove.iterator;

public interface TByteObjectIterator<V> extends TAdvancingIterator {
  byte key();
  
  V value();
  
  V setValue(V paramV);
}
