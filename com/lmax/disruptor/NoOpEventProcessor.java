package com.lmax.disruptor;

import java.util.concurrent.atomic.AtomicBoolean;

public final class NoOpEventProcessor implements EventProcessor {
  private final SequencerFollowingSequence sequence;
  
  private final AtomicBoolean running = new AtomicBoolean(false);
  
  public NoOpEventProcessor(RingBuffer<?> sequencer) {
    this.sequence = new SequencerFollowingSequence(sequencer);
  }
  
  public Sequence getSequence() {
    return this.sequence;
  }
  
  public void halt() {
    this.running.set(false);
  }
  
  public boolean isRunning() {
    return this.running.get();
  }
  
  public void run() {
    if (!this.running.compareAndSet(false, true))
      throw new IllegalStateException("Thread is already running"); 
  }
  
  private static final class SequencerFollowingSequence extends Sequence {
    private final RingBuffer<?> sequencer;
    
    private SequencerFollowingSequence(RingBuffer<?> sequencer) {
      super(-1L);
      this.sequencer = sequencer;
    }
    
    public long get() {
      return this.sequencer.getCursor();
    }
  }
}
