package net.md_5.bungee.http;

import dev._2lstudios.flamecord.FlameCord;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.charset.Charset;
import net.md_5.bungee.api.Callback;

public class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {
  private final Callback<String> callback;
  
  private final StringBuilder buffer;
  
  public HttpHandler(Callback<String> callback) {
    this.buffer = new StringBuilder(640);
    this.callback = callback;
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    String reason = cause.getClass().getSimpleName();
    if (FlameCord.getInstance().getFlameCordConfiguration().getAntibotFirewalledExceptions().contains(reason))
      FlameCord.getInstance().getAddressDataManager().getAddressData(ctx.channel().remoteAddress()).firewall(reason); 
    try {
      this.callback.done(null, cause);
    } finally {
      ctx.channel().pipeline().remove((ChannelHandler)this);
      ctx.channel().close();
    } 
  }
  
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    if (msg instanceof HttpResponse) {
      HttpResponse response = (HttpResponse)msg;
      int responseCode = response.status().code();
      if (responseCode == HttpResponseStatus.NO_CONTENT.code()) {
        done(ctx);
        return;
      } 
      if (responseCode != HttpResponseStatus.OK.code())
        throw new IllegalStateException("Expected HTTP response 200 OK, got " + response.status()); 
    } 
    if (msg instanceof HttpContent) {
      HttpContent content = (HttpContent)msg;
      this.buffer.append(content.content().toString(Charset.forName("UTF-8")));
      if (msg instanceof io.netty.handler.codec.http.LastHttpContent)
        done(ctx); 
    } 
  }
  
  private void done(ChannelHandlerContext ctx) {
    try {
      this.callback.done(this.buffer.toString(), null);
    } finally {
      ctx.channel().pipeline().remove((ChannelHandler)this);
      ctx.channel().close();
    } 
  }
}
