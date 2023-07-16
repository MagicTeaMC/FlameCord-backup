package io.netty.handler.ssl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public abstract class ApplicationProtocolNegotiationHandler extends ChannelInboundHandlerAdapter {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(ApplicationProtocolNegotiationHandler.class);
  
  private final String fallbackProtocol;
  
  private final RecyclableArrayList bufferedMessages = RecyclableArrayList.newInstance();
  
  private ChannelHandlerContext ctx;
  
  private boolean sslHandlerChecked;
  
  protected ApplicationProtocolNegotiationHandler(String fallbackProtocol) {
    this.fallbackProtocol = (String)ObjectUtil.checkNotNull(fallbackProtocol, "fallbackProtocol");
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    this.ctx = ctx;
    super.handlerAdded(ctx);
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    fireBufferedMessages();
    this.bufferedMessages.recycle();
    super.handlerRemoved(ctx);
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    this.bufferedMessages.add(msg);
    if (!this.sslHandlerChecked) {
      this.sslHandlerChecked = true;
      if (ctx.pipeline().get(SslHandler.class) == null)
        removeSelfIfPresent(ctx); 
    } 
  }
  
  private void fireBufferedMessages() {
    if (!this.bufferedMessages.isEmpty()) {
      for (int i = 0; i < this.bufferedMessages.size(); i++)
        this.ctx.fireChannelRead(this.bufferedMessages.get(i)); 
      this.ctx.fireChannelReadComplete();
      this.bufferedMessages.clear();
    } 
  }
  
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof SslHandshakeCompletionEvent) {
      SslHandshakeCompletionEvent handshakeEvent = (SslHandshakeCompletionEvent)evt;
      try {
        if (handshakeEvent.isSuccess()) {
          SslHandler sslHandler = (SslHandler)ctx.pipeline().get(SslHandler.class);
          if (sslHandler == null)
            throw new IllegalStateException("cannot find an SslHandler in the pipeline (required for application-level protocol negotiation)"); 
          String protocol = sslHandler.applicationProtocol();
          configurePipeline(ctx, (protocol != null) ? protocol : this.fallbackProtocol);
        } 
      } catch (Throwable cause) {
        exceptionCaught(ctx, cause);
      } finally {
        if (handshakeEvent.isSuccess())
          removeSelfIfPresent(ctx); 
      } 
    } 
    if (evt instanceof io.netty.channel.socket.ChannelInputShutdownEvent)
      fireBufferedMessages(); 
    ctx.fireUserEventTriggered(evt);
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    fireBufferedMessages();
    super.channelInactive(ctx);
  }
  
  private void removeSelfIfPresent(ChannelHandlerContext ctx) {
    ChannelPipeline pipeline = ctx.pipeline();
    if (!ctx.isRemoved())
      pipeline.remove((ChannelHandler)this); 
  }
  
  protected abstract void configurePipeline(ChannelHandlerContext paramChannelHandlerContext, String paramString) throws Exception;
  
  protected void handshakeFailure(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    logger.warn("{} TLS handshake failed:", ctx.channel(), cause);
    ctx.close();
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    Throwable wrapped;
    if (cause instanceof io.netty.handler.codec.DecoderException && wrapped = cause.getCause() instanceof javax.net.ssl.SSLException)
      try {
        handshakeFailure(ctx, wrapped);
        return;
      } finally {
        removeSelfIfPresent(ctx);
      }  
    logger.warn("{} Failed to select the application-level protocol:", ctx.channel(), cause);
    ctx.fireExceptionCaught(cause);
    ctx.close();
  }
}
