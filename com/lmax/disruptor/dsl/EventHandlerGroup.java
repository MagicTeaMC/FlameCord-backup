package com.lmax.disruptor.dsl;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkHandler;
import java.util.Arrays;

public class EventHandlerGroup<T> {
  private final Disruptor<T> disruptor;
  
  private final ConsumerRepository<T> consumerRepository;
  
  private final Sequence[] sequences;
  
  EventHandlerGroup(Disruptor<T> disruptor, ConsumerRepository<T> consumerRepository, Sequence[] sequences) {
    this.disruptor = disruptor;
    this.consumerRepository = consumerRepository;
    this.sequences = Arrays.<Sequence>copyOf(sequences, sequences.length);
  }
  
  public EventHandlerGroup<T> and(EventHandlerGroup<T> otherHandlerGroup) {
    Sequence[] combinedSequences = new Sequence[this.sequences.length + otherHandlerGroup.sequences.length];
    System.arraycopy(this.sequences, 0, combinedSequences, 0, this.sequences.length);
    System.arraycopy(otherHandlerGroup.sequences, 0, combinedSequences, this.sequences.length, otherHandlerGroup.sequences.length);
    return new EventHandlerGroup(this.disruptor, this.consumerRepository, combinedSequences);
  }
  
  public EventHandlerGroup<T> and(EventProcessor... processors) {
    Sequence[] combinedSequences = new Sequence[this.sequences.length + processors.length];
    for (int i = 0; i < processors.length; i++) {
      this.consumerRepository.add(processors[i]);
      combinedSequences[i] = processors[i].getSequence();
    } 
    System.arraycopy(this.sequences, 0, combinedSequences, processors.length, this.sequences.length);
    return new EventHandlerGroup(this.disruptor, this.consumerRepository, combinedSequences);
  }
  
  @SafeVarargs
  public final EventHandlerGroup<T> then(EventHandler<? super T>... handlers) {
    return handleEventsWith(handlers);
  }
  
  @SafeVarargs
  public final EventHandlerGroup<T> then(EventProcessorFactory<T>... eventProcessorFactories) {
    return handleEventsWith(eventProcessorFactories);
  }
  
  @SafeVarargs
  public final EventHandlerGroup<T> thenHandleEventsWithWorkerPool(WorkHandler<? super T>... handlers) {
    return handleEventsWithWorkerPool(handlers);
  }
  
  @SafeVarargs
  public final EventHandlerGroup<T> handleEventsWith(EventHandler<? super T>... handlers) {
    return this.disruptor.createEventProcessors(this.sequences, handlers);
  }
  
  @SafeVarargs
  public final EventHandlerGroup<T> handleEventsWith(EventProcessorFactory<T>... eventProcessorFactories) {
    return this.disruptor.createEventProcessors(this.sequences, eventProcessorFactories);
  }
  
  @SafeVarargs
  public final EventHandlerGroup<T> handleEventsWithWorkerPool(WorkHandler<? super T>... handlers) {
    return this.disruptor.createWorkerPool(this.sequences, handlers);
  }
  
  public SequenceBarrier asSequenceBarrier() {
    return this.disruptor.getRingBuffer().newBarrier(this.sequences);
  }
}
