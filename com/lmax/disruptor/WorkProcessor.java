package com.lmax.disruptor;

import java.util.concurrent.atomic.AtomicBoolean;

public final class WorkProcessor<T> implements EventProcessor {
  private final AtomicBoolean running = new AtomicBoolean(false);
  
  private final Sequence sequence = new Sequence(-1L);
  
  private final RingBuffer<T> ringBuffer;
  
  private final SequenceBarrier sequenceBarrier;
  
  private final WorkHandler<? super T> workHandler;
  
  private final ExceptionHandler<? super T> exceptionHandler;
  
  private final Sequence workSequence;
  
  private final EventReleaser eventReleaser = new EventReleaser() {
      public void release() {
        WorkProcessor.this.sequence.set(Long.MAX_VALUE);
      }
    };
  
  private final TimeoutHandler timeoutHandler;
  
  public WorkProcessor(RingBuffer<T> ringBuffer, SequenceBarrier sequenceBarrier, WorkHandler<? super T> workHandler, ExceptionHandler<? super T> exceptionHandler, Sequence workSequence) {
    this.ringBuffer = ringBuffer;
    this.sequenceBarrier = sequenceBarrier;
    this.workHandler = workHandler;
    this.exceptionHandler = exceptionHandler;
    this.workSequence = workSequence;
    if (this.workHandler instanceof EventReleaseAware)
      ((EventReleaseAware)this.workHandler).setEventReleaser(this.eventReleaser); 
    this.timeoutHandler = (workHandler instanceof TimeoutHandler) ? (TimeoutHandler)workHandler : null;
  }
  
  public Sequence getSequence() {
    return this.sequence;
  }
  
  public void halt() {
    this.running.set(false);
    this.sequenceBarrier.alert();
  }
  
  public boolean isRunning() {
    return this.running.get();
  }
  
  public void run() {
    if (!this.running.compareAndSet(false, true))
      throw new IllegalStateException("Thread is already running"); 
    this.sequenceBarrier.clearAlert();
    notifyStart();
    boolean processedSequence = true;
    long cachedAvailableSequence = Long.MIN_VALUE;
    long nextSequence = this.sequence.get();
    T event = null;
    while (true) {
      try {
        if (processedSequence) {
          processedSequence = false;
          do {
            nextSequence = this.workSequence.get() + 1L;
            this.sequence.set(nextSequence - 1L);
          } while (!this.workSequence.compareAndSet(nextSequence - 1L, nextSequence));
        } 
        if (cachedAvailableSequence >= nextSequence) {
          event = this.ringBuffer.get(nextSequence);
          this.workHandler.onEvent(event);
          processedSequence = true;
          continue;
        } 
        cachedAvailableSequence = this.sequenceBarrier.waitFor(nextSequence);
      } catch (TimeoutException e) {
        notifyTimeout(this.sequence.get());
      } catch (AlertException ex) {
        if (!this.running.get())
          break; 
      } catch (Throwable ex) {
        this.exceptionHandler.handleEventException(ex, nextSequence, event);
        processedSequence = true;
      } 
    } 
    notifyShutdown();
    this.running.set(false);
  }
  
  private void notifyTimeout(long availableSequence) {
    try {
      if (this.timeoutHandler != null)
        this.timeoutHandler.onTimeout(availableSequence); 
    } catch (Throwable e) {
      this.exceptionHandler.handleEventException(e, availableSequence, null);
    } 
  }
  
  private void notifyStart() {
    if (this.workHandler instanceof LifecycleAware)
      try {
        ((LifecycleAware)this.workHandler).onStart();
      } catch (Throwable ex) {
        this.exceptionHandler.handleOnStartException(ex);
      }  
  }
  
  private void notifyShutdown() {
    if (this.workHandler instanceof LifecycleAware)
      try {
        ((LifecycleAware)this.workHandler).onShutdown();
      } catch (Throwable ex) {
        this.exceptionHandler.handleOnShutdownException(ex);
      }  
  }
}
