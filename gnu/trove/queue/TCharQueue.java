package gnu.trove.queue;

import gnu.trove.TCharCollection;

public interface TCharQueue extends TCharCollection {
  char element();
  
  boolean offer(char paramChar);
  
  char peek();
  
  char poll();
}
