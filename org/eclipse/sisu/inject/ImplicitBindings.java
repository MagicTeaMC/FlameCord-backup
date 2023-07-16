package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.eclipse.sisu.Hidden;

final class ImplicitBindings {
  private final Iterable<BindingPublisher> publishers;
  
  ImplicitBindings(Iterable<BindingPublisher> publishers) {
    this.publishers = publishers;
  }
  
  public <T> Binding<T> get(TypeLiteral<T> type) {
    Key<?> implicitKey = TypeArguments.implicitKey(type.getRawType());
    for (BindingPublisher p : this.publishers) {
      if (p instanceof InjectorBindings) {
        Injector i = ((InjectorBindings)p).getInjector();
        Binding<T> binding = (Binding)i.getBindings().get(implicitKey);
        if (binding != null) {
          Logs.trace("Using implicit binding: {} from: <>", binding, i);
          return binding;
        } 
      } 
    } 
    Key justInTimeKey = Key.get(type);
    for (BindingPublisher p : this.publishers) {
      if (p instanceof InjectorBindings) {
        Injector i = ((InjectorBindings)p).getInjector();
        try {
          Binding<?> binding = i.getBinding(justInTimeKey);
          if (Sources.getAnnotation(binding, Hidden.class) == null) {
            Logs.trace("Using just-in-time binding: {} from: <>", binding, i);
            return (Binding)binding;
          } 
        } catch (RuntimeException e) {
          Logs.trace("Problem with just-in-time binding: {}", justInTimeKey, e);
        } catch (LinkageError e) {
          Logs.trace("Problem with just-in-time binding: {}", justInTimeKey, e);
        } 
      } 
    } 
    return null;
  }
}
