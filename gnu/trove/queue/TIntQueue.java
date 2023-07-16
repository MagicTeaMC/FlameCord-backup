package gnu.trove.queue;

import gnu.trove.TIntCollection;

public interface TIntQueue extends TIntCollection {
  int element();
  
  boolean offer(int paramInt);
  
  int peek();
  
  int poll();
}
