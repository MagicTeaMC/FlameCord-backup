package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
import java.util.Collection;
import java.util.Iterator;

final class RankedBindings<T> implements Iterable<Binding<T>>, BindingSubscriber<T> {
  final transient RankedSequence<Binding<T>> bindings = new RankedSequence<Binding<T>>();
  
  final transient TypeLiteral<T> type;
  
  final transient RankedSequence<BindingPublisher> pendingPublishers;
  
  final Collection<BeanCache<?, T>> cachedBeans = Weak.elements();
  
  RankedBindings(TypeLiteral<T> type, RankedSequence<BindingPublisher> publishers) {
    this.type = type;
    this.pendingPublishers = new RankedSequence<BindingPublisher>(publishers);
  }
  
  public TypeLiteral<T> type() {
    return this.type;
  }
  
  public void add(Binding<T> binding, int rank) {
    this.bindings.insert(binding, rank);
  }
  
  public void remove(Binding<T> binding) {
    if (this.bindings.removeThis(binding))
      synchronized (this.cachedBeans) {
        for (BeanCache<?, T> beans : this.cachedBeans)
          beans.remove(binding); 
      }  
  }
  
  public Iterable<Binding<T>> bindings() {
    return this.bindings.snapshot();
  }
  
  public Itr iterator() {
    return new Itr();
  }
  
  <Q extends java.lang.annotation.Annotation> BeanCache<Q, T> newBeanCache() {
    BeanCache<Q, T> beans = new BeanCache<Q, T>();
    synchronized (this.cachedBeans) {
      this.cachedBeans.add(beans);
    } 
    return beans;
  }
  
  void add(BindingPublisher publisher, int rank) {
    this.pendingPublishers.insert(publisher, rank);
  }
  
  void remove(BindingPublisher publisher) {
    synchronized (publisher) {
      if (!this.pendingPublishers.removeThis(publisher))
        publisher.unsubscribe(this); 
    } 
  }
  
  final class Itr implements Iterator<Binding<T>> {
    private final RankedSequence<Binding<T>>.Itr itr = RankedBindings.this.bindings.iterator();
    
    public boolean hasNext() {
      BindingPublisher publisher = RankedBindings.this.pendingPublishers.peek();
      while (publisher != null && !this.itr.hasNext(publisher.maxBindingRank())) {
        synchronized (publisher) {
          if (publisher == RankedBindings.this.pendingPublishers.peek()) {
            publisher.subscribe(RankedBindings.this);
            RankedBindings.this.pendingPublishers.removeThis(publisher);
          } 
        } 
        publisher = RankedBindings.this.pendingPublishers.peek();
      } 
      return this.itr.hasNext();
    }
    
    public Binding<T> next() {
      return (Binding<T>)this.itr.next();
    }
    
    public int rank() {
      return this.itr.rank();
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
