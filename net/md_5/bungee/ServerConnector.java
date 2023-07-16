package net.md_5.bungee;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.forge.ForgeServerHandler;
import net.md_5.bungee.forge.ForgeUtils;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Property;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EntityStatus;
import net.md_5.bungee.protocol.packet.GameState;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.LoginPayloadRequest;
import net.md_5.bungee.protocol.packet.LoginPayloadResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.ViewDistance;
import net.md_5.bungee.util.AddressUtil;
import net.md_5.bungee.util.BufUtil;
import net.md_5.bungee.util.QuietException;

public class ServerConnector extends PacketHandler {
  private final ProxyServer bungee;
  
  private ChannelWrapper ch;
  
  private final UserConnection user;
  
  private final BungeeServerInfo target;
  
  private State thisState;
  
  private ForgeServerHandler handshakeHandler;
  
  private boolean obsolete;
  
  public ServerConnector(ProxyServer bungee, UserConnection user, BungeeServerInfo target) {
    this.thisState = State.LOGIN_SUCCESS;
    this.bungee = bungee;
    this.user = user;
    this.target = target;
  }
  
  public ForgeServerHandler getHandshakeHandler() {
    return this.handshakeHandler;
  }
  
  private enum State {
    LOGIN_SUCCESS, LOGIN, FINISHED;
  }
  
  public void exception(Throwable t) throws Exception {
    if (this.obsolete)
      return; 
    String message = ChatColor.RED + "Exception Connecting: " + Util.exception(t);
    if (this.user.getServer() == null) {
      this.user.disconnect(message);
    } else {
      this.user.sendMessage(message);
    } 
  }
  
  public void connected(ChannelWrapper channel) throws Exception {
    this.ch = channel;
    this.handshakeHandler = new ForgeServerHandler(this.user, this.ch, this.target);
    Handshake originalHandshake = this.user.getPendingConnection().getHandshake();
    Handshake copiedHandshake = new Handshake(originalHandshake.getProtocolVersion(), originalHandshake.getHost(), originalHandshake.getPort(), 2);
    if ((BungeeCord.getInstance()).config.isIpForward() && this.user.getSocketAddress() instanceof java.net.InetSocketAddress) {
      String newHost = copiedHandshake.getHost() + "\000" + AddressUtil.sanitizeAddress(this.user.getAddress()) + "\000" + this.user.getUUID();
      LoginResult profile = this.user.getPendingConnection().getLoginProfile();
      Property[] properties = new Property[0];
      if (profile != null && profile.getProperties() != null && (profile.getProperties()).length > 0)
        properties = profile.getProperties(); 
      if (this.user.getForgeClientHandler().isFmlTokenInHandshake()) {
        Property[] newp = Arrays.<Property>copyOf(properties, properties.length + 2);
        newp[newp.length - 2] = new Property("forgeClient", "true", null);
        newp[newp.length - 1] = new Property("extraData", this.user.getExtraDataInHandshake().replace("\000", "\001"), "");
        properties = newp;
      } 
      if (properties.length > 0)
        newHost = newHost + "\000" + (BungeeCord.getInstance()).gson.toJson(properties); 
      copiedHandshake.setHost(newHost);
    } else if (!this.user.getExtraDataInHandshake().isEmpty()) {
      copiedHandshake.setHost(copiedHandshake.getHost() + this.user.getExtraDataInHandshake());
    } 
    channel.write(copiedHandshake);
    channel.setProtocol(Protocol.LOGIN);
    channel.write(new LoginRequest(this.user.getName(), null, this.user.getUniqueId()));
  }
  
  public void disconnected(ChannelWrapper channel) throws Exception {
    this.user.getPendingConnects().remove(this.target);
  }
  
  public void handle(PacketWrapper packet) throws Exception {
    if (packet.packet == null)
      throw new QuietException("Unexpected packet received during server connector process!\n" + BufUtil.dump(packet.buf, 16)); 
  }
  
  public void handle(LoginSuccess loginSuccess) throws Exception {
    Preconditions.checkState((this.thisState == State.LOGIN_SUCCESS), "Not expecting LOGIN_SUCCESS");
    this.ch.setProtocol(Protocol.GAME);
    this.thisState = State.LOGIN;
    if (this.user.getServer() != null && this.user.getForgeClientHandler().isHandshakeComplete() && this.user
      .getServer().isForgeServer())
      this.user.getForgeClientHandler().resetHandshake(); 
    throw CancelSendSignal.INSTANCE;
  }
  
  public void handle(SetCompression setCompression) throws Exception {
    this.ch.setCompressionThreshold(setCompression.getThreshold());
  }
  
  public void handle(Login login) throws Exception {
    Preconditions.checkState((this.thisState == State.LOGIN), "Not expecting LOGIN");
    ServerConnection server = new ServerConnection(this.ch, this.target);
    ServerConnectedEvent event = new ServerConnectedEvent(this.user, server);
    if (server.isForgeServer() && this.user.isForgeUser()) {
      ((MinecraftDecoder)server.getCh().getHandle().pipeline().get("packet-decoder")).setSupportsForge(true);
      ((MinecraftDecoder)this.user.getCh().getHandle().pipeline().get("packet-decoder")).setSupportsForge(true);
    } 
    this.bungee.getPluginManager().callEvent((Event)event);
    this.ch.write(BungeeCord.getInstance().registerChannels(this.user.getPendingConnection().getVersion()));
    Queue<DefinedPacket> packetQueue = this.target.getPacketQueue();
    synchronized (packetQueue) {
      while (!packetQueue.isEmpty())
        this.ch.write(packetQueue.poll()); 
    } 
    PluginMessage brandMessage = this.user.getPendingConnection().getBrandMessage();
    if (brandMessage != null)
      this.ch.write(brandMessage); 
    Set<String> registeredChannels = this.user.getPendingConnection().getRegisteredChannels();
    if (!registeredChannels.isEmpty())
      this.ch.write(new PluginMessage((this.user.getPendingConnection().getVersion() >= 393) ? "minecraft:register" : "REGISTER", Joiner.on("\000").join(registeredChannels).getBytes(StandardCharsets.UTF_8), false)); 
    if (this.user.getForgeClientHandler().getClientModList() == null && !this.user.getForgeClientHandler().isHandshakeComplete())
      this.user.getForgeClientHandler().setHandshakeComplete(); 
    if (this.user.getServer() == null || !(login.getDimension() instanceof Integer)) {
      this.user.setClientEntityId(login.getEntityId());
      this.user.setServerEntityId(login.getEntityId());
      Login modLogin = new Login(login.getEntityId(), login.isHardcore(), login.getGameMode(), login.getPreviousGameMode(), login.getWorldNames(), login.getDimensions(), login.getDimension(), login.getWorldName(), login.getSeed(), login.getDifficulty(), (byte)this.user.getPendingConnection().getListener().getTabListSize(), login.getLevelType(), login.getViewDistance(), login.getSimulationDistance(), login.isReducedDebugInfo(), login.isNormalRespawn(), login.isDebug(), login.isFlat(), login.getDeathLocation(), login.getPortalCooldown());
      this.user.unsafe().sendPacket((DefinedPacket)modLogin);
      if (this.user.getServer() != null) {
        this.user.getServer().setObsolete(true);
        this.user.getTabListHandler().onServerChange();
        this.user.getServerSentScoreboard().clear();
        for (UUID bossbar : this.user.getSentBossBars())
          this.user.unsafe().sendPacket((DefinedPacket)new BossBar(bossbar, 1)); 
        this.user.getSentBossBars().clear();
        this.user.unsafe().sendPacket((DefinedPacket)new Respawn(login.getDimension(), login.getWorldName(), login.getSeed(), login.getDifficulty(), login.getGameMode(), login.getPreviousGameMode(), login.getLevelType(), login.isDebug(), login.isFlat(), false, login.getDeathLocation(), login
              .getPortalCooldown()));
        this.user.getServer().disconnect("Quitting");
      } else {
        String brandString = this.bungee.getName() + " (" + this.bungee.getVersion() + ")";
        if (ProtocolConstants.isBeforeOrEq(this.user.getPendingConnection().getVersion(), 5)) {
          this.user.unsafe().sendPacket((DefinedPacket)new PluginMessage("MC|Brand", brandString.getBytes(StandardCharsets.UTF_8), this.handshakeHandler.isServerForge()));
        } else {
          ByteBuf brand = ByteBufAllocator.DEFAULT.heapBuffer();
          DefinedPacket.writeString(this.bungee.getName() + " (" + this.bungee.getVersion() + ")", brand);
          this.user.unsafe().sendPacket((DefinedPacket)new PluginMessage((this.user.getPendingConnection().getVersion() >= 393) ? "minecraft:brand" : "MC|Brand", brand, this.handshakeHandler.isServerForge()));
          brand.release();
        } 
      } 
      this.user.setDimension(login.getDimension());
    } else {
      this.user.getServer().setObsolete(true);
      this.user.getTabListHandler().onServerChange();
      Scoreboard serverScoreboard = this.user.getServerSentScoreboard();
      serverScoreboard.clear();
      for (UUID bossbar : this.user.getSentBossBars())
        this.user.unsafe().sendPacket((DefinedPacket)new BossBar(bossbar, 1)); 
      this.user.getSentBossBars().clear();
      this.user.unsafe().sendPacket((DefinedPacket)new EntityStatus(this.user.getClientEntityId(), login.isReducedDebugInfo() ? 22 : 23));
      if (this.user.getPendingConnection().getVersion() >= 573)
        this.user.unsafe().sendPacket((DefinedPacket)new GameState((short)11, login.isNormalRespawn() ? 0.0F : 1.0F)); 
      this.user.setDimensionChange(true);
      this.user.setServerEntityId(login.getEntityId());
      this.user.setClientEntityId(login.getEntityId());
      if (login.getDimension() != this.user.getDimension())
        this.user.unsafe().sendPacket((DefinedPacket)new Respawn(Integer.valueOf((((Integer)this.user.getDimension()).intValue() >= 0) ? -1 : 0), login.getWorldName(), login.getSeed(), login.getDifficulty(), login.getGameMode(), login.getPreviousGameMode(), login.getLevelType(), login.isDebug(), login.isFlat(), false, login.getDeathLocation(), login.getPortalCooldown())); 
      Login modLogin = new Login(login.getEntityId(), login.isHardcore(), login.getGameMode(), login.getPreviousGameMode(), login.getWorldNames(), login.getDimensions(), login.getDimension(), login.getWorldName(), login.getSeed(), login.getDifficulty(), (byte)this.user.getPendingConnection().getListener().getTabListSize(), login.getLevelType(), login.getViewDistance(), login.getSimulationDistance(), login.isReducedDebugInfo(), login.isNormalRespawn(), login.isDebug(), login.isFlat(), login.getDeathLocation(), login.getPortalCooldown());
      this.user.unsafe().sendPacket((DefinedPacket)modLogin);
      if (login.getDimension() == this.user.getDimension())
        this.user.unsafe().sendPacket((DefinedPacket)new Respawn(Integer.valueOf((((Integer)login.getDimension()).intValue() >= 0) ? -1 : 0), login.getWorldName(), login.getSeed(), login.getDifficulty(), login.getGameMode(), login.getPreviousGameMode(), login.getLevelType(), login.isDebug(), login.isFlat(), false, login.getDeathLocation(), login.getPortalCooldown())); 
      this.user.unsafe().sendPacket((DefinedPacket)new Respawn(login.getDimension(), login.getWorldName(), login.getSeed(), login.getDifficulty(), login.getGameMode(), login.getPreviousGameMode(), login.getLevelType(), login.isDebug(), login.isFlat(), false, login
            .getDeathLocation(), login.getPortalCooldown()));
      if (this.user.getPendingConnection().getVersion() >= 477)
        this.user.unsafe().sendPacket((DefinedPacket)new ViewDistance(login.getViewDistance())); 
      this.user.setDimension(login.getDimension());
      this.user.getServer().disconnect("Quitting");
    } 
    if (!this.user.isActive()) {
      server.disconnect("Quitting");
      this.bungee.getLogger().warning("No client connected for pending server!");
      return;
    } 
    this.target.addPlayer(this.user);
    this.user.getPendingConnects().remove(this.target);
    this.user.setServerJoinQueue(null);
    this.user.setDimensionChange(false);
    ServerInfo from = (this.user.getServer() == null) ? null : this.user.getServer().getInfo();
    this.user.setServer(server);
    ((HandlerBoss)this.ch.getHandle().pipeline().get(HandlerBoss.class)).setHandler((PacketHandler)new DownstreamBridge(this.bungee, this.user, server));
    this.bungee.getPluginManager().callEvent((Event)new ServerSwitchEvent(this.user, from));
    this.thisState = State.FINISHED;
    throw CancelSendSignal.INSTANCE;
  }
  
  public void handle(EncryptionRequest encryptionRequest) throws Exception {
    throw new QuietException("Server is online mode!");
  }
  
  public void handle(Kick kick) throws Exception {
    ServerInfo def = this.user.updateAndGetNextServer(this.target);
    ServerKickEvent event = new ServerKickEvent(this.user, this.target, ComponentSerializer.parse(kick.getMessage()), def, ServerKickEvent.State.CONNECTING, ServerKickEvent.Cause.SERVER);
    if (event.getKickReason().toLowerCase(Locale.ROOT).contains("outdated") && def != null)
      event.setCancelled(true); 
    this.bungee.getPluginManager().callEvent((Event)event);
    if (event.isCancelled() && event.getCancelServer() != null) {
      this.obsolete = true;
      this.user.connect(event.getCancelServer(), ServerConnectEvent.Reason.KICK_REDIRECT);
      throw CancelSendSignal.INSTANCE;
    } 
    String message = this.bungee.getTranslation("connect_kick", new Object[] { this.target.getName(), event.getKickReason() });
    if (this.user.isDimensionChange()) {
      this.user.disconnect(message);
    } else {
      this.user.sendMessage(message);
    } 
    throw CancelSendSignal.INSTANCE;
  }
  
  public void handle(PluginMessage pluginMessage) throws Exception {
    if ((BungeeCord.getInstance()).config.isForgeSupport()) {
      if (pluginMessage.getTag().equals("REGISTER")) {
        Set<String> channels = ForgeUtils.readRegisteredChannels(pluginMessage);
        boolean isForgeServer = false;
        for (String channel : channels) {
          if (channel.equals("FML|HS")) {
            if (this.user.getServer() != null && this.user.getForgeClientHandler().isHandshakeComplete())
              this.user.getForgeClientHandler().resetHandshake(); 
            isForgeServer = true;
            break;
          } 
        } 
        if (isForgeServer && !this.handshakeHandler.isServerForge()) {
          this.handshakeHandler.setServerAsForgeServer();
          this.user.setForgeServerHandler(this.handshakeHandler);
        } 
      } 
      if (pluginMessage.getTag().equals("FML|HS") || pluginMessage.getTag().equals("FORGE")) {
        this.handshakeHandler.handle(pluginMessage);
        if (this.user.getForgeClientHandler().checkUserOutdated()) {
          this.ch.close();
          this.user.getPendingConnects().remove(this.target);
        } 
        throw CancelSendSignal.INSTANCE;
      } 
    } 
    this.user.unsafe().sendPacket((DefinedPacket)pluginMessage);
  }
  
  public void handle(LoginPayloadRequest loginPayloadRequest) {
    this.ch.write(new LoginPayloadResponse(loginPayloadRequest.getId(), null));
  }
  
  public String toString() {
    return "[" + this.user.getName() + "|" + this.user.getAddress() + "] <-> ServerConnector [" + this.target.getName() + "]";
  }
}
