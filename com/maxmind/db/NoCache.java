package com.maxmind.db;

import java.io.IOException;

public class NoCache implements NodeCache {
  private static final NoCache INSTANCE = new NoCache();
  
  public DecodedValue get(CacheKey key, NodeCache.Loader loader) throws IOException {
    return loader.load(key);
  }
  
  public static NoCache getInstance() {
    return INSTANCE;
  }
}
