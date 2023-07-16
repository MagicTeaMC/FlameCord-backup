package com.lmax.disruptor;

public class EventPoller<T> {
  private final DataProvider<T> dataProvider;
  
  private final Sequencer sequencer;
  
  private final Sequence sequence;
  
  private final Sequence gatingSequence;
  
  public enum PollState {
    PROCESSING, GATING, IDLE;
  }
  
  public EventPoller(DataProvider<T> dataProvider, Sequencer sequencer, Sequence sequence, Sequence gatingSequence) {
    this.dataProvider = dataProvider;
    this.sequencer = sequencer;
    this.sequence = sequence;
    this.gatingSequence = gatingSequence;
  }
  
  public PollState poll(Handler<T> eventHandler) throws Exception {
    long currentSequence = this.sequence.get();
    long nextSequence = currentSequence + 1L;
    long availableSequence = this.sequencer.getHighestPublishedSequence(nextSequence, this.gatingSequence.get());
    if (nextSequence <= availableSequence) {
      long processedSequence = currentSequence;
      try {
        boolean processNextEvent;
        do {
          T event = this.dataProvider.get(nextSequence);
          processNextEvent = eventHandler.onEvent(event, nextSequence, (nextSequence == availableSequence));
          processedSequence = nextSequence;
          nextSequence++;
        } while ((((nextSequence <= availableSequence) ? 1 : 0) & processNextEvent) != 0);
      } finally {
        this.sequence.set(processedSequence);
      } 
      return PollState.PROCESSING;
    } 
    if (this.sequencer.getCursor() >= nextSequence)
      return PollState.GATING; 
    return PollState.IDLE;
  }
  
  public static <T> EventPoller<T> newInstance(DataProvider<T> dataProvider, Sequencer sequencer, Sequence sequence, Sequence cursorSequence, Sequence... gatingSequences) {
    Sequence gatingSequence;
    if (gatingSequences.length == 0) {
      gatingSequence = cursorSequence;
    } else if (gatingSequences.length == 1) {
      gatingSequence = gatingSequences[0];
    } else {
      gatingSequence = new FixedSequenceGroup(gatingSequences);
    } 
    return new EventPoller<>(dataProvider, sequencer, sequence, gatingSequence);
  }
  
  public Sequence getSequence() {
    return this.sequence;
  }
  
  public static interface Handler<T> {
    boolean onEvent(T param1T, long param1Long, boolean param1Boolean) throws Exception;
  }
}
