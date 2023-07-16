package net.md_5.bungee.connection;

import com.google.gson.Gson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.channel.ChannelHandler;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.util.BufUtil;
import net.md_5.bungee.util.QuietException;

public class PingHandler extends PacketHandler {
  private final ServerInfo target;
  
  private final Callback<ServerPing> callback;
  
  private final int protocol;
  
  private ChannelWrapper channel;
  
  public PingHandler(ServerInfo target, Callback<ServerPing> callback, int protocol) {
    this.target = target;
    this.callback = callback;
    this.protocol = protocol;
  }
  
  public void connected(ChannelWrapper channel) throws Exception {
    this.channel = channel;
    MinecraftEncoder encoder = new MinecraftEncoder(Protocol.HANDSHAKE, false, this.protocol);
    channel.getHandle().pipeline().addAfter("frame-decoder", "packet-decoder", (ChannelHandler)new MinecraftDecoder(Protocol.STATUS, false, ProxyServer.getInstance().getProtocolVersion()));
    channel.getHandle().pipeline().addAfter("frame-prepender", "packet-encoder", (ChannelHandler)encoder);
    channel.write(new Handshake(this.protocol, this.target.getAddress().getHostString(), this.target.getAddress().getPort(), 1));
    encoder.setProtocol(Protocol.STATUS);
    channel.write(new StatusRequest());
  }
  
  public void exception(Throwable t) throws Exception {
    this.callback.done(null, t);
  }
  
  public void handle(PacketWrapper packet) throws Exception {
    if (packet.packet == null)
      throw new QuietException("Unexpected packet received during ping process! " + BufUtil.dump(packet.buf, 16)); 
  }
  
  @SuppressFBWarnings({"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"})
  public void handle(StatusResponse statusResponse) throws Exception {
    Gson gson = (this.protocol == 4) ? (BungeeCord.getInstance()).gsonLegacy : (BungeeCord.getInstance()).gson;
    ServerPing serverPing = (ServerPing)gson.fromJson(statusResponse.getResponse(), ServerPing.class);
    ((BungeeServerInfo)this.target).cachePing(serverPing);
    this.callback.done(serverPing, null);
    this.channel.close();
  }
  
  public String toString() {
    return "[Ping Handler] -> " + this.target.getName();
  }
}
