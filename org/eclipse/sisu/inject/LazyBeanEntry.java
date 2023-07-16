package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Scopes;
import com.google.inject.name.Named;
import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Provider;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Description;

final class LazyBeanEntry<Q extends Annotation, T> implements BeanEntry<Q, T> {
  private final Q qualifier;
  
  final Binding<T> binding;
  
  private final Provider<T> lazyValue;
  
  private final int rank;
  
  LazyBeanEntry(Q qualifier, Binding<T> binding, int rank) {
    if (qualifier != null && Named.class == qualifier.annotationType()) {
      this.qualifier = (Q)new JsrNamed((Named)qualifier);
    } else {
      this.qualifier = qualifier;
    } 
    this.binding = binding;
    this.rank = rank;
    if (Scopes.isSingleton(binding)) {
      this.lazyValue = (Provider<T>)binding.getProvider();
    } else {
      this.lazyValue = Guice4.lazy(binding);
    } 
  }
  
  public Q getKey() {
    return this.qualifier;
  }
  
  public T getValue() {
    return (T)this.lazyValue.get();
  }
  
  public T setValue(T value) {
    throw new UnsupportedOperationException();
  }
  
  public Provider<T> getProvider() {
    return (Provider<T>)this.binding.getProvider();
  }
  
  public String getDescription() {
    Description description = Sources.<Description>getAnnotation(this.binding, Description.class);
    return (description != null) ? description.value() : null;
  }
  
  public Class<T> getImplementationClass() {
    return (Class)Implementations.find(this.binding);
  }
  
  public Object getSource() {
    return Guice4.getDeclaringSource(this.binding);
  }
  
  public int getRank() {
    return this.rank;
  }
  
  public String toString() {
    StringBuilder buf = (new StringBuilder()).append(getKey()).append('=');
    try {
      Class<T> impl = getImplementationClass();
      buf.append((impl != null) ? impl : getProvider());
    } catch (RuntimeException e) {
      buf.append(e);
    } 
    return buf.toString();
  }
  
  private static final class JsrNamed implements Named, Named {
    private final String value;
    
    JsrNamed(Named named) {
      this.value = named.value();
    }
    
    public String value() {
      return this.value;
    }
    
    public Class<? extends Annotation> annotationType() {
      return (Class)Named.class;
    }
    
    public int hashCode() {
      return 127 * "value".hashCode() ^ this.value.hashCode();
    }
    
    public boolean equals(Object rhs) {
      if (this == rhs)
        return true; 
      if (rhs instanceof Named)
        return this.value.equals(((Named)rhs).value()); 
      if (rhs instanceof Named)
        return this.value.equals(((Named)rhs).value()); 
      return false;
    }
    
    public String toString() {
      return "@" + Named.class.getName() + "(value=" + this.value + ")";
    }
  }
}
