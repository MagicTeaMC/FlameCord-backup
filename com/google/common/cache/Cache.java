package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import javax.annotation.CheckForNull;

@DoNotMock("Use CacheBuilder.newBuilder().build()")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Cache<K, V> {
  @CheckForNull
  V getIfPresent(@CompatibleWith("K") Object paramObject);
  
  V get(K paramK, Callable<? extends V> paramCallable) throws ExecutionException;
  
  ImmutableMap<K, V> getAllPresent(Iterable<? extends Object> paramIterable);
  
  void put(K paramK, V paramV);
  
  void putAll(Map<? extends K, ? extends V> paramMap);
  
  void invalidate(@CompatibleWith("K") Object paramObject);
  
  void invalidateAll(Iterable<? extends Object> paramIterable);
  
  void invalidateAll();
  
  @CheckReturnValue
  long size();
  
  @CheckReturnValue
  CacheStats stats();
  
  @CheckReturnValue
  ConcurrentMap<K, V> asMap();
  
  void cleanUp();
}
