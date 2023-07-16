package org.eclipse.sisu.inject;

import com.google.inject.ImplementedBy;
import com.google.inject.Injector;

@ImplementedBy(DefaultBeanLocator.class)
public interface MutableBeanLocator extends BeanLocator {
  boolean add(BindingPublisher paramBindingPublisher);
  
  boolean remove(BindingPublisher paramBindingPublisher);
  
  Iterable<BindingPublisher> publishers();
  
  void clear();
  
  @Deprecated
  void add(Injector paramInjector, int paramInt);
  
  @Deprecated
  void remove(Injector paramInjector);
}
