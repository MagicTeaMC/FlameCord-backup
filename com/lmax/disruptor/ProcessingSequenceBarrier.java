package com.lmax.disruptor;

final class ProcessingSequenceBarrier implements SequenceBarrier {
  private final WaitStrategy waitStrategy;
  
  private final Sequence dependentSequence;
  
  private volatile boolean alerted = false;
  
  private final Sequence cursorSequence;
  
  private final Sequencer sequencer;
  
  ProcessingSequenceBarrier(Sequencer sequencer, WaitStrategy waitStrategy, Sequence cursorSequence, Sequence[] dependentSequences) {
    this.sequencer = sequencer;
    this.waitStrategy = waitStrategy;
    this.cursorSequence = cursorSequence;
    if (0 == dependentSequences.length) {
      this.dependentSequence = cursorSequence;
    } else {
      this.dependentSequence = new FixedSequenceGroup(dependentSequences);
    } 
  }
  
  public long waitFor(long sequence) throws AlertException, InterruptedException, TimeoutException {
    checkAlert();
    long availableSequence = this.waitStrategy.waitFor(sequence, this.cursorSequence, this.dependentSequence, this);
    if (availableSequence < sequence)
      return availableSequence; 
    return this.sequencer.getHighestPublishedSequence(sequence, availableSequence);
  }
  
  public long getCursor() {
    return this.dependentSequence.get();
  }
  
  public boolean isAlerted() {
    return this.alerted;
  }
  
  public void alert() {
    this.alerted = true;
    this.waitStrategy.signalAllWhenBlocking();
  }
  
  public void clearAlert() {
    this.alerted = false;
  }
  
  public void checkAlert() throws AlertException {
    if (this.alerted)
      throw AlertException.INSTANCE; 
  }
}
