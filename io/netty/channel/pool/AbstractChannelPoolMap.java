package io.netty.channel.pool;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractChannelPoolMap<K, P extends ChannelPool> implements ChannelPoolMap<K, P>, Iterable<Map.Entry<K, P>>, Closeable {
  private final ConcurrentMap<K, P> map = PlatformDependent.newConcurrentHashMap();
  
  public final P get(K key) {
    ChannelPool channelPool = (ChannelPool)this.map.get(ObjectUtil.checkNotNull(key, "key"));
    if (channelPool == null) {
      channelPool = (ChannelPool)newPool(key);
      ChannelPool channelPool1 = (ChannelPool)this.map.putIfAbsent(key, (P)channelPool);
      if (channelPool1 != null) {
        poolCloseAsyncIfSupported(channelPool);
        channelPool = channelPool1;
      } 
    } 
    return (P)channelPool;
  }
  
  public final boolean remove(K key) {
    ChannelPool channelPool = (ChannelPool)this.map.remove(ObjectUtil.checkNotNull(key, "key"));
    if (channelPool != null) {
      poolCloseAsyncIfSupported(channelPool);
      return true;
    } 
    return false;
  }
  
  private Future<Boolean> removeAsyncIfSupported(K key) {
    ChannelPool channelPool = (ChannelPool)this.map.remove(ObjectUtil.checkNotNull(key, "key"));
    if (channelPool != null) {
      final Promise<Boolean> removePromise = GlobalEventExecutor.INSTANCE.newPromise();
      poolCloseAsyncIfSupported(channelPool).addListener(new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> future) throws Exception {
              if (future.isSuccess()) {
                removePromise.setSuccess(Boolean.TRUE);
              } else {
                removePromise.setFailure(future.cause());
              } 
            }
          });
      return (Future<Boolean>)removePromise;
    } 
    return GlobalEventExecutor.INSTANCE.newSucceededFuture(Boolean.FALSE);
  }
  
  private static Future<Void> poolCloseAsyncIfSupported(ChannelPool pool) {
    if (pool instanceof SimpleChannelPool)
      return ((SimpleChannelPool)pool).closeAsync(); 
    try {
      pool.close();
      return GlobalEventExecutor.INSTANCE.newSucceededFuture(null);
    } catch (Exception e) {
      return GlobalEventExecutor.INSTANCE.newFailedFuture(e);
    } 
  }
  
  public final Iterator<Map.Entry<K, P>> iterator() {
    return (Iterator<Map.Entry<K, P>>)new ReadOnlyIterator(this.map.entrySet().iterator());
  }
  
  public final int size() {
    return this.map.size();
  }
  
  public final boolean isEmpty() {
    return this.map.isEmpty();
  }
  
  public final boolean contains(K key) {
    return this.map.containsKey(ObjectUtil.checkNotNull(key, "key"));
  }
  
  protected abstract P newPool(K paramK);
  
  public final void close() {
    for (K key : this.map.keySet())
      removeAsyncIfSupported(key).syncUninterruptibly(); 
  }
}
