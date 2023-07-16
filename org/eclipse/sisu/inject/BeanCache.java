package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.sisu.BeanEntry;

final class BeanCache<Q extends Annotation, T> extends AtomicReference<Object> {
  private static final long serialVersionUID = 1L;
  
  private Map<Binding<T>, BeanEntry<Q, T>> readCache;
  
  private volatile boolean mutated;
  
  public BeanEntry<Q, T> create(Q qualifier, Binding<T> binding, int rank) {
    LazyBeanEntry<Q, T> newBean;
    Object o;
    Object n;
    do {
      o = get();
      if (o == null) {
        n = newBean = new LazyBeanEntry<Q, T>(qualifier, binding, rank);
      } else if (o instanceof LazyBeanEntry) {
        LazyBeanEntry<Q, T> oldBean = (LazyBeanEntry)o;
        if (binding == oldBean.binding)
          return oldBean; 
        n = createMap(oldBean, newBean = new LazyBeanEntry<Q, T>(qualifier, binding, rank));
      } else {
        synchronized (this) {
          Map<Binding, LazyBeanEntry> map = (Map<Binding, LazyBeanEntry>)o;
          if ((newBean = map.get(binding)) == null) {
            map.put(binding, newBean = new LazyBeanEntry<Q, T>(qualifier, binding, rank));
            this.mutated = true;
          } 
          return newBean;
        } 
      } 
    } while (!compareAndSet(o, n));
    if (n instanceof IdentityHashMap)
      this.mutated = true; 
    return newBean;
  }
  
  public Map<Binding<T>, BeanEntry<Q, T>> flush() {
    if (this.mutated)
      synchronized (this) {
        if (this.mutated) {
          this.readCache = (Map<Binding<T>, BeanEntry<Q, T>>)((IdentityHashMap)get()).clone();
          this.mutated = false;
        } 
      }  
    return this.readCache;
  }
  
  public Iterable<Binding<T>> bindings() {
    Object o = get();
    if (o == null)
      return Collections.EMPTY_SET; 
    if (o instanceof LazyBeanEntry)
      return Collections.singleton(((LazyBeanEntry)o).binding); 
    synchronized (this) {
      return new ArrayList<Binding<T>>(((Map)o).keySet());
    } 
  }
  
  public BeanEntry<Q, T> remove(Binding<T> binding) {
    LazyBeanEntry<Q, T> oldBean;
    Object o;
    Object n;
    do {
      o = get();
      if (o == null)
        return null; 
      if (o instanceof LazyBeanEntry) {
        oldBean = (LazyBeanEntry)o;
        if (binding != oldBean.binding)
          return null; 
        n = null;
      } else {
        synchronized (this) {
          oldBean = (LazyBeanEntry)((Map)o).remove(binding);
          if (oldBean != null)
            this.mutated = true; 
          return oldBean;
        } 
      } 
    } while (!compareAndSet(o, n));
    return oldBean;
  }
  
  private static Map createMap(LazyBeanEntry one, LazyBeanEntry two) {
    Map<Object, Object> map = new IdentityHashMap<Object, Object>(10);
    map.put(one.binding, one);
    map.put(two.binding, two);
    return map;
  }
}
