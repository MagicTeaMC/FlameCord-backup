package com.lmax.disruptor.dsl;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.DataProvider;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.util.Util;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Disruptor<T> {
  private final RingBuffer<T> ringBuffer;
  
  private final Executor executor;
  
  private final ConsumerRepository<T> consumerRepository = new ConsumerRepository<>();
  
  private final AtomicBoolean started = new AtomicBoolean(false);
  
  private ExceptionHandler<? super T> exceptionHandler = new ExceptionHandlerWrapper<>();
  
  @Deprecated
  public Disruptor(EventFactory<T> eventFactory, int ringBufferSize, Executor executor) {
    this(RingBuffer.createMultiProducer(eventFactory, ringBufferSize), executor);
  }
  
  @Deprecated
  public Disruptor(EventFactory<T> eventFactory, int ringBufferSize, Executor executor, ProducerType producerType, WaitStrategy waitStrategy) {
    this(RingBuffer.create(producerType, eventFactory, ringBufferSize, waitStrategy), executor);
  }
  
  public Disruptor(EventFactory<T> eventFactory, int ringBufferSize, ThreadFactory threadFactory) {
    this(RingBuffer.createMultiProducer(eventFactory, ringBufferSize), new BasicExecutor(threadFactory));
  }
  
  public Disruptor(EventFactory<T> eventFactory, int ringBufferSize, ThreadFactory threadFactory, ProducerType producerType, WaitStrategy waitStrategy) {
    this(
        RingBuffer.create(producerType, eventFactory, ringBufferSize, waitStrategy), new BasicExecutor(threadFactory));
  }
  
  private Disruptor(RingBuffer<T> ringBuffer, Executor executor) {
    this.ringBuffer = ringBuffer;
    this.executor = executor;
  }
  
  @SafeVarargs
  public final EventHandlerGroup<T> handleEventsWith(EventHandler<? super T>... handlers) {
    return createEventProcessors(new Sequence[0], handlers);
  }
  
  @SafeVarargs
  public final EventHandlerGroup<T> handleEventsWith(EventProcessorFactory<T>... eventProcessorFactories) {
    Sequence[] barrierSequences = new Sequence[0];
    return createEventProcessors(barrierSequences, eventProcessorFactories);
  }
  
  public EventHandlerGroup<T> handleEventsWith(EventProcessor... processors) {
    for (EventProcessor processor : processors)
      this.consumerRepository.add(processor); 
    Sequence[] sequences = new Sequence[processors.length];
    for (int i = 0; i < processors.length; i++)
      sequences[i] = processors[i].getSequence(); 
    this.ringBuffer.addGatingSequences(sequences);
    return new EventHandlerGroup<>(this, this.consumerRepository, Util.getSequencesFor(processors));
  }
  
  @SafeVarargs
  public final EventHandlerGroup<T> handleEventsWithWorkerPool(WorkHandler<T>... workHandlers) {
    return createWorkerPool(new Sequence[0], (WorkHandler<? super T>[])workHandlers);
  }
  
  public void handleExceptionsWith(ExceptionHandler<? super T> exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }
  
  public void setDefaultExceptionHandler(ExceptionHandler<? super T> exceptionHandler) {
    checkNotStarted();
    if (!(this.exceptionHandler instanceof ExceptionHandlerWrapper))
      throw new IllegalStateException("setDefaultExceptionHandler can not be used after handleExceptionsWith"); 
    ((ExceptionHandlerWrapper)this.exceptionHandler).switchTo(exceptionHandler);
  }
  
  public ExceptionHandlerSetting<T> handleExceptionsFor(EventHandler<T> eventHandler) {
    return new ExceptionHandlerSetting<>(eventHandler, this.consumerRepository);
  }
  
  @SafeVarargs
  public final EventHandlerGroup<T> after(EventHandler<T>... handlers) {
    Sequence[] sequences = new Sequence[handlers.length];
    for (int i = 0, handlersLength = handlers.length; i < handlersLength; i++)
      sequences[i] = this.consumerRepository.getSequenceFor(handlers[i]); 
    return new EventHandlerGroup<>(this, this.consumerRepository, sequences);
  }
  
  public EventHandlerGroup<T> after(EventProcessor... processors) {
    for (EventProcessor processor : processors)
      this.consumerRepository.add(processor); 
    return new EventHandlerGroup<>(this, this.consumerRepository, Util.getSequencesFor(processors));
  }
  
  public void publishEvent(EventTranslator<T> eventTranslator) {
    this.ringBuffer.publishEvent(eventTranslator);
  }
  
  public <A> void publishEvent(EventTranslatorOneArg<T, A> eventTranslator, A arg) {
    this.ringBuffer.publishEvent(eventTranslator, arg);
  }
  
  public <A> void publishEvents(EventTranslatorOneArg<T, A> eventTranslator, A[] arg) {
    this.ringBuffer.publishEvents(eventTranslator, (Object[])arg);
  }
  
  public <A, B> void publishEvent(EventTranslatorTwoArg<T, A, B> eventTranslator, A arg0, B arg1) {
    this.ringBuffer.publishEvent(eventTranslator, arg0, arg1);
  }
  
  public <A, B, C> void publishEvent(EventTranslatorThreeArg<T, A, B, C> eventTranslator, A arg0, B arg1, C arg2) {
    this.ringBuffer.publishEvent(eventTranslator, arg0, arg1, arg2);
  }
  
  public RingBuffer<T> start() {
    checkOnlyStartedOnce();
    for (ConsumerInfo consumerInfo : this.consumerRepository)
      consumerInfo.start(this.executor); 
    return this.ringBuffer;
  }
  
  public void halt() {
    for (ConsumerInfo consumerInfo : this.consumerRepository)
      consumerInfo.halt(); 
  }
  
  public void shutdown() {
    try {
      shutdown(-1L, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      this.exceptionHandler.handleOnShutdownException((Throwable)e);
    } 
  }
  
  public void shutdown(long timeout, TimeUnit timeUnit) throws TimeoutException {
    long timeOutAt = System.currentTimeMillis() + timeUnit.toMillis(timeout);
    while (hasBacklog()) {
      if (timeout >= 0L && System.currentTimeMillis() > timeOutAt)
        throw TimeoutException.INSTANCE; 
    } 
    halt();
  }
  
  public RingBuffer<T> getRingBuffer() {
    return this.ringBuffer;
  }
  
  public long getCursor() {
    return this.ringBuffer.getCursor();
  }
  
  public long getBufferSize() {
    return this.ringBuffer.getBufferSize();
  }
  
  public T get(long sequence) {
    return (T)this.ringBuffer.get(sequence);
  }
  
  public SequenceBarrier getBarrierFor(EventHandler<T> handler) {
    return this.consumerRepository.getBarrierFor(handler);
  }
  
  public long getSequenceValueFor(EventHandler<T> b1) {
    return this.consumerRepository.getSequenceFor(b1).get();
  }
  
  private boolean hasBacklog() {
    long cursor = this.ringBuffer.getCursor();
    for (Sequence consumer : this.consumerRepository.getLastSequenceInChain(false)) {
      if (cursor > consumer.get())
        return true; 
    } 
    return false;
  }
  
  EventHandlerGroup<T> createEventProcessors(Sequence[] barrierSequences, EventHandler<? super T>[] eventHandlers) {
    checkNotStarted();
    Sequence[] processorSequences = new Sequence[eventHandlers.length];
    SequenceBarrier barrier = this.ringBuffer.newBarrier(barrierSequences);
    for (int i = 0, eventHandlersLength = eventHandlers.length; i < eventHandlersLength; i++) {
      EventHandler<? super T> eventHandler = eventHandlers[i];
      BatchEventProcessor<T> batchEventProcessor = new BatchEventProcessor((DataProvider)this.ringBuffer, barrier, eventHandler);
      if (this.exceptionHandler != null)
        batchEventProcessor.setExceptionHandler(this.exceptionHandler); 
      this.consumerRepository.add((EventProcessor)batchEventProcessor, eventHandler, barrier);
      processorSequences[i] = batchEventProcessor.getSequence();
    } 
    updateGatingSequencesForNextInChain(barrierSequences, processorSequences);
    return new EventHandlerGroup<>(this, this.consumerRepository, processorSequences);
  }
  
  private void updateGatingSequencesForNextInChain(Sequence[] barrierSequences, Sequence[] processorSequences) {
    if (processorSequences.length > 0) {
      this.ringBuffer.addGatingSequences(processorSequences);
      for (Sequence barrierSequence : barrierSequences)
        this.ringBuffer.removeGatingSequence(barrierSequence); 
      this.consumerRepository.unMarkEventProcessorsAsEndOfChain(barrierSequences);
    } 
  }
  
  EventHandlerGroup<T> createEventProcessors(Sequence[] barrierSequences, EventProcessorFactory<T>[] processorFactories) {
    EventProcessor[] eventProcessors = new EventProcessor[processorFactories.length];
    for (int i = 0; i < processorFactories.length; i++)
      eventProcessors[i] = processorFactories[i].createEventProcessor(this.ringBuffer, barrierSequences); 
    return handleEventsWith(eventProcessors);
  }
  
  EventHandlerGroup<T> createWorkerPool(Sequence[] barrierSequences, WorkHandler<? super T>[] workHandlers) {
    SequenceBarrier sequenceBarrier = this.ringBuffer.newBarrier(barrierSequences);
    WorkerPool<T> workerPool = new WorkerPool(this.ringBuffer, sequenceBarrier, this.exceptionHandler, (WorkHandler[])workHandlers);
    this.consumerRepository.add(workerPool, sequenceBarrier);
    Sequence[] workerSequences = workerPool.getWorkerSequences();
    updateGatingSequencesForNextInChain(barrierSequences, workerSequences);
    return new EventHandlerGroup<>(this, this.consumerRepository, workerSequences);
  }
  
  private void checkNotStarted() {
    if (this.started.get())
      throw new IllegalStateException("All event handlers must be added before calling starts."); 
  }
  
  private void checkOnlyStartedOnce() {
    if (!this.started.compareAndSet(false, true))
      throw new IllegalStateException("Disruptor.start() must only be called once."); 
  }
  
  public String toString() {
    return "Disruptor{ringBuffer=" + this.ringBuffer + ", started=" + this.started + ", executor=" + this.executor + '}';
  }
}
