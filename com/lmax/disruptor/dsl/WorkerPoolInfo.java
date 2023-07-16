package com.lmax.disruptor.dsl;

import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import java.util.concurrent.Executor;

class WorkerPoolInfo<T> implements ConsumerInfo {
  private final WorkerPool<T> workerPool;
  
  private final SequenceBarrier sequenceBarrier;
  
  private boolean endOfChain = true;
  
  WorkerPoolInfo(WorkerPool<T> workerPool, SequenceBarrier sequenceBarrier) {
    this.workerPool = workerPool;
    this.sequenceBarrier = sequenceBarrier;
  }
  
  public Sequence[] getSequences() {
    return this.workerPool.getWorkerSequences();
  }
  
  public SequenceBarrier getBarrier() {
    return this.sequenceBarrier;
  }
  
  public boolean isEndOfChain() {
    return this.endOfChain;
  }
  
  public void start(Executor executor) {
    this.workerPool.start(executor);
  }
  
  public void halt() {
    this.workerPool.halt();
  }
  
  public void markAsUsedInBarrier() {
    this.endOfChain = false;
  }
  
  public boolean isRunning() {
    return this.workerPool.isRunning();
  }
}
