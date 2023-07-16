package com.lmax.disruptor;

abstract class SingleProducerSequencerFields extends SingleProducerSequencerPad {
  long nextValue;
  
  long cachedValue;
  
  SingleProducerSequencerFields(int bufferSize, WaitStrategy waitStrategy) {
    super(bufferSize, waitStrategy);
    this.nextValue = -1L;
    this.cachedValue = -1L;
  }
}
