package net.md_5.bungee.query;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;

public class RemoteQuery {
  private final ProxyServer bungee;
  
  private final ListenerInfo listener;
  
  public RemoteQuery(ProxyServer bungee, ListenerInfo listener) {
    this.bungee = bungee;
    this.listener = listener;
  }
  
  public void start(Class<? extends Channel> channel, InetSocketAddress address, EventLoopGroup eventLoop, ChannelFutureListener future) {
    ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap())
      .channel(channel))
      .group(eventLoop))
      .handler((ChannelHandler)new QueryHandler(this.bungee, this.listener)))
      .localAddress(address))
      .bind().addListener((GenericFutureListener)future);
  }
}
