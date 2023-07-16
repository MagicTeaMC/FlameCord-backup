package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.util.internal.ObjectUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

final class AuthoritativeDnsServerCacheAdapter implements AuthoritativeDnsServerCache {
  private static final DnsRecord[] EMPTY = new DnsRecord[0];
  
  private final DnsCache cache;
  
  AuthoritativeDnsServerCacheAdapter(DnsCache cache) {
    this.cache = (DnsCache)ObjectUtil.checkNotNull(cache, "cache");
  }
  
  public DnsServerAddressStream get(String hostname) {
    List<? extends DnsCacheEntry> entries = this.cache.get(hostname, EMPTY);
    if (entries == null || entries.isEmpty())
      return null; 
    if (((DnsCacheEntry)entries.get(0)).cause() != null)
      return null; 
    List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(entries.size());
    int i = 0;
    while (true) {
      InetAddress addr = ((DnsCacheEntry)entries.get(i)).address();
      addresses.add(new InetSocketAddress(addr, 53));
      if (++i >= entries.size())
        return new SequentialDnsServerAddressStream(addresses, 0); 
    } 
  }
  
  public void cache(String hostname, InetSocketAddress address, long originalTtl, EventLoop loop) {
    if (!address.isUnresolved())
      this.cache.cache(hostname, EMPTY, address.getAddress(), originalTtl, loop); 
  }
  
  public void clear() {
    this.cache.clear();
  }
  
  public boolean clear(String hostname) {
    return this.cache.clear(hostname);
  }
}
