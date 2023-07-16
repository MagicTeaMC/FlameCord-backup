package com.lmax.disruptor.util;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import sun.misc.Unsafe;

public final class Util {
  private static final Unsafe THE_UNSAFE;
  
  public static int ceilingNextPowerOfTwo(int x) {
    return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
  }
  
  public static long getMinimumSequence(Sequence[] sequences) {
    return getMinimumSequence(sequences, Long.MAX_VALUE);
  }
  
  public static long getMinimumSequence(Sequence[] sequences, long minimum) {
    for (int i = 0, n = sequences.length; i < n; i++) {
      long value = sequences[i].get();
      minimum = Math.min(minimum, value);
    } 
    return minimum;
  }
  
  public static Sequence[] getSequencesFor(EventProcessor... processors) {
    Sequence[] sequences = new Sequence[processors.length];
    for (int i = 0; i < sequences.length; i++)
      sequences[i] = processors[i].getSequence(); 
    return sequences;
  }
  
  static {
    try {
      PrivilegedExceptionAction<Unsafe> action = new PrivilegedExceptionAction<Unsafe>() {
          public Unsafe run() throws Exception {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe)theUnsafe.get(null);
          }
        };
      THE_UNSAFE = AccessController.<Unsafe>doPrivileged(action);
    } catch (Exception e) {
      throw new RuntimeException("Unable to load unsafe", e);
    } 
  }
  
  public static Unsafe getUnsafe() {
    return THE_UNSAFE;
  }
  
  public static int log2(int i) {
    int r = 0;
    while ((i >>= 1) != 0)
      r++; 
    return r;
  }
}
