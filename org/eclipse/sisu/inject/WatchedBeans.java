package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;

final class WatchedBeans<Q extends Annotation, T, W> implements BindingSubscriber<T> {
  private final BeanCache<Q, T> beans = new BeanCache<Q, T>();
  
  private final Key<T> key;
  
  private final Mediator<Q, T, W> mediator;
  
  private final QualifyingStrategy strategy;
  
  private final Reference<W> watcherRef;
  
  WatchedBeans(Key<T> key, Mediator<Q, T, W> mediator, W watcher) {
    this.key = key;
    this.mediator = mediator;
    this.strategy = QualifyingStrategy.selectFor(key);
    this.watcherRef = new WeakReference<W>(watcher);
  }
  
  public TypeLiteral<T> type() {
    return this.key.getTypeLiteral();
  }
  
  public void add(Binding<T> binding, int rank) {
    Annotation annotation = this.strategy.qualifies(this.key, binding);
    if (annotation != null) {
      W watcher = this.watcherRef.get();
      if (watcher != null) {
        BeanEntry<Q, T> bean = this.beans.create((Q)annotation, binding, rank);
        try {
          this.mediator.add(bean, watcher);
        } catch (Throwable e) {
          Logs.catchThrowable(e);
          Logs.warn("Problem adding: <> to: " + detail(watcher), bean, e);
        } 
      } 
    } 
  }
  
  public void remove(Binding<T> binding) {
    BeanEntry<Q, T> bean = this.beans.remove(binding);
    if (bean != null) {
      W watcher = this.watcherRef.get();
      if (watcher != null)
        try {
          this.mediator.remove(bean, watcher);
        } catch (Throwable e) {
          Logs.catchThrowable(e);
          Logs.warn("Problem removing: <> from: " + detail(watcher), bean, e);
        }  
    } 
  }
  
  public Iterable<Binding<T>> bindings() {
    return this.beans.bindings();
  }
  
  private String detail(Object watcher) {
    return String.valueOf(Logs.identityToString(watcher)) + " via: " + Logs.identityToString(this.mediator);
  }
}
