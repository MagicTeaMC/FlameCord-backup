package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class OptionalSslHandler extends ByteToMessageDecoder {
  private final SslContext sslContext;
  
  public OptionalSslHandler(SslContext sslContext) {
    this.sslContext = (SslContext)ObjectUtil.checkNotNull(sslContext, "sslContext");
  }
  
  protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
    if (in.readableBytes() < 5)
      return; 
    if (SslHandler.isEncrypted(in)) {
      handleSsl(context);
    } else {
      handleNonSsl(context);
    } 
  }
  
  private void handleSsl(ChannelHandlerContext context) {
    SslHandler sslHandler = null;
    try {
      sslHandler = newSslHandler(context, this.sslContext);
      context.pipeline().replace((ChannelHandler)this, newSslHandlerName(), (ChannelHandler)sslHandler);
      sslHandler = null;
    } finally {
      if (sslHandler != null)
        ReferenceCountUtil.safeRelease(sslHandler.engine()); 
    } 
  }
  
  private void handleNonSsl(ChannelHandlerContext context) {
    ChannelHandler handler = newNonSslHandler(context);
    if (handler != null) {
      context.pipeline().replace((ChannelHandler)this, newNonSslHandlerName(), handler);
    } else {
      context.pipeline().remove((ChannelHandler)this);
    } 
  }
  
  protected String newSslHandlerName() {
    return null;
  }
  
  protected SslHandler newSslHandler(ChannelHandlerContext context, SslContext sslContext) {
    return sslContext.newHandler(context.alloc());
  }
  
  protected String newNonSslHandlerName() {
    return null;
  }
  
  protected ChannelHandler newNonSslHandler(ChannelHandlerContext context) {
    return null;
  }
}