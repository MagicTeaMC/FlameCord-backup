package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.Arrays;

public final class FixedSequenceGroup extends Sequence {
  private final Sequence[] sequences;
  
  public FixedSequenceGroup(Sequence[] sequences) {
    this.sequences = Arrays.<Sequence>copyOf(sequences, sequences.length);
  }
  
  public long get() {
    return Util.getMinimumSequence(this.sequences);
  }
  
  public String toString() {
    return Arrays.toString((Object[])this.sequences);
  }
  
  public void set(long value) {
    throw new UnsupportedOperationException();
  }
  
  public boolean compareAndSet(long expectedValue, long newValue) {
    throw new UnsupportedOperationException();
  }
  
  public long incrementAndGet() {
    throw new UnsupportedOperationException();
  }
  
  public long addAndGet(long increment) {
    throw new UnsupportedOperationException();
  }
}
