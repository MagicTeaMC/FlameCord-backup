package com.lmax.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

public final class BatchEventProcessor<T> implements EventProcessor {
  private static final int IDLE = 0;
  
  private static final int HALTED = 1;
  
  private static final int RUNNING = 2;
  
  private final AtomicInteger running = new AtomicInteger(0);
  
  private ExceptionHandler<? super T> exceptionHandler;
  
  private final DataProvider<T> dataProvider;
  
  private final SequenceBarrier sequenceBarrier;
  
  private final EventHandler<? super T> eventHandler;
  
  private final Sequence sequence = new Sequence(-1L);
  
  private final TimeoutHandler timeoutHandler;
  
  private final BatchStartAware batchStartAware;
  
  public BatchEventProcessor(DataProvider<T> dataProvider, SequenceBarrier sequenceBarrier, EventHandler<? super T> eventHandler) {
    this.dataProvider = dataProvider;
    this.sequenceBarrier = sequenceBarrier;
    this.eventHandler = eventHandler;
    if (eventHandler instanceof SequenceReportingEventHandler)
      ((SequenceReportingEventHandler)eventHandler).setSequenceCallback(this.sequence); 
    this.batchStartAware = (eventHandler instanceof BatchStartAware) ? (BatchStartAware)eventHandler : null;
    this.timeoutHandler = (eventHandler instanceof TimeoutHandler) ? (TimeoutHandler)eventHandler : null;
  }
  
  public Sequence getSequence() {
    return this.sequence;
  }
  
  public void halt() {
    this.running.set(1);
    this.sequenceBarrier.alert();
  }
  
  public boolean isRunning() {
    return (this.running.get() != 0);
  }
  
  public void setExceptionHandler(ExceptionHandler<? super T> exceptionHandler) {
    if (null == exceptionHandler)
      throw new NullPointerException(); 
    this.exceptionHandler = exceptionHandler;
  }
  
  public void run() {
    if (this.running.compareAndSet(0, 2)) {
      this.sequenceBarrier.clearAlert();
      notifyStart();
      try {
        if (this.running.get() == 2)
          processEvents(); 
      } finally {
        notifyShutdown();
        this.running.set(0);
      } 
    } else {
      if (this.running.get() == 2)
        throw new IllegalStateException("Thread is already running"); 
      earlyExit();
    } 
  }
  
  private void processEvents() {
    T event = null;
    long nextSequence = this.sequence.get() + 1L;
    while (true) {
      try {
        long availableSequence = this.sequenceBarrier.waitFor(nextSequence);
        if (this.batchStartAware != null)
          this.batchStartAware.onBatchStart(availableSequence - nextSequence + 1L); 
        while (nextSequence <= availableSequence) {
          event = this.dataProvider.get(nextSequence);
          this.eventHandler.onEvent(event, nextSequence, (nextSequence == availableSequence));
          nextSequence++;
        } 
        this.sequence.set(availableSequence);
      } catch (TimeoutException e) {
        notifyTimeout(this.sequence.get());
      } catch (AlertException ex) {
        if (this.running.get() != 2)
          break; 
      } catch (Throwable ex) {
        handleEventException(ex, nextSequence, event);
        this.sequence.set(nextSequence);
        nextSequence++;
      } 
    } 
  }
  
  private void earlyExit() {
    notifyStart();
    notifyShutdown();
  }
  
  private void notifyTimeout(long availableSequence) {
    try {
      if (this.timeoutHandler != null)
        this.timeoutHandler.onTimeout(availableSequence); 
    } catch (Throwable e) {
      handleEventException(e, availableSequence, null);
    } 
  }
  
  private void notifyStart() {
    if (this.eventHandler instanceof LifecycleAware)
      try {
        ((LifecycleAware)this.eventHandler).onStart();
      } catch (Throwable ex) {
        handleOnStartException(ex);
      }  
  }
  
  private void notifyShutdown() {
    if (this.eventHandler instanceof LifecycleAware)
      try {
        ((LifecycleAware)this.eventHandler).onShutdown();
      } catch (Throwable ex) {
        handleOnShutdownException(ex);
      }  
  }
  
  private void handleEventException(Throwable ex, long sequence, T event) {
    getExceptionHandler().handleEventException(ex, sequence, event);
  }
  
  private void handleOnStartException(Throwable ex) {
    getExceptionHandler().handleOnStartException(ex);
  }
  
  private void handleOnShutdownException(Throwable ex) {
    getExceptionHandler().handleOnShutdownException(ex);
  }
  
  private ExceptionHandler<? super T> getExceptionHandler() {
    ExceptionHandler<? super T> handler = this.exceptionHandler;
    if (handler == null)
      return (ExceptionHandler)ExceptionHandlers.defaultHandler(); 
    return handler;
  }
}
