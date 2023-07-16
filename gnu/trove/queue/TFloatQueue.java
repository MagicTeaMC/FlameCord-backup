package gnu.trove.queue;

import gnu.trove.TFloatCollection;

public interface TFloatQueue extends TFloatCollection {
  float element();
  
  boolean offer(float paramFloat);
  
  float peek();
  
  float poll();
}
