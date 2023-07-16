package gnu.trove.iterator;

public interface TDoubleLongIterator extends TAdvancingIterator {
  double key();
  
  long value();
  
  long setValue(long paramLong);
}
