package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.util.Map;
import java.util.Objects;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
final class JdkBackedImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
  private final transient ImmutableList<Map.Entry<K, V>> entries;
  
  private final Map<K, V> forwardDelegate;
  
  private final Map<V, K> backwardDelegate;
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  private transient JdkBackedImmutableBiMap<V, K> inverse;
  
  @VisibleForTesting
  static <K, V> ImmutableBiMap<K, V> create(int n, Map.Entry<K, V>[] entryArray) {
    Map<K, V> forwardDelegate = Maps.newHashMapWithExpectedSize(n);
    Map<V, K> backwardDelegate = Maps.newHashMapWithExpectedSize(n);
    for (int i = 0; i < n; i++) {
      Map.Entry<K, V> e = RegularImmutableMap.makeImmutable(Objects.<Map.Entry<K, V>>requireNonNull(entryArray[i]));
      entryArray[i] = e;
      V oldValue = forwardDelegate.putIfAbsent(e.getKey(), e.getValue());
      if (oldValue != null) {
        String str1 = String.valueOf(e.getKey()), str2 = String.valueOf(oldValue);
        throw conflictException("key", (new StringBuilder(1 + String.valueOf(str1).length() + String.valueOf(str2).length())).append(str1).append("=").append(str2).toString(), entryArray[i]);
      } 
      K oldKey = backwardDelegate.putIfAbsent(e.getValue(), e.getKey());
      if (oldKey != null) {
        String str1 = String.valueOf(oldKey), str2 = String.valueOf(e.getValue());
        throw conflictException("value", (new StringBuilder(1 + String.valueOf(str1).length() + String.valueOf(str2).length())).append(str1).append("=").append(str2).toString(), entryArray[i]);
      } 
    } 
    ImmutableList<Map.Entry<K, V>> entryList = ImmutableList.asImmutableList((Object[])entryArray, n);
    return new JdkBackedImmutableBiMap<>(entryList, forwardDelegate, backwardDelegate);
  }
  
  private JdkBackedImmutableBiMap(ImmutableList<Map.Entry<K, V>> entries, Map<K, V> forwardDelegate, Map<V, K> backwardDelegate) {
    this.entries = entries;
    this.forwardDelegate = forwardDelegate;
    this.backwardDelegate = backwardDelegate;
  }
  
  public int size() {
    return this.entries.size();
  }
  
  public ImmutableBiMap<V, K> inverse() {
    JdkBackedImmutableBiMap<V, K> result = this.inverse;
    if (result == null) {
      this.inverse = result = new JdkBackedImmutableBiMap(new InverseEntries(), this.backwardDelegate, this.forwardDelegate);
      result.inverse = this;
    } 
    return result;
  }
  
  private final class InverseEntries extends ImmutableList<Map.Entry<V, K>> {
    private InverseEntries() {}
    
    public Map.Entry<V, K> get(int index) {
      Map.Entry<K, V> entry = JdkBackedImmutableBiMap.this.entries.get(index);
      return Maps.immutableEntry(entry.getValue(), entry.getKey());
    }
    
    boolean isPartialView() {
      return false;
    }
    
    public int size() {
      return JdkBackedImmutableBiMap.this.entries.size();
    }
  }
  
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return this.forwardDelegate.get(key);
  }
  
  ImmutableSet<Map.Entry<K, V>> createEntrySet() {
    return new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries);
  }
  
  ImmutableSet<K> createKeySet() {
    return new ImmutableMapKeySet<>(this);
  }
  
  boolean isPartialView() {
    return false;
  }
}
