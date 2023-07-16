package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class WebSocketServerExtensionHandler extends ChannelDuplexHandler {
  private final List<WebSocketServerExtensionHandshaker> extensionHandshakers;
  
  private final Queue<List<WebSocketServerExtension>> validExtensions = new ArrayDeque<List<WebSocketServerExtension>>(4);
  
  public WebSocketServerExtensionHandler(WebSocketServerExtensionHandshaker... extensionHandshakers) {
    this.extensionHandshakers = Arrays.asList(ObjectUtil.checkNonEmpty((Object[])extensionHandshakers, "extensionHandshakers"));
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg != LastHttpContent.EMPTY_LAST_CONTENT) {
      if (msg instanceof io.netty.handler.codec.http.DefaultHttpRequest) {
        onHttpRequestChannelRead(ctx, (HttpRequest)msg);
      } else if (msg instanceof HttpRequest) {
        onHttpRequestChannelRead(ctx, (HttpRequest)msg);
      } else {
        super.channelRead(ctx, msg);
      } 
    } else {
      super.channelRead(ctx, msg);
    } 
  }
  
  protected void onHttpRequestChannelRead(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
    List<WebSocketServerExtension> validExtensionsList = null;
    if (WebSocketExtensionUtil.isWebsocketUpgrade(request.headers())) {
      String extensionsHeader = request.headers().getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
      if (extensionsHeader != null) {
        List<WebSocketExtensionData> extensions = WebSocketExtensionUtil.extractExtensions(extensionsHeader);
        int rsv = 0;
        for (WebSocketExtensionData extensionData : extensions) {
          Iterator<WebSocketServerExtensionHandshaker> extensionHandshakersIterator = this.extensionHandshakers.iterator();
          WebSocketServerExtension validExtension = null;
          while (validExtension == null && extensionHandshakersIterator.hasNext()) {
            WebSocketServerExtensionHandshaker extensionHandshaker = extensionHandshakersIterator.next();
            validExtension = extensionHandshaker.handshakeExtension(extensionData);
          } 
          if (validExtension != null && (validExtension.rsv() & rsv) == 0) {
            if (validExtensionsList == null)
              validExtensionsList = new ArrayList<WebSocketServerExtension>(1); 
            rsv |= validExtension.rsv();
            validExtensionsList.add(validExtension);
          } 
        } 
      } 
    } 
    if (validExtensionsList == null)
      validExtensionsList = Collections.emptyList(); 
    this.validExtensions.offer(validExtensionsList);
    super.channelRead(ctx, request);
  }
  
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    if (msg != Unpooled.EMPTY_BUFFER && !(msg instanceof io.netty.buffer.ByteBuf)) {
      if (msg instanceof io.netty.handler.codec.http.DefaultHttpResponse) {
        onHttpResponseWrite(ctx, (HttpResponse)msg, promise);
      } else if (msg instanceof HttpResponse) {
        onHttpResponseWrite(ctx, (HttpResponse)msg, promise);
      } else {
        super.write(ctx, msg, promise);
      } 
    } else {
      super.write(ctx, msg, promise);
    } 
  }
  
  protected void onHttpResponseWrite(ChannelHandlerContext ctx, HttpResponse response, ChannelPromise promise) throws Exception {
    List<WebSocketServerExtension> validExtensionsList = this.validExtensions.poll();
    HttpResponse httpResponse = response;
    if (HttpResponseStatus.SWITCHING_PROTOCOLS.equals(httpResponse.status()))
      handlePotentialUpgrade(ctx, promise, httpResponse, validExtensionsList); 
    super.write(ctx, response, promise);
  }
  
  private void handlePotentialUpgrade(final ChannelHandlerContext ctx, ChannelPromise promise, HttpResponse httpResponse, final List<WebSocketServerExtension> validExtensionsList) {
    HttpHeaders headers = httpResponse.headers();
    if (WebSocketExtensionUtil.isWebsocketUpgrade(headers)) {
      if (validExtensionsList != null && !validExtensionsList.isEmpty()) {
        String headerValue = headers.getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
        List<WebSocketExtensionData> extraExtensions = new ArrayList<WebSocketExtensionData>(this.extensionHandshakers.size());
        for (WebSocketServerExtension extension : validExtensionsList)
          extraExtensions.add(extension.newReponseData()); 
        String newHeaderValue = WebSocketExtensionUtil.computeMergeExtensionsHeaderValue(headerValue, extraExtensions);
        promise.addListener((GenericFutureListener)new ChannelFutureListener() {
              public void operationComplete(ChannelFuture future) {
                if (future.isSuccess())
                  for (WebSocketServerExtension extension : validExtensionsList) {
                    WebSocketExtensionDecoder decoder = extension.newExtensionDecoder();
                    WebSocketExtensionEncoder encoder = extension.newExtensionEncoder();
                    String name = ctx.name();
                    ctx.pipeline()
                      .addAfter(name, decoder.getClass().getName(), (ChannelHandler)decoder)
                      .addAfter(name, encoder.getClass().getName(), (ChannelHandler)encoder);
                  }  
              }
            });
        if (newHeaderValue != null)
          headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, newHeaderValue); 
      } 
      promise.addListener((GenericFutureListener)new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
              if (future.isSuccess())
                ctx.pipeline().remove((ChannelHandler)WebSocketServerExtensionHandler.this); 
            }
          });
    } 
  }
}
