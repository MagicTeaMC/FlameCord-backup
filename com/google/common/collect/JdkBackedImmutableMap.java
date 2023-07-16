package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
final class JdkBackedImmutableMap<K, V> extends ImmutableMap<K, V> {
  private final transient Map<K, V> delegateMap;
  
  private final transient ImmutableList<Map.Entry<K, V>> entries;
  
  static <K, V> ImmutableMap<K, V> create(int n, Map.Entry<K, V>[] entryArray, boolean throwIfDuplicateKeys) {
    Map.Entry[] arrayOfEntry;
    Map<K, V> delegateMap = Maps.newHashMapWithExpectedSize(n);
    Map<K, V> duplicates = null;
    int dupCount = 0;
    for (int i = 0; i < n; i++) {
      entryArray[i] = RegularImmutableMap.makeImmutable(Objects.<Map.Entry<K, V>>requireNonNull(entryArray[i]));
      K key = entryArray[i].getKey();
      V value = entryArray[i].getValue();
      V oldValue = delegateMap.put(key, value);
      if (oldValue != null) {
        if (throwIfDuplicateKeys) {
          String str1 = String.valueOf(entryArray[i].getKey()), str2 = String.valueOf(oldValue);
          throw conflictException("key", entryArray[i], (new StringBuilder(1 + String.valueOf(str1).length() + String.valueOf(str2).length())).append(str1).append("=").append(str2).toString());
        } 
        if (duplicates == null)
          duplicates = new HashMap<>(); 
        duplicates.put(key, value);
        dupCount++;
      } 
    } 
    if (duplicates != null) {
      Map.Entry[] arrayOfEntry1 = new Map.Entry[n - dupCount];
      for (int inI = 0, outI = 0; inI < n; inI++) {
        Map.Entry<K, V> entry = Objects.<Map.Entry<K, V>>requireNonNull(entryArray[inI]);
        K key = entry.getKey();
        if (duplicates.containsKey(key)) {
          V value = duplicates.get(key);
          if (value == null)
            continue; 
          entry = new ImmutableMapEntry<>(key, value);
          duplicates.put(key, null);
        } 
        arrayOfEntry1[outI++] = entry;
        continue;
      } 
      arrayOfEntry = arrayOfEntry1;
    } 
    return new JdkBackedImmutableMap<>(delegateMap, ImmutableList.asImmutableList((Object[])arrayOfEntry, n));
  }
  
  JdkBackedImmutableMap(Map<K, V> delegateMap, ImmutableList<Map.Entry<K, V>> entries) {
    this.delegateMap = delegateMap;
    this.entries = entries;
  }
  
  public int size() {
    return this.entries.size();
  }
  
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return this.delegateMap.get(key);
  }
  
  ImmutableSet<Map.Entry<K, V>> createEntrySet() {
    return new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries);
  }
  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    this.entries.forEach(e -> action.accept(e.getKey(), e.getValue()));
  }
  
  ImmutableSet<K> createKeySet() {
    return new ImmutableMapKeySet<>(this);
  }
  
  ImmutableCollection<V> createValues() {
    return new ImmutableMapValues<>(this);
  }
  
  boolean isPartialView() {
    return false;
  }
}
