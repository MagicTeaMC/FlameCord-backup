package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public final class DefaultDnsCnameCache implements DnsCnameCache {
  private final int minTtl;
  
  private final int maxTtl;
  
  private final Cache<String> cache = new Cache<String>() {
      protected boolean shouldReplaceAll(String entry) {
        return true;
      }
      
      protected boolean equals(String entry, String otherEntry) {
        return AsciiString.contentEqualsIgnoreCase(entry, otherEntry);
      }
    };
  
  public DefaultDnsCnameCache() {
    this(0, Cache.MAX_SUPPORTED_TTL_SECS);
  }
  
  public DefaultDnsCnameCache(int minTtl, int maxTtl) {
    this.minTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(minTtl, "minTtl"));
    this.maxTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositive(maxTtl, "maxTtl"));
    if (minTtl > maxTtl)
      throw new IllegalArgumentException("minTtl: " + minTtl + ", maxTtl: " + maxTtl + " (expected: 0 <= minTtl <= maxTtl)"); 
  }
  
  public String get(String hostname) {
    List<? extends String> cached = this.cache.get((String)ObjectUtil.checkNotNull(hostname, "hostname"));
    if (cached == null || cached.isEmpty())
      return null; 
    return cached.get(0);
  }
  
  public void cache(String hostname, String cname, long originalTtl, EventLoop loop) {
    ObjectUtil.checkNotNull(hostname, "hostname");
    ObjectUtil.checkNotNull(cname, "cname");
    ObjectUtil.checkNotNull(loop, "loop");
    this.cache.cache(hostname, cname, Math.max(this.minTtl, (int)Math.min(this.maxTtl, originalTtl)), loop);
  }
  
  public void clear() {
    this.cache.clear();
  }
  
  public boolean clear(String hostname) {
    return this.cache.clear((String)ObjectUtil.checkNotNull(hostname, "hostname"));
  }
  
  int minTtl() {
    return this.minTtl;
  }
  
  int maxTtl() {
    return this.maxTtl;
  }
}
