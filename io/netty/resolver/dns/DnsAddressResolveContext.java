package io.netty.resolver.dns;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.concurrent.Promise;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

final class DnsAddressResolveContext extends DnsResolveContext<InetAddress> {
  private final DnsCache resolveCache;
  
  private final AuthoritativeDnsServerCache authoritativeDnsServerCache;
  
  private final boolean completeEarlyIfPossible;
  
  DnsAddressResolveContext(DnsNameResolver parent, Channel channel, Promise<?> originalPromise, String hostname, DnsRecord[] additionals, DnsServerAddressStream nameServerAddrs, int allowedQueries, DnsCache resolveCache, AuthoritativeDnsServerCache authoritativeDnsServerCache, boolean completeEarlyIfPossible) {
    super(parent, channel, originalPromise, hostname, 1, parent
        .resolveRecordTypes(), additionals, nameServerAddrs, allowedQueries);
    this.resolveCache = resolveCache;
    this.authoritativeDnsServerCache = authoritativeDnsServerCache;
    this.completeEarlyIfPossible = completeEarlyIfPossible;
  }
  
  DnsResolveContext<InetAddress> newResolverContext(DnsNameResolver parent, Channel channel, Promise<?> originalPromise, String hostname, int dnsClass, DnsRecordType[] expectedTypes, DnsRecord[] additionals, DnsServerAddressStream nameServerAddrs, int allowedQueries) {
    return new DnsAddressResolveContext(parent, channel, originalPromise, hostname, additionals, nameServerAddrs, allowedQueries, this.resolveCache, this.authoritativeDnsServerCache, this.completeEarlyIfPossible);
  }
  
  InetAddress convertRecord(DnsRecord record, String hostname, DnsRecord[] additionals, EventLoop eventLoop) {
    return DnsAddressDecoder.decodeAddress(record, hostname, this.parent.isDecodeIdn());
  }
  
  List<InetAddress> filterResults(List<InetAddress> unfiltered) {
    Collections.sort(unfiltered, PreferredAddressTypeComparator.comparator(this.parent.preferredAddressType()));
    return unfiltered;
  }
  
  boolean isCompleteEarly(InetAddress resolved) {
    return (this.completeEarlyIfPossible && this.parent.preferredAddressType().addressType() == resolved.getClass());
  }
  
  boolean isDuplicateAllowed() {
    return false;
  }
  
  void cache(String hostname, DnsRecord[] additionals, DnsRecord result, InetAddress convertedResult) {
    this.resolveCache.cache(hostname, additionals, convertedResult, result.timeToLive(), channel().eventLoop());
  }
  
  void cache(String hostname, DnsRecord[] additionals, UnknownHostException cause) {
    this.resolveCache.cache(hostname, additionals, cause, channel().eventLoop());
  }
  
  void doSearchDomainQuery(String hostname, Promise<List<InetAddress>> nextPromise) {
    if (!DnsNameResolver.doResolveAllCached(hostname, this.additionals, nextPromise, this.resolveCache, this.parent
        .resolvedInternetProtocolFamiliesUnsafe()))
      super.doSearchDomainQuery(hostname, nextPromise); 
  }
  
  DnsCache resolveCache() {
    return this.resolveCache;
  }
  
  AuthoritativeDnsServerCache authoritativeDnsServerCache() {
    return this.authoritativeDnsServerCache;
  }
}
