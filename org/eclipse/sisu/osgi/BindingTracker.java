package org.eclipse.sisu.osgi;

import java.util.Collection;
import org.eclipse.sisu.inject.BindingSubscriber;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.inject.Weak;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

final class BindingTracker<T> extends ServiceTracker<T, ServiceBinding<T>> {
  private final Collection<BindingSubscriber<T>> subscribers = Weak.elements();
  
  private final String clazzName;
  
  private final int maxRank;
  
  private boolean isOpen;
  
  BindingTracker(BundleContext context, int maxRank, String clazzName) {
    super(context, clazzName, null);
    this.clazzName = clazzName;
    this.maxRank = maxRank;
  }
  
  public void subscribe(BindingSubscriber<T> subscriber) {
    synchronized (this.subscribers) {
      openIfNecessary();
      for (ServiceBinding<T> binding : (Iterable<ServiceBinding<T>>)getTracked().values()) {
        if (binding.isCompatibleWith(subscriber))
          subscriber.add(binding, binding.rank()); 
      } 
      this.subscribers.add(subscriber);
    } 
  }
  
  public void unsubscribe(BindingSubscriber<T> subscriber) {
    synchronized (this.subscribers) {
      if (this.subscribers.remove(subscriber))
        for (ServiceBinding<T> binding : (Iterable<ServiceBinding<T>>)getTracked().values())
          subscriber.remove(binding);  
      closeIfNecessary();
    } 
  }
  
  public ServiceBinding<T> addingService(ServiceReference<T> reference) {
    ServiceBinding<T> binding;
    try {
      binding = new ServiceBinding<T>(this.context, this.clazzName, this.maxRank, reference);
    } catch (Exception e) {
      Logs.warn("Problem subscribing to service: {}", reference, e);
      return null;
    } 
    synchronized (this.subscribers) {
      for (BindingSubscriber<T> subscriber : this.subscribers) {
        if (binding.isCompatibleWith(subscriber))
          subscriber.add(binding, binding.rank()); 
      } 
      closeIfNecessary();
    } 
    return binding;
  }
  
  public void removedService(ServiceReference<T> reference, ServiceBinding<T> binding) {
    synchronized (this.subscribers) {
      for (BindingSubscriber<T> subscriber : this.subscribers)
        subscriber.remove(binding); 
      closeIfNecessary();
    } 
    super.removedService(reference, binding);
  }
  
  private void openIfNecessary() {
    if (!this.isOpen) {
      open(true);
      Logs.trace("Started tracking services: {}", this.filter, null);
      this.isOpen = true;
    } 
  }
  
  private void closeIfNecessary() {
    if (this.isOpen && this.subscribers.isEmpty()) {
      this.isOpen = false;
      Logs.trace("Stopped tracking services: {}", this.filter, null);
      close();
    } 
  }
}
