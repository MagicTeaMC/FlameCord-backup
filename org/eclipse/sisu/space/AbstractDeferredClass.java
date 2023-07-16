package org.eclipse.sisu.space;

import com.google.inject.Injector;
import javax.inject.Inject;
import org.eclipse.sisu.inject.DeferredClass;
import org.eclipse.sisu.inject.DeferredProvider;
import org.eclipse.sisu.inject.Logs;

abstract class AbstractDeferredClass<T> implements DeferredClass<T>, DeferredProvider<T> {
  @Inject
  private Injector injector;
  
  public final DeferredProvider<T> asProvider() {
    return this;
  }
  
  public final T get() {
    try {
      return (T)this.injector.getInstance(load());
    } catch (Throwable e) {
      Logs.catchThrowable(e);
      try {
        Logs.warn("Error injecting: {}", getName(), e);
      } finally {
        Logs.throwUnchecked(e);
      } 
      return null;
    } 
  }
  
  public final DeferredClass<T> getImplementationClass() {
    return this;
  }
}
