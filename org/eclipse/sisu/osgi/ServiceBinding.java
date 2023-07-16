package org.eclipse.sisu.osgi;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.name.Names;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ElementVisitor;
import java.lang.annotation.Annotation;
import javax.inject.Singleton;
import org.eclipse.sisu.inject.BindingSubscriber;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

final class ServiceBinding<T> implements Binding<T>, Provider<T> {
  private final Key<T> key;
  
  private final T instance;
  
  private final int rank;
  
  ServiceBinding(BundleContext context, String clazzName, int maxRank, ServiceReference<T> reference) throws ClassNotFoundException {
    Class<T> clazz = reference.getBundle().loadClass(clazzName);
    Object name = reference.getProperty("name");
    if (name instanceof String && ((String)name).length() > 0) {
      this.key = Key.get(clazz, (Annotation)Names.named((String)name));
    } else {
      this.key = Key.get(clazz);
    } 
    this.instance = (T)context.getService(reference);
    if (maxRank > Integer.MIN_VALUE) {
      int serviceRanking = getServiceRanking(reference);
      this.rank = (serviceRanking < maxRank) ? serviceRanking : maxRank;
    } else {
      this.rank = Integer.MIN_VALUE;
    } 
  }
  
  public Key<T> getKey() {
    return this.key;
  }
  
  public Provider<T> getProvider() {
    return this;
  }
  
  public T get() {
    return this.instance;
  }
  
  public Object getSource() {
    return "OSGi service registry";
  }
  
  public void applyTo(Binder binder) {}
  
  public <V> V acceptVisitor(ElementVisitor<V> visitor) {
    return (V)visitor.visit(this);
  }
  
  public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
    return null;
  }
  
  public <V> V acceptScopingVisitor(BindingScopingVisitor<V> visitor) {
    return (V)visitor.visitScopeAnnotation(Singleton.class);
  }
  
  boolean isCompatibleWith(BindingSubscriber<T> subscriber) {
    return this.key.getTypeLiteral().getRawType().equals(subscriber.type().getRawType());
  }
  
  int rank() {
    return this.rank;
  }
  
  private static int getServiceRanking(ServiceReference<?> reference) {
    Object ranking = reference.getProperty("service.ranking");
    return (ranking instanceof Integer) ? ((Integer)ranking).intValue() : 0;
  }
}
