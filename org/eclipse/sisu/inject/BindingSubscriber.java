package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.TypeLiteral;

public interface BindingSubscriber<T> {
  TypeLiteral<T> type();
  
  void add(Binding<T> paramBinding, int paramInt);
  
  void remove(Binding<T> paramBinding);
  
  Iterable<Binding<T>> bindings();
}
