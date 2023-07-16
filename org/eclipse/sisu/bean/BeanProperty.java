package org.eclipse.sisu.bean;

import com.google.inject.TypeLiteral;

public interface BeanProperty<T> {
  <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> paramClass);
  
  TypeLiteral<T> getType();
  
  String getName();
  
  <B> void set(B paramB, T paramT);
}
