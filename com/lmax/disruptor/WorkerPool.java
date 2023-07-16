package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WorkerPool<T> {
  private final AtomicBoolean started = new AtomicBoolean(false);
  
  private final Sequence workSequence = new Sequence(-1L);
  
  private final RingBuffer<T> ringBuffer;
  
  private final WorkProcessor<?>[] workProcessors;
  
  @SafeVarargs
  public WorkerPool(RingBuffer<T> ringBuffer, SequenceBarrier sequenceBarrier, ExceptionHandler<? super T> exceptionHandler, WorkHandler<? super T>... workHandlers) {
    this.ringBuffer = ringBuffer;
    int numWorkers = workHandlers.length;
    this.workProcessors = (WorkProcessor<?>[])new WorkProcessor[numWorkers];
    for (int i = 0; i < numWorkers; i++)
      this.workProcessors[i] = new WorkProcessor(ringBuffer, sequenceBarrier, workHandlers[i], exceptionHandler, this.workSequence); 
  }
  
  @SafeVarargs
  public WorkerPool(EventFactory<T> eventFactory, ExceptionHandler<? super T> exceptionHandler, WorkHandler<? super T>... workHandlers) {
    this.ringBuffer = RingBuffer.createMultiProducer(eventFactory, 1024, new BlockingWaitStrategy());
    SequenceBarrier barrier = this.ringBuffer.newBarrier(new Sequence[0]);
    int numWorkers = workHandlers.length;
    this.workProcessors = (WorkProcessor<?>[])new WorkProcessor[numWorkers];
    for (int i = 0; i < numWorkers; i++)
      this.workProcessors[i] = new WorkProcessor(this.ringBuffer, barrier, workHandlers[i], exceptionHandler, this.workSequence); 
    this.ringBuffer.addGatingSequences(getWorkerSequences());
  }
  
  public Sequence[] getWorkerSequences() {
    Sequence[] sequences = new Sequence[this.workProcessors.length + 1];
    for (int i = 0, size = this.workProcessors.length; i < size; i++)
      sequences[i] = this.workProcessors[i].getSequence(); 
    sequences[sequences.length - 1] = this.workSequence;
    return sequences;
  }
  
  public RingBuffer<T> start(Executor executor) {
    if (!this.started.compareAndSet(false, true))
      throw new IllegalStateException("WorkerPool has already been started and cannot be restarted until halted."); 
    long cursor = this.ringBuffer.getCursor();
    this.workSequence.set(cursor);
    for (WorkProcessor<?> processor : this.workProcessors) {
      processor.getSequence().set(cursor);
      executor.execute(processor);
    } 
    return this.ringBuffer;
  }
  
  public void drainAndHalt() {
    Sequence[] workerSequences = getWorkerSequences();
    while (this.ringBuffer.getCursor() > Util.getMinimumSequence(workerSequences))
      Thread.yield(); 
    for (WorkProcessor<?> processor : this.workProcessors)
      processor.halt(); 
    this.started.set(false);
  }
  
  public void halt() {
    for (WorkProcessor<?> processor : this.workProcessors)
      processor.halt(); 
    this.started.set(false);
  }
  
  public boolean isRunning() {
    return this.started.get();
  }
}
