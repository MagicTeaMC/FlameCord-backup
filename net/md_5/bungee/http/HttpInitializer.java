package net.md_5.bungee.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;
import net.md_5.bungee.api.Callback;

public class HttpInitializer extends ChannelInitializer<Channel> {
  private final Callback<String> callback;
  
  private final boolean ssl;
  
  private final String host;
  
  private final int port;
  
  public HttpInitializer(Callback<String> callback, boolean ssl, String host, int port) {
    this.callback = callback;
    this.ssl = ssl;
    this.host = host;
    this.port = port;
  }
  
  protected void initChannel(Channel ch) throws Exception {
    ch.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(5000L, TimeUnit.MILLISECONDS));
    if (this.ssl) {
      SSLEngine engine = SslContextBuilder.forClient().build().newEngine(ch.alloc(), this.host, this.port);
      ch.pipeline().addLast("ssl", (ChannelHandler)new SslHandler(engine));
    } 
    ch.pipeline().addLast("http", (ChannelHandler)new HttpClientCodec());
    ch.pipeline().addLast("handler", (ChannelHandler)new HttpHandler(this.callback));
  }
}
