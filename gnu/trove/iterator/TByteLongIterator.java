package gnu.trove.iterator;

public interface TByteLongIterator extends TAdvancingIterator {
  byte key();
  
  long value();
  
  long setValue(long paramLong);
}
