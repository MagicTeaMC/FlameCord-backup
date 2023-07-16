package net.md_5.bungee;

import com.google.common.base.Preconditions;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.connection.PingHandler;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class BungeeServerInfo implements ServerInfo {
  private final String name;
  
  private final SocketAddress socketAddress;
  
  private final Collection<ProxiedPlayer> players;
  
  private final String motd;
  
  private final boolean restricted;
  
  private final Queue<DefinedPacket> packetQueue;
  
  private long lastPing;
  
  private ServerPing cachedPing;
  
  public BungeeServerInfo(String name, SocketAddress socketAddress, String motd, boolean restricted) {
    this.players = new ArrayList<>();
    this.packetQueue = new LinkedList<>();
    this.name = name;
    this.socketAddress = socketAddress;
    this.motd = motd;
    this.restricted = restricted;
  }
  
  public String toString() {
    return "BungeeServerInfo(name=" + getName() + ", socketAddress=" + getSocketAddress() + ", restricted=" + isRestricted() + ")";
  }
  
  public String getName() {
    return this.name;
  }
  
  public SocketAddress getSocketAddress() {
    return this.socketAddress;
  }
  
  public String getMotd() {
    return this.motd;
  }
  
  public boolean isRestricted() {
    return this.restricted;
  }
  
  public Queue<DefinedPacket> getPacketQueue() {
    return this.packetQueue;
  }
  
  public void addPlayer(ProxiedPlayer player) {
    synchronized (this.players) {
      this.players.add(player);
    } 
  }
  
  public void removePlayer(ProxiedPlayer player) {
    synchronized (this.players) {
      this.players.remove(player);
    } 
  }
  
  public Collection<ProxiedPlayer> getPlayers() {
    synchronized (this.players) {
      return Collections.unmodifiableCollection(new HashSet<>(this.players));
    } 
  }
  
  public String getPermission() {
    return "bungeecord.server." + this.name;
  }
  
  public boolean canAccess(CommandSender player) {
    Preconditions.checkNotNull(player, "player");
    return (!this.restricted || player.hasPermission(getPermission()));
  }
  
  public boolean equals(Object obj) {
    return (obj instanceof ServerInfo && Objects.equals(getAddress(), ((ServerInfo)obj).getAddress()));
  }
  
  public int hashCode() {
    return this.socketAddress.hashCode();
  }
  
  public void sendData(String channel, byte[] data) {
    sendData(channel, data, true);
  }
  
  public boolean sendData(String channel, byte[] data, boolean queue) {
    Server server;
    Preconditions.checkNotNull(channel, "channel");
    Preconditions.checkNotNull(data, "data");
    synchronized (this.players) {
      server = this.players.isEmpty() ? null : ((ProxiedPlayer)this.players.iterator().next()).getServer();
    } 
    if (server != null) {
      server.sendData(channel, data);
      return true;
    } 
    if (queue)
      synchronized (this.packetQueue) {
        this.packetQueue.add(new PluginMessage(channel, data, false));
      }  
    return false;
  }
  
  public void cachePing(ServerPing serverPing) {
    if (ProxyServer.getInstance().getConfig().getRemotePingCache() > 0) {
      this.cachedPing = serverPing;
      this.lastPing = System.currentTimeMillis();
    } 
  }
  
  public InetSocketAddress getAddress() {
    return (InetSocketAddress)this.socketAddress;
  }
  
  public void ping(Callback<ServerPing> callback) {
    ping(callback, ProxyServer.getInstance().getProtocolVersion());
  }
  
  public void ping(final Callback<ServerPing> callback, final int protocolVersion) {
    Preconditions.checkNotNull(callback, "callback");
    int pingCache = ProxyServer.getInstance().getConfig().getRemotePingCache();
    if (pingCache > 0 && this.cachedPing != null && System.currentTimeMillis() - this.lastPing > pingCache)
      this.cachedPing = null; 
    if (this.cachedPing != null) {
      callback.done(this.cachedPing, null);
      return;
    } 
    ChannelFutureListener listener = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture future) throws Exception {
          if (future.isSuccess()) {
            ((HandlerBoss)future.channel().pipeline().get(HandlerBoss.class)).setHandler((PacketHandler)new PingHandler(BungeeServerInfo.this, callback, protocolVersion));
          } else {
            callback.done(null, future.cause());
          } 
        }
      };
    ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap())
      .channelFactory(PipelineUtils.getChannelFactory(this.socketAddress)))
      .group((BungeeCord.getInstance()).workerEventLoopGroup))
      .handler((ChannelHandler)PipelineUtils.BASE_SERVERSIDE))
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf(BungeeCord.getInstance().getConfig().getRemotePingTimeout())))
      .remoteAddress(this.socketAddress)
      .connect()
      .addListener((GenericFutureListener)listener);
  }
}
