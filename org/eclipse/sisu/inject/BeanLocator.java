package org.eclipse.sisu.inject;

import com.google.inject.ImplementedBy;
import com.google.inject.Key;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;

@ImplementedBy(MutableBeanLocator.class)
public interface BeanLocator {
  <Q extends java.lang.annotation.Annotation, T> Iterable<? extends BeanEntry<Q, T>> locate(Key<T> paramKey);
  
  <Q extends java.lang.annotation.Annotation, T, W> void watch(Key<T> paramKey, Mediator<Q, T, W> paramMediator, W paramW);
}
