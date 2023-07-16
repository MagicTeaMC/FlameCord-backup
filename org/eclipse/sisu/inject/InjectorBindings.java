package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.sisu.Hidden;

public final class InjectorBindings implements BindingPublisher {
  private static final TypeLiteral<Object> OBJECT_TYPE_LITERAL = TypeLiteral.get(Object.class);
  
  private static final Binding<?>[] NO_BINDINGS = (Binding<?>[])new Binding[0];
  
  private final Injector injector;
  
  private final RankingFunction function;
  
  private volatile Binding<?>[] wildcards;
  
  public InjectorBindings(Injector injector, RankingFunction function) {
    this.injector = injector;
    this.function = function;
  }
  
  public InjectorBindings(Injector injector) {
    this(injector, (RankingFunction)injector.getInstance(RankingFunction.class));
  }
  
  public Injector getInjector() {
    return this.injector;
  }
  
  public <T> void subscribe(BindingSubscriber<T> subscriber) {
    TypeLiteral<T> type = subscriber.type();
    Class<?> clazz = type.getRawType();
    if (clazz != Object.class) {
      publishExactMatches(type, subscriber);
      if (clazz != type.getType())
        publishGenericMatches(type, subscriber, clazz); 
    } 
    publishWildcardMatches(type, subscriber);
  }
  
  public <T> void unsubscribe(BindingSubscriber<T> subscriber) {
    Map<Key<?>, ?> ourBindings = this.injector.getBindings();
    for (Binding<T> binding : subscriber.bindings()) {
      if (binding == ourBindings.get(binding.getKey()))
        subscriber.remove(binding); 
    } 
  }
  
  public int maxBindingRank() {
    return this.function.maxRank();
  }
  
  public int hashCode() {
    return this.injector.hashCode();
  }
  
  public boolean equals(Object rhs) {
    if (this == rhs)
      return true; 
    if (rhs instanceof InjectorBindings)
      return this.injector.equals(((InjectorBindings)rhs).injector); 
    return false;
  }
  
  public String toString() {
    return Logs.toString(this.injector);
  }
  
  private static <T, S> boolean isAssignableFrom(TypeLiteral<T> type, Binding<S> binding) {
    Class<?> implementation = Implementations.find(binding);
    if (implementation != null && type.getRawType() != implementation)
      return TypeArguments.isAssignableFrom(type, TypeLiteral.get(implementation)); 
    return false;
  }
  
  private <T> void publishExactMatches(TypeLiteral<T> type, BindingSubscriber<T> subscriber) {
    List<Binding<T>> bindings = this.injector.findBindingsByType(type);
    for (int i = 0, size = bindings.size(); i < size; i++) {
      Binding<T> binding = bindings.get(i);
      if (Sources.getAnnotation(binding, Hidden.class) == null)
        subscriber.add(binding, this.function.rank(binding)); 
    } 
  }
  
  private <T, S> void publishGenericMatches(TypeLiteral<T> type, BindingSubscriber<T> subscriber, Class<S> rawType) {
    List<Binding<S>> bindings = this.injector.findBindingsByType(TypeLiteral.get(rawType));
    for (int i = 0, size = bindings.size(); i < size; i++) {
      Binding<?> binding = bindings.get(i);
      if (Sources.getAnnotation(binding, Hidden.class) == null && isAssignableFrom(type, binding))
        subscriber.add((Binding)binding, this.function.rank(binding)); 
    } 
  }
  
  private <T> void publishWildcardMatches(TypeLiteral<T> type, BindingSubscriber<T> subscriber) {
    boolean untyped = (type.getRawType() == Object.class);
    byte b;
    int i;
    Binding[] arrayOfBinding;
    for (i = (arrayOfBinding = (Binding[])getWildcardBindings()).length, b = 0; b < i; ) {
      Binding<?> binding = arrayOfBinding[b];
      if (untyped || isAssignableFrom(type, binding))
        subscriber.add((Binding)binding, this.function.rank(binding)); 
      b++;
    } 
  }
  
  private Binding<?>[] getWildcardBindings() {
    if (this.wildcards == null)
      synchronized (this) {
        if (this.wildcards == null) {
          List<Binding<?>> visible = new ArrayList<Binding<?>>();
          List<Binding<Object>> candidates = this.injector.findBindingsByType(OBJECT_TYPE_LITERAL);
          for (int i = 0, size = candidates.size(); i < size; i++) {
            Binding<?> binding = candidates.get(i);
            if (Sources.getAnnotation(binding, Hidden.class) == null)
              visible.add(binding); 
          } 
          this.wildcards = visible.isEmpty() ? NO_BINDINGS : (Binding<?>[])visible.<Binding>toArray(new Binding[visible.size()]);
        } 
      }  
    return this.wildcards;
  }
}
