package org.apache.commons.lang3.concurrent;

public abstract class LazyInitializer<T> implements ConcurrentInitializer<T> {
  private static final Object NO_INIT = new Object();
  
  private volatile T object = (T)NO_INIT;
  
  public T get() throws ConcurrentException {
    T result = this.object;
    if (result == NO_INIT)
      synchronized (this) {
        result = this.object;
        if (result == NO_INIT)
          this.object = result = initialize(); 
      }  
    return result;
  }
  
  protected abstract T initialize() throws ConcurrentException;
}
