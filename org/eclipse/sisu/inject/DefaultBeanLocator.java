package org.eclipse.sisu.inject;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;

@Singleton
public final class DefaultBeanLocator implements MutableBeanLocator {
  private final RankedSequence<BindingPublisher> publishers = new RankedSequence<BindingPublisher>();
  
  private final ConcurrentMap<Long, RankedBindings> cachedBindings = Weak.concurrentValues(256, 8);
  
  private final Map<WatchedBeans, Object> cachedWatchers = Weak.values();
  
  private final ImplicitBindings implicitBindings = new ImplicitBindings(this.publishers);
  
  private final Long[] typeIdHolder = new Long[1];
  
  public Iterable<BeanEntry> locate(Key<?> key) {
    TypeLiteral<?> type = key.getTypeLiteral();
    RankedBindings<?> bindings = fetchBindings(type, null);
    if (bindings == null)
      synchronized (this.cachedBindings) {
        bindings = fetchBindings(type, this.typeIdHolder);
        if (bindings == null) {
          bindings = new RankedBindings(type, this.publishers);
          this.cachedBindings.put(this.typeIdHolder[0], bindings);
        } 
      }  
    boolean isImplicit = (key.getAnnotationType() == null && TypeArguments.isImplicit(type));
    return (Iterable)new LocatedBeans<Annotation, Object>(key, bindings, isImplicit ? this.implicitBindings : null);
  }
  
  public synchronized void watch(Key<?> key, Mediator<Annotation, ?, Object> mediator, Object watcher) {
    WatchedBeans<Annotation, Object, Object> beans = new WatchedBeans<Annotation, Object, Object>(key, mediator, watcher);
    for (BindingPublisher p : publishers())
      p.subscribe(beans); 
    this.cachedWatchers.put(beans, watcher);
  }
  
  public synchronized boolean add(BindingPublisher publisher) {
    if (this.publishers.contains(publisher))
      return false; 
    Logs.trace("Add publisher: {}", publisher, null);
    synchronized (this.cachedBindings) {
      int rank = publisher.maxBindingRank();
      this.publishers.insert(publisher, rank);
      for (RankedBindings bindings : this.cachedBindings.values())
        bindings.add(publisher, rank); 
    } 
    for (WatchedBeans<?> beans : (Iterable<WatchedBeans<?>>)new ArrayList(this.cachedWatchers.keySet()))
      publisher.subscribe(beans); 
    return true;
  }
  
  public synchronized boolean remove(BindingPublisher publisher) {
    BindingPublisher oldPublisher;
    synchronized (this.cachedBindings) {
      oldPublisher = this.publishers.remove(publisher);
      if (oldPublisher == null)
        return false; 
      Logs.trace("Remove publisher: {}", oldPublisher, null);
      for (RankedBindings bindings : this.cachedBindings.values())
        bindings.remove(oldPublisher); 
    } 
    for (WatchedBeans<?> beans : this.cachedWatchers.keySet())
      oldPublisher.unsubscribe(beans); 
    ((MildConcurrentValues)this.cachedBindings).compact();
    return true;
  }
  
  public Iterable<BindingPublisher> publishers() {
    return this.publishers.snapshot();
  }
  
  public synchronized void clear() {
    for (BindingPublisher p : publishers())
      remove(p); 
  }
  
  public void add(Injector injector, int rank) {
    add(new InjectorBindings(injector, new DefaultRankingFunction(rank)));
  }
  
  public void remove(Injector injector) {
    remove(new InjectorBindings(injector, null));
  }
  
  private RankedBindings fetchBindings(TypeLiteral type, Long[] idReturn) {
    int loaderHash = System.identityHashCode(type.getRawType().getClassLoader());
    long id = type.hashCode() << 32L | 0xFFFFFFFFL & loaderHash;
    RankedBindings result;
    while ((result = this.cachedBindings.get(Long.valueOf(id))) != null && !type.equals(result.type()))
      id++; 
    if (idReturn != null)
      idReturn[0] = Long.valueOf(id); 
    return result;
  }
  
  @Inject
  void autoPublish(Injector injector) {
    staticAutoPublish(this, injector);
  }
  
  @Inject
  static void staticAutoPublish(MutableBeanLocator locator, Injector injector) {
    locator.add(new InjectorBindings(injector));
  }
}
