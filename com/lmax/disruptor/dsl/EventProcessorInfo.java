package com.lmax.disruptor.dsl;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import java.util.concurrent.Executor;

class EventProcessorInfo<T> implements ConsumerInfo {
  private final EventProcessor eventprocessor;
  
  private final EventHandler<? super T> handler;
  
  private final SequenceBarrier barrier;
  
  private boolean endOfChain = true;
  
  EventProcessorInfo(EventProcessor eventprocessor, EventHandler<? super T> handler, SequenceBarrier barrier) {
    this.eventprocessor = eventprocessor;
    this.handler = handler;
    this.barrier = barrier;
  }
  
  public EventProcessor getEventProcessor() {
    return this.eventprocessor;
  }
  
  public Sequence[] getSequences() {
    return new Sequence[] { this.eventprocessor.getSequence() };
  }
  
  public EventHandler<? super T> getHandler() {
    return this.handler;
  }
  
  public SequenceBarrier getBarrier() {
    return this.barrier;
  }
  
  public boolean isEndOfChain() {
    return this.endOfChain;
  }
  
  public void start(Executor executor) {
    executor.execute((Runnable)this.eventprocessor);
  }
  
  public void halt() {
    this.eventprocessor.halt();
  }
  
  public void markAsUsedInBarrier() {
    this.endOfChain = false;
  }
  
  public boolean isRunning() {
    return this.eventprocessor.isRunning();
  }
}
