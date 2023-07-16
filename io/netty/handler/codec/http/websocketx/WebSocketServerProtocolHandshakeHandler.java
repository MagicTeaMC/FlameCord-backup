package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

class WebSocketServerProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {
  private final WebSocketServerProtocolConfig serverConfig;
  
  private ChannelHandlerContext ctx;
  
  private ChannelPromise handshakePromise;
  
  private boolean isWebSocketPath;
  
  WebSocketServerProtocolHandshakeHandler(WebSocketServerProtocolConfig serverConfig) {
    this.serverConfig = (WebSocketServerProtocolConfig)ObjectUtil.checkNotNull(serverConfig, "serverConfig");
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) {
    this.ctx = ctx;
    this.handshakePromise = ctx.newPromise();
  }
  
  public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
    HttpObject httpObject = (HttpObject)msg;
    if (httpObject instanceof HttpRequest) {
      final HttpRequest req = (HttpRequest)httpObject;
      this.isWebSocketPath = isWebSocketPath(req);
      if (!this.isWebSocketPath) {
        ctx.fireChannelRead(msg);
        return;
      } 
      try {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(ctx.pipeline(), req, this.serverConfig.websocketPath()), this.serverConfig.subprotocols(), this.serverConfig.decoderConfig());
        final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        final ChannelPromise localHandshakePromise = this.handshakePromise;
        if (handshaker == null) {
          WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
          WebSocketServerProtocolHandler.setHandshaker(ctx.channel(), handshaker);
          ctx.pipeline().remove((ChannelHandler)this);
          ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
          handshakeFuture.addListener((GenericFutureListener)new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                  if (!future.isSuccess()) {
                    localHandshakePromise.tryFailure(future.cause());
                    ctx.fireExceptionCaught(future.cause());
                  } else {
                    localHandshakePromise.trySuccess();
                    ctx.fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                    ctx.fireUserEventTriggered(new WebSocketServerProtocolHandler.HandshakeComplete(req
                          
                          .uri(), req.headers(), handshaker.selectedSubprotocol()));
                  } 
                }
              });
          applyHandshakeTimeout();
        } 
      } finally {
        ReferenceCountUtil.release(req);
      } 
    } else if (!this.isWebSocketPath) {
      ctx.fireChannelRead(msg);
    } else {
      ReferenceCountUtil.release(msg);
    } 
  }
  
  private boolean isWebSocketPath(HttpRequest req) {
    String websocketPath = this.serverConfig.websocketPath();
    String uri = req.uri();
    boolean checkStartUri = uri.startsWith(websocketPath);
    boolean checkNextUri = ("/".equals(websocketPath) || checkNextUri(uri, websocketPath));
    return this.serverConfig.checkStartsWith() ? ((checkStartUri && checkNextUri)) : uri.equals(websocketPath);
  }
  
  private boolean checkNextUri(String uri, String websocketPath) {
    int len = websocketPath.length();
    if (uri.length() > len) {
      char nextUri = uri.charAt(len);
      return (nextUri == '/' || nextUri == '?');
    } 
    return true;
  }
  
  private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
    ChannelFuture f = ctx.writeAndFlush(res);
    if (!HttpUtil.isKeepAlive((HttpMessage)req) || res.status().code() != 200)
      f.addListener((GenericFutureListener)ChannelFutureListener.CLOSE); 
  }
  
  private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
    String protocol = "ws";
    if (cp.get(SslHandler.class) != null)
      protocol = "wss"; 
    String host = req.headers().get((CharSequence)HttpHeaderNames.HOST);
    return protocol + "://" + host + path;
  }
  
  private void applyHandshakeTimeout() {
    final ChannelPromise localHandshakePromise = this.handshakePromise;
    long handshakeTimeoutMillis = this.serverConfig.handshakeTimeoutMillis();
    if (handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone())
      return; 
    final ScheduledFuture timeoutFuture = this.ctx.executor().schedule(new Runnable() {
          public void run() {
            if (!localHandshakePromise.isDone() && localHandshakePromise
              .tryFailure(new WebSocketServerHandshakeException("handshake timed out")))
              WebSocketServerProtocolHandshakeHandler.this.ctx.flush()
                .fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_TIMEOUT)
                .close(); 
          }
        },  handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
    localHandshakePromise.addListener((GenericFutureListener)new FutureListener<Void>() {
          public void operationComplete(Future<Void> f) {
            timeoutFuture.cancel(false);
          }
        });
  }
}
