package io.netty.handler.address;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;

@Sharable
public class ResolveAddressHandler extends ChannelOutboundHandlerAdapter {
  private final AddressResolverGroup<? extends SocketAddress> resolverGroup;
  
  public ResolveAddressHandler(AddressResolverGroup<? extends SocketAddress> resolverGroup) {
    this.resolverGroup = (AddressResolverGroup<? extends SocketAddress>)ObjectUtil.checkNotNull(resolverGroup, "resolverGroup");
  }
  
  public void connect(final ChannelHandlerContext ctx, SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
    AddressResolver<? extends SocketAddress> resolver = this.resolverGroup.getResolver(ctx.executor());
    if (resolver.isSupported(remoteAddress) && !resolver.isResolved(remoteAddress)) {
      resolver.resolve(remoteAddress).addListener((GenericFutureListener)new FutureListener<SocketAddress>() {
            public void operationComplete(Future<SocketAddress> future) {
              Throwable cause = future.cause();
              if (cause != null) {
                promise.setFailure(cause);
              } else {
                ctx.connect((SocketAddress)future.getNow(), localAddress, promise);
              } 
              ctx.pipeline().remove((ChannelHandler)ResolveAddressHandler.this);
            }
          });
    } else {
      ctx.connect(remoteAddress, localAddress, promise);
      ctx.pipeline().remove((ChannelHandler)this);
    } 
  }
}
