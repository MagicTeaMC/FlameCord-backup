package gnu.trove.queue;

import gnu.trove.TLongCollection;

public interface TLongQueue extends TLongCollection {
  long element();
  
  boolean offer(long paramLong);
  
  long peek();
  
  long poll();
}
