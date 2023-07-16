package com.mysql.cj;

import com.mysql.cj.util.LRUCache;
import java.util.Set;

public class PerConnectionLRUFactory implements CacheAdapterFactory<String, QueryInfo> {
  public CacheAdapter<String, QueryInfo> getInstance(Object syncMutex, String url, int cacheMaxSize, int maxKeySize) {
    return new PerConnectionLRU(syncMutex, cacheMaxSize, maxKeySize);
  }
  
  class PerConnectionLRU implements CacheAdapter<String, QueryInfo> {
    private final int cacheSqlLimit;
    
    private final LRUCache<String, QueryInfo> cache;
    
    private final Object syncMutex;
    
    protected PerConnectionLRU(Object syncMutex, int cacheMaxSize, int maxKeySize) {
      int cacheSize = cacheMaxSize;
      this.cacheSqlLimit = maxKeySize;
      this.cache = new LRUCache(cacheSize);
      this.syncMutex = syncMutex;
    }
    
    public QueryInfo get(String key) {
      if (key == null || key.length() > this.cacheSqlLimit)
        return null; 
      synchronized (this.syncMutex) {
        return (QueryInfo)this.cache.get(key);
      } 
    }
    
    public void put(String key, QueryInfo value) {
      if (key == null || key.length() > this.cacheSqlLimit)
        return; 
      synchronized (this.syncMutex) {
        this.cache.put(key, value);
      } 
    }
    
    public void invalidate(String key) {
      synchronized (this.syncMutex) {
        this.cache.remove(key);
      } 
    }
    
    public void invalidateAll(Set<String> keys) {
      synchronized (this.syncMutex) {
        for (String key : keys)
          this.cache.remove(key); 
      } 
    }
    
    public void invalidateAll() {
      synchronized (this.syncMutex) {
        this.cache.clear();
      } 
    }
  }
}
