package com.mysql.cj;

public interface CacheAdapterFactory<K, V> {
  CacheAdapter<K, V> getInstance(Object paramObject, String paramString, int paramInt1, int paramInt2);
}
