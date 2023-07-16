package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import dev._2lstudios.flamecord.FlameCord;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.PacketWrapper;

public class HandlerBoss extends ChannelInboundHandlerAdapter {
  private ChannelWrapper channel;
  
  private PacketHandler handler;
  
  public void setHandler(PacketHandler handler) {
    Preconditions.checkArgument((handler != null), "handler");
    this.handler = handler;
  }
  
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    if (this.handler != null) {
      this.channel = new ChannelWrapper(ctx);
      this.handler.connected(this.channel);
      if (!(this.handler instanceof net.md_5.bungee.connection.InitialHandler) && !(this.handler instanceof net.md_5.bungee.connection.PingHandler) && FlameCord.getInstance().getFlameCordConfiguration().isLoggerInitialhandler())
        ProxyServer.getInstance().getLogger().log(Level.INFO, "{0} has connected", this.handler); 
    } 
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    if (this.handler != null) {
      this.channel.markClosed();
      this.handler.disconnected(this.channel);
      if (!(this.handler instanceof net.md_5.bungee.connection.InitialHandler) && !(this.handler instanceof net.md_5.bungee.connection.PingHandler) && FlameCord.getInstance().getFlameCordConfiguration().isLoggerInitialhandler())
        ProxyServer.getInstance().getLogger().log(Level.INFO, "{0} has disconnected", this.handler); 
    } 
  }
  
  public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
    if (this.handler != null)
      this.handler.writabilityChanged(this.channel); 
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (!ctx.channel().isActive()) {
      if (msg instanceof PacketWrapper)
        ((PacketWrapper)msg).trySingleRelease(); 
      return;
    } 
    if (msg instanceof HAProxyMessage) {
      HAProxyMessage proxy = (HAProxyMessage)msg;
      try {
        if (proxy.sourceAddress() != null) {
          InetSocketAddress newAddress = new InetSocketAddress(proxy.sourceAddress(), proxy.sourcePort());
          if (FlameCord.getInstance().getFlameCordConfiguration().isLoggerHaProxy())
            ProxyServer.getInstance().getLogger().log(Level.FINE, "Set remote address via PROXY {0} -> {1}", new Object[] { this.channel
                  
                  .getRemoteAddress(), newAddress }); 
          this.channel.setRemoteAddress(newAddress);
        } 
      } finally {
        proxy.release();
      } 
      return;
    } 
    if (this.handler != null) {
      PacketWrapper packet = (PacketWrapper)msg;
      boolean sendPacket = this.handler.shouldHandle(packet);
      try {
        if (sendPacket && packet.packet != null)
          try {
            packet.packet.handle(this.handler);
          } catch (CancelSendSignal ex) {
            sendPacket = false;
          }  
        if (sendPacket)
          this.handler.handle(packet); 
      } finally {
        packet.trySingleRelease();
      } 
    } 
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    String reason = cause.getClass().getSimpleName();
    if (FlameCord.getInstance().getFlameCordConfiguration().getAntibotFirewalledExceptions().contains(reason))
      FlameCord.getInstance().getAddressDataManager().getAddressData(ctx.channel().remoteAddress()).firewall(reason); 
    if (ctx.channel().isActive()) {
      boolean logExceptions = !(this.handler instanceof net.md_5.bungee.connection.PingHandler);
      logExceptions = FlameCord.getInstance().getFlameCordConfiguration().isLoggerExceptions() ? logExceptions : false;
      if (logExceptions)
        if (cause instanceof io.netty.handler.timeout.ReadTimeoutException) {
          ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - read timed out", this.handler);
        } else if (cause instanceof io.netty.handler.codec.DecoderException) {
          if (MinecraftDecoder.DEBUG) {
            LogRecord logRecord = new LogRecord(Level.WARNING, "{0} - A decoder exception has been thrown:");
            logRecord.setParameters(new Object[] { this.handler });
            logRecord.setThrown(cause);
            ProxyServer.getInstance().getLogger().log(logRecord);
          } else if (cause instanceof io.netty.handler.codec.CorruptedFrameException) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - corrupted frame: {1}", new Object[] { this.handler, cause
                  
                  .getMessage() });
          } else if (cause.getCause() instanceof net.md_5.bungee.protocol.BadPacketException) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - bad packet, are mods in use!? {1}", new Object[] { this.handler, cause
                  
                  .getCause().getMessage() });
          } else if (cause.getCause() instanceof net.md_5.bungee.protocol.OverflowPacketException) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - overflow in packet detected! {1}", new Object[] { this.handler, cause
                  
                  .getCause().getMessage() });
          } else {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - could not decode packet! {1}", new Object[] { this.handler, 
                  
                  (cause.getCause() != null) ? cause.getCause() : cause });
          } 
        } else if (cause instanceof java.io.IOException || (cause instanceof IllegalStateException && this.handler instanceof net.md_5.bungee.connection.InitialHandler)) {
          ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - {1}: {2}", new Object[] { this.handler, cause
                
                .getClass().getSimpleName(), cause.getMessage() });
        } else if (cause instanceof net.md_5.bungee.util.QuietException) {
          ProxyServer.getInstance().getLogger().log(Level.SEVERE, "{0} - encountered exception: {1}", new Object[] { this.handler, cause });
        } else {
          ProxyServer.getInstance().getLogger().log(Level.SEVERE, this.handler + " - encountered exception", cause);
        }  
      if (this.handler != null)
        try {
          this.handler.exception(cause);
        } catch (Exception ex) {
          ProxyServer.getInstance().getLogger().log(Level.SEVERE, this.handler + " - exception processing exception", ex);
        }  
      ctx.close();
    } 
  }
}
