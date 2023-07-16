package com.maxmind.db;

import java.io.IOException;

public interface NodeCache {
  DecodedValue get(CacheKey paramCacheKey, Loader paramLoader) throws IOException;
  
  public static interface Loader {
    DecodedValue load(CacheKey param1CacheKey) throws IOException;
  }
}
