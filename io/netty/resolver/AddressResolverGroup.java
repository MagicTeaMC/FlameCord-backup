package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.net.SocketAddress;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class AddressResolverGroup<T extends SocketAddress> implements Closeable {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(AddressResolverGroup.class);
  
  private final Map<EventExecutor, AddressResolver<T>> resolvers = new IdentityHashMap<EventExecutor, AddressResolver<T>>();
  
  private final Map<EventExecutor, GenericFutureListener<Future<Object>>> executorTerminationListeners = new IdentityHashMap<EventExecutor, GenericFutureListener<Future<Object>>>();
  
  public AddressResolver<T> getResolver(final EventExecutor executor) {
    AddressResolver<T> r;
    ObjectUtil.checkNotNull(executor, "executor");
    if (executor.isShuttingDown())
      throw new IllegalStateException("executor not accepting a task"); 
    synchronized (this.resolvers) {
      r = this.resolvers.get(executor);
      if (r == null) {
        final AddressResolver<T> newResolver;
        try {
          newResolver = newResolver(executor);
        } catch (Exception e) {
          throw new IllegalStateException("failed to create a new resolver", e);
        } 
        this.resolvers.put(executor, newResolver);
        FutureListener<Object> terminationListener = new FutureListener<Object>() {
            public void operationComplete(Future<Object> future) {
              synchronized (AddressResolverGroup.this.resolvers) {
                AddressResolverGroup.this.resolvers.remove(executor);
                AddressResolverGroup.this.executorTerminationListeners.remove(executor);
              } 
              newResolver.close();
            }
          };
        this.executorTerminationListeners.put(executor, terminationListener);
        executor.terminationFuture().addListener((GenericFutureListener)terminationListener);
        r = newResolver;
      } 
    } 
    return r;
  }
  
  public void close() {
    AddressResolver[] arrayOfAddressResolver;
    Map.Entry[] arrayOfEntry;
    synchronized (this.resolvers) {
      arrayOfAddressResolver = (AddressResolver[])this.resolvers.values().toArray((Object[])new AddressResolver[0]);
      this.resolvers.clear();
      arrayOfEntry = (Map.Entry[])this.executorTerminationListeners.entrySet().toArray((Object[])new Map.Entry[0]);
      this.executorTerminationListeners.clear();
    } 
    for (Map.Entry<EventExecutor, GenericFutureListener<Future<Object>>> entry : arrayOfEntry)
      ((EventExecutor)entry.getKey()).terminationFuture().removeListener(entry.getValue()); 
    for (AddressResolver<T> r : arrayOfAddressResolver) {
      try {
        r.close();
      } catch (Throwable t) {
        logger.warn("Failed to close a resolver:", t);
      } 
    } 
  }
  
  protected abstract AddressResolver<T> newResolver(EventExecutor paramEventExecutor) throws Exception;
}
