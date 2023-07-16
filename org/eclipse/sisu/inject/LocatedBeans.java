package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Key;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.eclipse.sisu.BeanEntry;

final class LocatedBeans<Q extends Annotation, T> implements Iterable<BeanEntry<Q, T>> {
  final Key<T> key;
  
  final RankedBindings<T> explicitBindings;
  
  final ImplicitBindings implicitBindings;
  
  final QualifyingStrategy strategy;
  
  final BeanCache<Q, T> beans;
  
  LocatedBeans(Key<T> key, RankedBindings<T> explicitBindings, ImplicitBindings implicitBindings) {
    this.key = key;
    this.explicitBindings = explicitBindings;
    this.implicitBindings = implicitBindings;
    this.strategy = QualifyingStrategy.selectFor(key);
    this.beans = explicitBindings.newBeanCache();
  }
  
  public Iterator<BeanEntry<Q, T>> iterator() {
    return new Itr();
  }
  
  final class Itr implements Iterator<BeanEntry<Q, T>> {
    private final RankedBindings<T>.Itr itr = LocatedBeans.this.explicitBindings.iterator();
    
    private final Map<Binding<T>, BeanEntry<Q, T>> readCache = LocatedBeans.this.beans.flush();
    
    private boolean checkImplicitBindings = (LocatedBeans.this.implicitBindings != null);
    
    private BeanEntry<Q, T> nextBean;
    
    public boolean hasNext() {
      if (this.nextBean != null)
        return true; 
      while (this.itr.hasNext()) {
        Binding<T> binding = this.itr.next();
        if (this.readCache != null && (this.nextBean = this.readCache.get(binding)) != null)
          return true; 
        Annotation annotation = LocatedBeans.this.strategy.qualifies(LocatedBeans.this.key, binding);
        if (annotation != null) {
          this.nextBean = LocatedBeans.this.beans.create((Q)annotation, binding, this.itr.rank());
          return true;
        } 
      } 
      if (this.checkImplicitBindings) {
        Binding<T> binding = LocatedBeans.this.implicitBindings.get(LocatedBeans.this.key.getTypeLiteral());
        if (binding != null) {
          this.nextBean = LocatedBeans.this.beans.create((Q)QualifyingStrategy.DEFAULT_QUALIFIER, binding, -2147483648);
          return true;
        } 
      } 
      return false;
    }
    
    public BeanEntry<Q, T> next() {
      if (hasNext()) {
        this.checkImplicitBindings = false;
        BeanEntry<Q, T> bean = this.nextBean;
        this.nextBean = null;
        return bean;
      } 
      throw new NoSuchElementException();
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
