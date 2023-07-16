package org.eclipse.sisu;

public interface Mediator<Q extends java.lang.annotation.Annotation, T, W> {
  void add(BeanEntry<Q, T> paramBeanEntry, W paramW) throws Exception;
  
  void remove(BeanEntry<Q, T> paramBeanEntry, W paramW) throws Exception;
}
