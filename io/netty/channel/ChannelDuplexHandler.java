package io.netty.channel;

import java.net.SocketAddress;

public class ChannelDuplexHandler extends ChannelInboundHandlerAdapter implements ChannelOutboundHandler {
  @Skip
  public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
    ctx.bind(localAddress, promise);
  }
  
  @Skip
  public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
    ctx.connect(remoteAddress, localAddress, promise);
  }
  
  @Skip
  public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
    ctx.disconnect(promise);
  }
  
  @Skip
  public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
    ctx.close(promise);
  }
  
  @Skip
  public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
    ctx.deregister(promise);
  }
  
  @Skip
  public void read(ChannelHandlerContext ctx) throws Exception {
    ctx.read();
  }
  
  @Skip
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    ctx.write(msg, promise);
  }
  
  @Skip
  public void flush(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }
}
