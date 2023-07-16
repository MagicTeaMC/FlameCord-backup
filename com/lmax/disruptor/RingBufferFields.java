package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import sun.misc.Unsafe;

abstract class RingBufferFields<E> extends RingBufferPad {
  private static final int BUFFER_PAD;
  
  private static final long REF_ARRAY_BASE;
  
  private static final int REF_ELEMENT_SHIFT;
  
  private static final Unsafe UNSAFE = Util.getUnsafe();
  
  private final long indexMask;
  
  private final Object[] entries;
  
  protected final int bufferSize;
  
  protected final Sequencer sequencer;
  
  static {
    int scale = UNSAFE.arrayIndexScale(Object[].class);
    if (4 == scale) {
      REF_ELEMENT_SHIFT = 2;
    } else if (8 == scale) {
      REF_ELEMENT_SHIFT = 3;
    } else {
      throw new IllegalStateException("Unknown pointer size");
    } 
    BUFFER_PAD = 128 / scale;
    REF_ARRAY_BASE = (UNSAFE.arrayBaseOffset(Object[].class) + (BUFFER_PAD << REF_ELEMENT_SHIFT));
  }
  
  RingBufferFields(EventFactory<E> eventFactory, Sequencer sequencer) {
    this.sequencer = sequencer;
    this.bufferSize = sequencer.getBufferSize();
    if (this.bufferSize < 1)
      throw new IllegalArgumentException("bufferSize must not be less than 1"); 
    if (Integer.bitCount(this.bufferSize) != 1)
      throw new IllegalArgumentException("bufferSize must be a power of 2"); 
    this.indexMask = (this.bufferSize - 1);
    this.entries = new Object[sequencer.getBufferSize() + 2 * BUFFER_PAD];
    fill(eventFactory);
  }
  
  private void fill(EventFactory<E> eventFactory) {
    for (int i = 0; i < this.bufferSize; i++)
      this.entries[BUFFER_PAD + i] = eventFactory.newInstance(); 
  }
  
  protected final E elementAt(long sequence) {
    return (E)UNSAFE.getObject(this.entries, REF_ARRAY_BASE + ((sequence & this.indexMask) << REF_ELEMENT_SHIFT));
  }
}
