package io.netty.util.internal;

import io.netty.util.Recycler;

public abstract class ObjectPool<T> {
  public abstract T get();
  
  public static <T> ObjectPool<T> newPool(ObjectCreator<T> creator) {
    return new RecyclerObjectPool<T>(ObjectUtil.<ObjectCreator<T>>checkNotNull(creator, "creator"));
  }
  
  private static final class RecyclerObjectPool<T> extends ObjectPool<T> {
    private final Recycler<T> recycler = new Recycler<T>() {
        protected T newObject(Recycler.Handle<T> handle) {
          return creator.newObject((ObjectPool.Handle<T>)handle);
        }
      };
    
    RecyclerObjectPool(final ObjectPool.ObjectCreator<T> creator) {}
    
    public T get() {
      return (T)this.recycler.get();
    }
  }
  
  public static interface ObjectCreator<T> {
    T newObject(ObjectPool.Handle<T> param1Handle);
  }
  
  public static interface Handle<T> {
    void recycle(T param1T);
  }
}
