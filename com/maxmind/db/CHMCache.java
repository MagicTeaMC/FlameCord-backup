package com.maxmind.db;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class CHMCache implements NodeCache {
  private static final int DEFAULT_CAPACITY = 4096;
  
  private final int capacity;
  
  private final ConcurrentHashMap<CacheKey, DecodedValue> cache;
  
  private boolean cacheFull = false;
  
  public CHMCache() {
    this(4096);
  }
  
  public CHMCache(int capacity) {
    this.capacity = capacity;
    this.cache = new ConcurrentHashMap<>(capacity);
  }
  
  public DecodedValue get(CacheKey key, NodeCache.Loader loader) throws IOException {
    DecodedValue value = this.cache.get(key);
    if (value == null) {
      value = loader.load(key);
      if (!this.cacheFull)
        if (this.cache.size() < this.capacity) {
          this.cache.put(key, value);
        } else {
          this.cacheFull = true;
        }  
    } 
    return value;
  }
}
