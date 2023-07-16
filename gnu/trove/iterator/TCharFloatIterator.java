package gnu.trove.iterator;

public interface TCharFloatIterator extends TAdvancingIterator {
  char key();
  
  float value();
  
  float setValue(float paramFloat);
}
