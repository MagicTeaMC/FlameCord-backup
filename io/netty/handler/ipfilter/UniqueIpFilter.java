package io.netty.handler.ipfilter;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ConcurrentSet;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

@Sharable
public class UniqueIpFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {
  private final Set<InetAddress> connected = (Set<InetAddress>)new ConcurrentSet();
  
  protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
    final InetAddress remoteIp = remoteAddress.getAddress();
    if (!this.connected.add(remoteIp))
      return false; 
    ctx.channel().closeFuture().addListener((GenericFutureListener)new ChannelFutureListener() {
          public void operationComplete(ChannelFuture future) throws Exception {
            UniqueIpFilter.this.connected.remove(remoteIp);
          }
        });
    return true;
  }
}
