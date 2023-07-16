package org.eclipse.sisu.inject;

public interface BindingPublisher {
  <T> void subscribe(BindingSubscriber<T> paramBindingSubscriber);
  
  <T> void unsubscribe(BindingSubscriber<T> paramBindingSubscriber);
  
  int maxBindingRank();
}
