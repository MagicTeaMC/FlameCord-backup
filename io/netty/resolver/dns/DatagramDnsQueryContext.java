package io.netty.resolver.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.net.InetSocketAddress;

final class DatagramDnsQueryContext extends DnsQueryContext {
  DatagramDnsQueryContext(Channel channel, Future<? extends Channel> channelReadyFuture, InetSocketAddress nameServerAddr, DnsQueryContextManager queryContextManager, int maxPayLoadSize, boolean recursionDesired, DnsQuestion question, DnsRecord[] additionals, Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise) {
    super(channel, channelReadyFuture, nameServerAddr, queryContextManager, maxPayLoadSize, recursionDesired, question, additionals, promise);
  }
  
  protected DnsQuery newQuery(int id, InetSocketAddress nameServerAddr) {
    return (DnsQuery)new DatagramDnsQuery(null, nameServerAddr, id);
  }
  
  protected String protocol() {
    return "UDP";
  }
}