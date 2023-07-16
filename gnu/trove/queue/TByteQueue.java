package gnu.trove.queue;

import gnu.trove.TByteCollection;

public interface TByteQueue extends TByteCollection {
  byte element();
  
  boolean offer(byte paramByte);
  
  byte peek();
  
  byte poll();
}
