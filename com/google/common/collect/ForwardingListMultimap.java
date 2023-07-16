package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.List;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingListMultimap<K, V> extends ForwardingMultimap<K, V> implements ListMultimap<K, V> {
  public List<V> get(@ParametricNullness K key) {
    return delegate().get(key);
  }
  
  @CanIgnoreReturnValue
  public List<V> removeAll(@CheckForNull Object key) {
    return delegate().removeAll(key);
  }
  
  @CanIgnoreReturnValue
  public List<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return delegate().replaceValues(key, values);
  }
  
  protected abstract ListMultimap<K, V> delegate();
}
