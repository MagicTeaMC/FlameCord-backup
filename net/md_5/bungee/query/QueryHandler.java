package net.md_5.bungee.query;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.waterfallmc.waterfall.QueryResult;
import io.github.waterfallmc.waterfall.event.ProxyQueryEvent;
import io.github.waterfallmc.waterfall.utils.FastException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Event;

public class QueryHandler extends SimpleChannelInboundHandler<DatagramPacket> {
  private final ProxyServer bungee;
  
  private final ListenerInfo listener;
  
  private final Random random;
  
  private final Cache<InetAddress, QuerySession> sessions;
  
  public QueryHandler(ProxyServer bungee, ListenerInfo listener) {
    this.random = new Random();
    this.sessions = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.SECONDS).build();
    this.bungee = bungee;
    this.listener = listener;
  }
  
  private static FastException cachedNoSessionException = new FastException("No Session!");
  
  private void writeShort(ByteBuf buf, int s) {
    buf.writeShortLE(s);
  }
  
  private void writeNumber(ByteBuf buf, int i) {
    writeString(buf, Integer.toString(i));
  }
  
  private void writeString(ByteBuf buf, String s) {
    for (char c : s.toCharArray())
      buf.writeByte(c); 
    buf.writeByte(0);
  }
  
  protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
    try {
      handleMessage(ctx, msg);
    } catch (Throwable t) {
      this.bungee.getLogger().log(Level.WARNING, "Error whilst handling query packet from " + msg.sender(), t);
    } 
  }
  
  private void handleMessage(ChannelHandlerContext ctx, DatagramPacket msg) {
    ByteBuf in = (ByteBuf)msg.content();
    if (in.readUnsignedByte() != 254 || in.readUnsignedByte() != 253) {
      this.bungee.getLogger().log(Level.WARNING, "Query - Incorrect magic!: {0}", msg.sender());
      return;
    } 
    ByteBuf out = ctx.alloc().buffer();
    DatagramPacket datagramPacket = new DatagramPacket(out, (InetSocketAddress)msg.sender());
    byte type = in.readByte();
    int sessionId = in.readInt();
    if (type == 9) {
      out.writeByte(9);
      out.writeInt(sessionId);
      int challengeToken = this.random.nextInt();
      this.sessions.put(((InetSocketAddress)msg.sender()).getAddress(), new QuerySession(challengeToken, System.currentTimeMillis()));
      writeNumber(out, challengeToken);
    } 
    if (type == 0) {
      int challengeToken = in.readInt();
      QuerySession session = (QuerySession)this.sessions.getIfPresent(((InetSocketAddress)msg.sender()).getAddress());
      if (session == null || session.getToken() != challengeToken)
        throw cachedNoSessionException; 
      List<String> players = (List<String>)this.bungee.getPlayers().stream().map(CommandSender::getName).collect(Collectors.toList());
      ProxyQueryEvent event = new ProxyQueryEvent(this.listener, new QueryResult(this.listener.getMotd(), "SMP", "FlameCord_Proxy", this.bungee.getOnlineCount(), this.listener.getMaxPlayers(), this.listener.getHost().getPort(), this.listener.getHost().getHostString(), "MINECRAFT", players, this.bungee.getGameVersion()));
      QueryResult result = ((ProxyQueryEvent)this.bungee.getPluginManager().callEvent((Event)event)).getResult();
      out.writeByte(0);
      out.writeInt(sessionId);
      if (in.readableBytes() == 0) {
        writeString(out, result.getMotd());
        writeString(out, result.getGameType());
        writeString(out, result.getWorldName());
        writeNumber(out, result.getOnlinePlayers());
        writeNumber(out, result.getMaxPlayers());
        writeShort(out, result.getPort());
        writeString(out, result.getAddress());
      } else if (in.readableBytes() == 4) {
        out.writeBytes(new byte[] { 
              115, 112, 108, 105, 116, 110, 117, 109, 0, Byte.MIN_VALUE, 
              0 });
        Map<String, String> data = new LinkedHashMap<>();
        data.put("hostname", result.getMotd());
        data.put("gametype", result.getGameType());
        data.put("game_id", result.getGameId());
        data.put("version", result.getVersion());
        data.put("plugins", "");
        data.put("map", result.getWorldName());
        data.put("numplayers", Integer.toString(result.getOnlinePlayers()));
        data.put("maxplayers", Integer.toString(result.getMaxPlayers()));
        data.put("hostport", Integer.toString(result.getPort()));
        data.put("hostip", result.getAddress());
        for (Map.Entry<String, String> entry : data.entrySet()) {
          writeString(out, entry.getKey());
          writeString(out, entry.getValue());
        } 
        out.writeByte(0);
        writeString(out, "\001player_\000");
        result.getPlayers().stream().forEach(p -> writeString(out, p));
        out.writeByte(0);
      } else {
        throw new IllegalStateException("Invalid data request packet");
      } 
    } 
    ctx.writeAndFlush(datagramPacket);
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    this.bungee.getLogger().log(Level.WARNING, "Error whilst handling query packet from " + ctx.channel().remoteAddress(), cause);
  }
  
  private static class QuerySession {
    private final int token;
    
    private final long time;
    
    public QuerySession(int token, long time) {
      this.token = token;
      this.time = time;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof QuerySession))
        return false; 
      QuerySession other = (QuerySession)o;
      return !other.canEqual(this) ? false : ((getToken() != other.getToken()) ? false : (!(getTime() != other.getTime())));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof QuerySession;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      result = result * 59 + getToken();
      long $time = getTime();
      return result * 59 + (int)($time >>> 32L ^ $time);
    }
    
    public String toString() {
      return "QueryHandler.QuerySession(token=" + getToken() + ", time=" + getTime() + ")";
    }
    
    public int getToken() {
      return this.token;
    }
    
    public long getTime() {
      return this.time;
    }
  }
}
