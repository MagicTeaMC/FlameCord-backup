package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.natives.Natives;
import dev._2lstudios.flamecord.natives.compress.Compressor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.compress.PacketCompressor;
import net.md_5.bungee.compress.PacketDecompressor;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Kick;

public class ChannelWrapper {
  private final Channel ch;
  
  private SocketAddress remoteAddress;
  
  private volatile boolean closed;
  
  private volatile boolean closing;
  
  MinecraftDecoder decoder;
  
  MinecraftEncoder encoder;
  
  public SocketAddress getRemoteAddress() {
    return this.remoteAddress;
  }
  
  public void setRemoteAddress(SocketAddress remoteAddress) {
    this.remoteAddress = remoteAddress;
  }
  
  public boolean isClosed() {
    return this.closed;
  }
  
  public boolean isClosing() {
    return this.closing;
  }
  
  public ChannelWrapper(ChannelHandlerContext ctx) {
    this.ch = ctx.channel();
    this.remoteAddress = (this.ch.remoteAddress() == null) ? this.ch.parent().localAddress() : this.ch.remoteAddress();
    this.decoder = (MinecraftDecoder)this.ch.pipeline().get("packet-decoder");
    this.encoder = (MinecraftEncoder)this.ch.pipeline().get("packet-encoder");
  }
  
  public void setProtocol(Protocol protocol) {
    ChannelPipeline pipeline = this.ch.pipeline();
    this.decoder.setProtocol(protocol);
    this.encoder.setProtocol(protocol);
  }
  
  public void setVersion(int protocol) {
    ChannelPipeline pipeline = this.ch.pipeline();
    this.decoder.setProtocolVersion(protocol);
    this.encoder.setProtocolVersion(protocol);
  }
  
  public void write(Object packet) {
    if (!this.closed)
      if (packet instanceof PacketWrapper) {
        ((PacketWrapper)packet).setReleased(true);
        this.ch.writeAndFlush(((PacketWrapper)packet).buf, this.ch.voidPromise());
      } else {
        this.ch.writeAndFlush(packet, this.ch.voidPromise());
      }  
  }
  
  public void markClosed() {
    this.closed = this.closing = true;
  }
  
  public void close() {
    close(null);
  }
  
  public void close(Object packet) {
    if (!this.closed) {
      this.closed = this.closing = true;
      if (packet != null && this.ch.isActive()) {
        this.ch.writeAndFlush(packet).addListeners(new GenericFutureListener[] { (GenericFutureListener)ChannelFutureListener.CLOSE });
      } else {
        this.ch.close();
      } 
    } 
  }
  
  public void delayedClose(final Kick kick) {
    if (!this.closing) {
      this.closing = true;
      this.ch.eventLoop().schedule(new Runnable() {
            public void run() {
              ChannelWrapper.this.close(kick);
            }
          },  250L, TimeUnit.MILLISECONDS);
    } 
  }
  
  public void addBefore(String baseName, String name, ChannelHandler handler) {
    Preconditions.checkState(this.ch.eventLoop().inEventLoop(), "cannot add handler outside of event loop");
    this.ch.pipeline().addBefore(baseName, name, handler);
  }
  
  public Channel getHandle() {
    return this.ch;
  }
  
  public void setCompressionThreshold(int compressionThreshold) {
    Compressor compressor = Natives.getCompressorFactory().create(FlameCord.getInstance().getFlameCordConfiguration().getCompressionLevel());
    ChannelPipeline pipeline = this.ch.pipeline();
    if (pipeline.get(PacketCompressor.class) == null && compressionThreshold != -1)
      addBefore("packet-encoder", "compress", (ChannelHandler)new PacketCompressor(compressor)); 
    if (compressionThreshold != -1) {
      ((PacketCompressor)pipeline.get(PacketCompressor.class)).setThreshold(compressionThreshold);
    } else {
      pipeline.remove("compress");
    } 
    if (pipeline.get(PacketDecompressor.class) == null && compressionThreshold != -1)
      addBefore("packet-decoder", "decompress", (ChannelHandler)new PacketDecompressor(compressor, compressionThreshold)); 
    if (compressionThreshold == -1)
      pipeline.remove("decompress"); 
  }
  
  public Channel getChannel() {
    return this.ch;
  }
}
