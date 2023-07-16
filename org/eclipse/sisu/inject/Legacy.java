package org.eclipse.sisu.inject;

import com.google.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import org.sonatype.inject.BeanEntry;
import org.sonatype.inject.Mediator;

@Deprecated
public final class Legacy<S> {
  private static final Legacy<BeanEntry<?, ?>> LEGACY_BEAN_ENTRY = as(BeanEntry.class);
  
  private final Constructor<?> proxyConstructor;
  
  private Legacy(Class<? extends S> clazz) {
    Class<?> proxyClazz = Proxy.getProxyClass(clazz.getClassLoader(), new Class[] { clazz });
    try {
      this.proxyConstructor = proxyClazz.getConstructor(new Class[] { InvocationHandler.class });
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  public <T extends S> T proxy(final S delegate) {
    try {
      return (delegate == null) ? null : (T)this.proxyConstructor.newInstance(new Object[] { new InvocationHandler() {
              public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                return method.invoke(delegate, args);
              }
            } });
    } catch (Exception e) {
      throw new IllegalStateException(e);
    } 
  }
  
  public static <S, T extends S> Legacy<S> as(Class<T> clazz) {
    return new Legacy<S>(clazz);
  }
  
  public static <Q extends Annotation, T> BeanEntry<Q, T> adapt(BeanEntry<Q, T> delegate) {
    return LEGACY_BEAN_ENTRY.<BeanEntry<Q, T>>proxy(delegate);
  }
  
  public static <Q extends Annotation, T> Iterable<BeanEntry<Q, T>> adapt(final Iterable<? extends BeanEntry<Q, T>> delegate) {
    return (Iterable)new Iterable<BeanEntry<Q, BeanEntry<Q, T>>>() {
        public Iterator<BeanEntry<Q, T>> iterator() {
          final Iterator<? extends BeanEntry<Q, T>> itr = delegate.iterator();
          return new Iterator() {
              public boolean hasNext() {
                return itr.hasNext();
              }
              
              public BeanEntry<Q, T> next() {
                return (BeanEntry)Legacy.adapt(itr.next());
              }
              
              public void remove() {
                itr.remove();
              }
            };
        }
      };
  }
  
  public static <Q extends Annotation, T> Provider<Iterable<BeanEntry<Q, T>>> adapt(final Provider<Iterable<? extends BeanEntry<Q, T>>> delegate) {
    return new Provider<Iterable<BeanEntry<Q, T>>>() {
        public Iterable<BeanEntry<Q, T>> get() {
          return (Iterable)Legacy.adapt((Iterable<? extends BeanEntry<Annotation, T>>)delegate.get());
        }
      };
  }
  
  public static <Q extends Annotation, T, W> Mediator<Q, T, W> adapt(final Mediator<Q, T, W> delegate) {
    return (delegate == null) ? null : new Mediator<Q, T, W>() {
        public void add(BeanEntry<Q, T> entry, W watcher) throws Exception {
          delegate.add(Legacy.adapt((BeanEntry)entry), watcher);
        }
        
        public void remove(BeanEntry<Q, T> entry, W watcher) throws Exception {
          delegate.remove(Legacy.adapt((BeanEntry)entry), watcher);
        }
      };
  }
}
