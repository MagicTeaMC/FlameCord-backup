package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class AbstractSequencer implements Sequencer {
  private static final AtomicReferenceFieldUpdater<AbstractSequencer, Sequence[]> SEQUENCE_UPDATER = (AtomicReferenceFieldUpdater)AtomicReferenceFieldUpdater.newUpdater(AbstractSequencer.class, (Class)Sequence[].class, "gatingSequences");
  
  protected final int bufferSize;
  
  protected final WaitStrategy waitStrategy;
  
  protected final Sequence cursor = new Sequence(-1L);
  
  protected volatile Sequence[] gatingSequences = new Sequence[0];
  
  public AbstractSequencer(int bufferSize, WaitStrategy waitStrategy) {
    if (bufferSize < 1)
      throw new IllegalArgumentException("bufferSize must not be less than 1"); 
    if (Integer.bitCount(bufferSize) != 1)
      throw new IllegalArgumentException("bufferSize must be a power of 2"); 
    this.bufferSize = bufferSize;
    this.waitStrategy = waitStrategy;
  }
  
  public final long getCursor() {
    return this.cursor.get();
  }
  
  public final int getBufferSize() {
    return this.bufferSize;
  }
  
  public final void addGatingSequences(Sequence... gatingSequences) {
    SequenceGroups.addSequences(this, SEQUENCE_UPDATER, this, gatingSequences);
  }
  
  public boolean removeGatingSequence(Sequence sequence) {
    return SequenceGroups.removeSequence(this, SEQUENCE_UPDATER, sequence);
  }
  
  public long getMinimumSequence() {
    return Util.getMinimumSequence(this.gatingSequences, this.cursor.get());
  }
  
  public SequenceBarrier newBarrier(Sequence... sequencesToTrack) {
    return new ProcessingSequenceBarrier(this, this.waitStrategy, this.cursor, sequencesToTrack);
  }
  
  public <T> EventPoller<T> newPoller(DataProvider<T> dataProvider, Sequence... gatingSequences) {
    return EventPoller.newInstance(dataProvider, this, new Sequence(), this.cursor, gatingSequences);
  }
  
  public String toString() {
    return "AbstractSequencer{waitStrategy=" + this.waitStrategy + ", cursor=" + this.cursor + ", gatingSequences=" + 
      
      Arrays.toString((Object[])this.gatingSequences) + '}';
  }
}
