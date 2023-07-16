package com.lmax.disruptor;

public final class AggregateEventHandler<T> implements EventHandler<T>, LifecycleAware {
  private final EventHandler<T>[] eventHandlers;
  
  @SafeVarargs
  public AggregateEventHandler(EventHandler<T>... eventHandlers) {
    this.eventHandlers = eventHandlers;
  }
  
  public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
    for (EventHandler<T> eventHandler : this.eventHandlers)
      eventHandler.onEvent(event, sequence, endOfBatch); 
  }
  
  public void onStart() {
    for (EventHandler<T> eventHandler : this.eventHandlers) {
      if (eventHandler instanceof LifecycleAware)
        ((LifecycleAware)eventHandler).onStart(); 
    } 
  }
  
  public void onShutdown() {
    for (EventHandler<T> eventHandler : this.eventHandlers) {
      if (eventHandler instanceof LifecycleAware)
        ((LifecycleAware)eventHandler).onShutdown(); 
    } 
  }
}
