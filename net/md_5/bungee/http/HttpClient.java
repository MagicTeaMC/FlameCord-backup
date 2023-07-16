package net.md_5.bungee.http;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.dns.DefaultDnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsAddressResolverGroup;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.netty.PipelineUtils;

public class HttpClient {
  public static final int TIMEOUT = 5000;
  
  private static final Cache<String, InetAddress> addressCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build();
  
  private static final DnsAddressResolverGroup dnsResolverGroup = new DnsAddressResolverGroup(
      PipelineUtils.getDatagramChannel(), (DnsServerAddressStreamProvider)DefaultDnsServerAddressStreamProvider.INSTANCE);
  
  public static void get(String url, EventLoop eventLoop, final Callback<String> callback) {
    Preconditions.checkNotNull(url, "url");
    Preconditions.checkNotNull(eventLoop, "eventLoop");
    Preconditions.checkNotNull(callback, "callBack");
    final URI uri = URI.create(url);
    Preconditions.checkNotNull(uri.getScheme(), "scheme");
    Preconditions.checkNotNull(uri.getHost(), "host");
    boolean ssl = uri.getScheme().equals("https");
    int port = uri.getPort();
    if (port == -1)
      switch (uri.getScheme()) {
        case "http":
          port = 80;
          break;
        case "https":
          port = 443;
          break;
        default:
          throw new IllegalArgumentException("Unknown scheme " + uri.getScheme());
      }  
    ChannelFutureListener future = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture future) throws Exception {
          if (future.isSuccess()) {
            String path = uri.getRawPath() + ((uri.getRawQuery() == null) ? "" : ("?" + uri.getRawQuery()));
            DefaultHttpRequest defaultHttpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
            defaultHttpRequest.headers().set((CharSequence)HttpHeaderNames.HOST, uri.getHost());
            future.channel().writeAndFlush(defaultHttpRequest);
          } else {
            HttpClient.addressCache.invalidate(uri.getHost());
            callback.done(null, future.cause());
          } 
        }
      };
    if (ProxyServer.getInstance().getConfig().isUseNettyDnsResolver()) {
      getWithNettyResolver(eventLoop, uri, port, future, callback, ssl);
    } else {
      getWithDefaultResolver(eventLoop, uri, port, future, callback, ssl);
    } 
  }
  
  private static void getWithNettyResolver(EventLoop eventLoop, URI uri, int port, ChannelFutureListener future, Callback<String> callback, boolean ssl) {
    InetSocketAddress address = InetSocketAddress.createUnresolved(uri.getHost(), port);
    ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).channelFactory(PipelineUtils.getChannelFactory(null))).group((EventLoopGroup)eventLoop)).handler((ChannelHandler)new HttpInitializer(callback, ssl, uri.getHost(), port)))
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf(5000))).resolver((AddressResolverGroup)dnsResolverGroup).remoteAddress(address).connect().addListener((GenericFutureListener)future);
  }
  
  private static void getWithDefaultResolver(EventLoop eventLoop, URI uri, int port, ChannelFutureListener future, Callback<String> callback, boolean ssl) {
    InetAddress inetHost = (InetAddress)addressCache.getIfPresent(uri.getHost());
    if (inetHost == null) {
      try {
        inetHost = InetAddress.getByName(uri.getHost());
      } catch (UnknownHostException ex) {
        callback.done(null, ex);
        return;
      } 
      addressCache.put(uri.getHost(), inetHost);
    } 
    ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).channelFactory(PipelineUtils.getChannelFactory(null))).group((EventLoopGroup)eventLoop)).handler((ChannelHandler)new HttpInitializer(callback, ssl, uri.getHost(), port)))
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf(5000))).remoteAddress(inetHost, port).connect().addListener((GenericFutureListener)future);
  }
}
