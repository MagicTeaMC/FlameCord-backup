package com.lmax.disruptor.dsl;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.ExceptionHandler;

public class ExceptionHandlerSetting<T> {
  private final EventHandler<T> eventHandler;
  
  private final ConsumerRepository<T> consumerRepository;
  
  ExceptionHandlerSetting(EventHandler<T> eventHandler, ConsumerRepository<T> consumerRepository) {
    this.eventHandler = eventHandler;
    this.consumerRepository = consumerRepository;
  }
  
  public void with(ExceptionHandler<? super T> exceptionHandler) {
    EventProcessor eventProcessor = this.consumerRepository.getEventProcessorFor(this.eventHandler);
    if (eventProcessor instanceof BatchEventProcessor) {
      ((BatchEventProcessor)eventProcessor).setExceptionHandler(exceptionHandler);
      this.consumerRepository.getBarrierFor(this.eventHandler).alert();
    } else {
      throw new RuntimeException("EventProcessor: " + eventProcessor + " is not a BatchEventProcessor and does not support exception handlers");
    } 
  }
}
