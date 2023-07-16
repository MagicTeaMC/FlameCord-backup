package net.md_5.bungee.connection;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.antibot.AddressData;
import dev._2lstudios.flamecord.antibot.CheckManager;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import io.github.waterfallmc.waterfall.utils.UUIDUtils;
import io.netty.channel.ChannelHandler;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.EncryptionUtil;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.http.HttpClient;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.cipher.CipherDecoder;
import net.md_5.bungee.netty.cipher.CipherEncoder;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.LegacyHandshake;
import net.md_5.bungee.protocol.packet.LegacyPing;
import net.md_5.bungee.protocol.packet.LoginPayloadResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PingPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.util.AllowedCharacters;
import net.md_5.bungee.util.BufUtil;
import net.md_5.bungee.util.QuietException;

public class InitialHandler extends PacketHandler implements PendingConnection {
  public InitialHandler(BungeeCord bungee, ListenerInfo listener) {
    this.registeredChannels = new HashSet<>();
    this.thisState = State.HANDSHAKE;
    this.unsafe = new Connection.Unsafe() {
        public void sendPacket(DefinedPacket packet) {
          InitialHandler.this.ch.write(packet);
        }
      };
    this
      .onlineMode = (BungeeCord.getInstance()).config.isOnlineMode();
    this.extraDataInHandshake = "";
    this.processing = false;
    this.bungee = bungee;
    this.listener = listener;
  }
  
  private static final String MOJANG_AUTH_URL = System.getProperty("waterfall.auth.url", "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s%s");
  
  private final BungeeCord bungee;
  
  private ChannelWrapper ch;
  
  private final ListenerInfo listener;
  
  private Handshake handshake;
  
  private LoginRequest loginRequest;
  
  private EncryptionRequest request;
  
  private PluginMessage brandMessage;
  
  private final Set<String> registeredChannels;
  
  private State thisState;
  
  private final Connection.Unsafe unsafe;
  
  private boolean onlineMode;
  
  private InetSocketAddress virtualHost;
  
  private String name;
  
  private UUID uniqueId;
  
  private UUID offlineId;
  
  private LoginResult loginProfile;
  
  private boolean legacy;
  
  private String extraDataInHandshake;
  
  private boolean processing;
  
  public ListenerInfo getListener() {
    return this.listener;
  }
  
  public Handshake getHandshake() {
    return this.handshake;
  }
  
  public LoginRequest getLoginRequest() {
    return this.loginRequest;
  }
  
  public PluginMessage getBrandMessage() {
    return this.brandMessage;
  }
  
  public Set<String> getRegisteredChannels() {
    return this.registeredChannels;
  }
  
  public boolean isOnlineMode() {
    return this.onlineMode;
  }
  
  public InetSocketAddress getVirtualHost() {
    return this.virtualHost;
  }
  
  public UUID getUniqueId() {
    return this.uniqueId;
  }
  
  public UUID getOfflineId() {
    return this.offlineId;
  }
  
  public LoginResult getLoginProfile() {
    return this.loginProfile;
  }
  
  public boolean isLegacy() {
    return this.legacy;
  }
  
  public String getExtraDataInHandshake() {
    return this.extraDataInHandshake;
  }
  
  public boolean shouldHandle(PacketWrapper packet) throws Exception {
    return !this.ch.isClosing();
  }
  
  private enum State {
    HANDSHAKE, STATUS, PING, USERNAME, ENCRYPT, FINISHING;
  }
  
  private boolean canSendKickMessage() {
    return (this.thisState == State.USERNAME || this.thisState == State.ENCRYPT || this.thisState == State.FINISHING);
  }
  
  public void connected(ChannelWrapper channel) throws Exception {
    this.ch = channel;
  }
  
  public void exception(Throwable t) throws Exception {
    if (canSendKickMessage()) {
      disconnect(ChatColor.RED + Util.exception(t));
    } else {
      this.ch.close();
    } 
  }
  
  public void handle(PacketWrapper packet) throws Exception {
    if (packet.packet == null)
      throw new QuietException("Unexpected packet received during initial handler process!\n" + BufUtil.dump(packet.buf, 16)); 
  }
  
  public void handle(PluginMessage pluginMessage) throws Exception {
    try {
      relayMessage(pluginMessage);
    } catch (IllegalStateException|IllegalArgumentException ex) {
      if (MinecraftDecoder.DEBUG)
        throw ex; 
      throw new QuietException(ex.getMessage());
    } 
  }
  
  public void handle(LegacyHandshake legacyHandshake) throws Exception {
    this.legacy = true;
    this.ch.close(this.bungee.getTranslation("outdated_client", new Object[] { this.bungee.getGameVersion() }));
  }
  
  public void handle(LegacyPing ping) throws Exception {
    this.legacy = true;
    final boolean v1_5 = ping.isV1_5();
    ServerInfo forced = AbstractReconnectHandler.getForcedHost(this);
    String motd = (forced != null) ? forced.getMotd() : this.listener.getMotd();
    final int protocol = this.bungee.getProtocolVersion();
    Callback<ServerPing> pingBack = new Callback<ServerPing>() {
        public void done(ServerPing result, Throwable error) {
          if (error != null) {
            result = InitialHandler.this.getPingInfo(InitialHandler.this.bungee.getTranslation("ping_cannot_connect", new Object[0]), protocol);
            InitialHandler.this.bungee.getLogger().log(Level.WARNING, "Error pinging remote server", error);
          } 
          Callback<ProxyPingEvent> callback = new Callback<ProxyPingEvent>() {
              public void done(ProxyPingEvent result, Throwable error) {
                String kickMessage;
                if (InitialHandler.this.ch.isClosed())
                  return; 
                ServerPing legacy = result.getResponse();
                if (legacy == null) {
                  InitialHandler.this.ch.close();
                  return;
                } 
                if (v1_5) {
                  kickMessage = ChatColor.DARK_BLUE + "\000" + '' + Character.MIN_VALUE + legacy.getVersion().getName() + Character.MIN_VALUE + InitialHandler.getFirstLine(legacy.getDescription()) + Character.MIN_VALUE + ((legacy.getPlayers() != null) ? (String)Integer.valueOf(legacy.getPlayers().getOnline()) : "-1") + Character.MIN_VALUE + ((legacy.getPlayers() != null) ? (String)Integer.valueOf(legacy.getPlayers().getMax()) : "-1");
                } else {
                  kickMessage = ChatColor.stripColor(InitialHandler.getFirstLine(legacy.getDescription())) + '§' + ((legacy.getPlayers() != null) ? (String)Integer.valueOf(legacy.getPlayers().getOnline()) : "-1") + '§' + ((legacy.getPlayers() != null) ? (String)Integer.valueOf(legacy.getPlayers().getMax()) : "-1");
                } 
                InitialHandler.this.ch.close(kickMessage);
              }
            };
          InitialHandler.this.bungee.getPluginManager().callEvent((Event)new ProxyPingEvent(InitialHandler.this, result, callback));
        }
      };
    if (forced != null && this.listener.isPingPassthrough()) {
      ((BungeeServerInfo)forced).ping(pingBack, this.bungee.getProtocolVersion());
    } else {
      pingBack.done(getPingInfo(motd, protocol), null);
    } 
  }
  
  private static String getFirstLine(String str) {
    int pos = str.indexOf('\n');
    return (pos == -1) ? str : str.substring(0, pos);
  }
  
  private ServerPing getPingInfo(String motd, int protocol) {
    return new ServerPing(new ServerPing.Protocol(this.bungee
          .getName() + " " + this.bungee.getGameVersion(), protocol), new ServerPing.Players(this.listener
          .getMaxPlayers(), this.bungee.getOnlineCount(), null), motd, 
        (BungeeCord.getInstance()).config.getFaviconObject());
  }
  
  public void handle(StatusRequest statusRequest) throws Exception {
    Preconditions.checkState((this.thisState == State.STATUS), "Not expecting STATUS");
    Preconditions.checkState(!this.processing, "Cannot request STATUS while processing another packet");
    this.processing = true;
    ServerInfo forced = AbstractReconnectHandler.getForcedHost(this);
    final int protocol = ProtocolConstants.SUPPORTED_VERSION_IDS.contains(Integer.valueOf(this.handshake.getProtocolVersion())) ? this.handshake.getProtocolVersion() : this.bungee.getProtocolVersion();
    Callback<ServerPing> pingBack = new Callback<ServerPing>() {
        public void done(ServerPing result, Throwable error) {
          if (error != null) {
            result = InitialHandler.this.getPingInfo(InitialHandler.this.bungee.getTranslation("ping_cannot_connect", new Object[0]), protocol);
            InitialHandler.this.bungee.getLogger().log(Level.WARNING, "Error pinging remote server", error);
          } 
          Callback<ProxyPingEvent> callback = new Callback<ProxyPingEvent>() {
              public void done(ProxyPingEvent pingResult, Throwable error) {
                if (pingResult.getResponse() == null) {
                  InitialHandler.this.ch.close();
                  return;
                } 
                if (InitialHandler.this.ch.isClosed())
                  return; 
                Gson gson = (InitialHandler.this.handshake.getProtocolVersion() == 4) ? (BungeeCord.getInstance()).gsonLegacy : (BungeeCord.getInstance()).gson;
                if (ProtocolConstants.isBeforeOrEq(InitialHandler.this.handshake.getProtocolVersion(), 47)) {
                  JsonElement element = gson.toJsonTree(pingResult.getResponse());
                  Preconditions.checkArgument(element.isJsonObject(), "Response is not a JSON object");
                  JsonObject object = element.getAsJsonObject();
                  object.addProperty("description", pingResult.getResponse().getDescription());
                  InitialHandler.this.unsafe.sendPacket((DefinedPacket)new StatusResponse(gson.toJson(element)));
                } else {
                  InitialHandler.this.unsafe.sendPacket((DefinedPacket)new StatusResponse(gson.toJson(pingResult.getResponse())));
                } 
                if (InitialHandler.this.bungee.getConnectionThrottle() != null)
                  InitialHandler.this.bungee.getConnectionThrottle().unthrottle(InitialHandler.this.getSocketAddress()); 
              }
            };
          InitialHandler.this.bungee.getPluginManager().callEvent((Event)new ProxyPingEvent(InitialHandler.this, result, callback));
        }
      };
    if (forced != null && this.listener.isPingPassthrough()) {
      ((BungeeServerInfo)forced).ping(pingBack, this.handshake.getProtocolVersion());
    } else {
      String motd, protocolName;
      FlameCordConfiguration config = FlameCord.getInstance().getFlameCordConfiguration();
      ServerPing.PlayerInfo[] sample = null;
      int maxPlayers = this.listener.getMaxPlayers();
      int onlinePlayers = this.bungee.getOnlineCount();
      if (config.isFakePlayersEnabled())
        onlinePlayers += config.getFakePlayersAmount(onlinePlayers); 
      if (config.isMaxPlayersEnabled())
        maxPlayers = config.isMaxPlayersOneMore() ? (onlinePlayers + 1) : config.getMaxPlayersAmount(); 
      if (config.isMotdEnabled()) {
        motd = config.getMOTD(maxPlayers, onlinePlayers, protocol);
      } else {
        motd = (forced != null) ? forced.getMotd() : this.listener.getMotd();
      } 
      if (config.isProtocolEnabled()) {
        protocolName = config.getProtocolName(maxPlayers, onlinePlayers);
      } else {
        protocolName = this.bungee.getName() + " " + this.bungee.getGameVersion();
      } 
      int customProtocol = (config.isProtocolEnabled() && config.isProtocolAlwaysShow()) ? -1 : protocol;
      if (config.isSampleEnabled()) {
        UUID fakeUuid = new UUID(0L, 0L);
        String[] sampleString = config.getSample(maxPlayers, onlinePlayers, protocol);
        sample = new ServerPing.PlayerInfo[sampleString.length];
        for (int i = 0; i < sampleString.length; i++)
          sample[i] = new ServerPing.PlayerInfo(sampleString[i], fakeUuid); 
      } 
      pingBack.done(new ServerPing(new ServerPing.Protocol(protocolName, customProtocol), new ServerPing.Players(maxPlayers, onlinePlayers, sample), motd, 
            
            (BungeeCord.getInstance()).config.getFaviconObject()), null);
    } 
    this.thisState = State.PING;
    this.processing = false;
  }
  
  private static final boolean ACCEPT_INVALID_PACKETS = Boolean.parseBoolean(System.getProperty("waterfall.acceptInvalidPackets", "false"));
  
  public void handle(PingPacket ping) throws Exception {
    Preconditions.checkState((this.thisState == State.PING), "Not expecting PING");
    Preconditions.checkState(!this.processing, "Cannot request PING while processing another packet");
    this.processing = true;
    this.unsafe.sendPacket((DefinedPacket)ping);
    this.ch.close();
  }
  
  public void handle(Handshake handshake) throws Exception {
    Preconditions.checkState((this.thisState == State.HANDSHAKE), "Not expecting HANDSHAKE");
    Preconditions.checkState(!this.processing, "Cannot request HANDSHAKE while processing another packet");
    this.processing = true;
    int protocol = handshake.getRequestedProtocol();
    if (protocol != 1 && protocol != 2) {
      if (FlameCord.getInstance().getFlameCordConfiguration().isAntibotRatelimitFirewall())
        FlameCord.getInstance().getAddressDataManager().getAddressData(this.ch.getChannel().remoteAddress()).firewall("Invalid handshake protocol"); 
      this.ch.close();
      return;
    } 
    this.handshake = handshake;
    this.ch.setVersion(handshake.getProtocolVersion());
    this.ch.getHandle().pipeline().remove("legacy-kick");
    if (handshake.getHost().contains("\000")) {
      String[] split = handshake.getHost().split("\000", 2);
      handshake.setHost(split[0]);
      this.extraDataInHandshake = "\000" + split[1];
    } 
    if (handshake.getHost().endsWith("."))
      handshake.setHost(handshake.getHost().substring(0, handshake.getHost().length() - 1)); 
    this.virtualHost = InetSocketAddress.createUnresolved(handshake.getHost(), handshake.getPort());
    SocketAddress remoteAddress = this.ch.getRemoteAddress();
    AddressData addressData = FlameCord.getInstance().getAddressDataManager().getAddressData(remoteAddress);
    CheckManager checkManager = FlameCord.getInstance().getCheckManager();
    if (protocol == 2) {
      this.thisState = State.USERNAME;
      this.ch.setProtocol(Protocol.LOGIN);
      addressData.addConnection();
      if (checkManager.getRatelimitCheck().check(remoteAddress, protocol)) {
        disconnect(this.bungee.getTranslation("antibot_ratelimit", new Object[] { Integer.valueOf(addressData.getConnectionsSecond()) }));
        return;
      } 
      if (checkManager.getProxyCheck().check(remoteAddress)) {
        disconnect(this.bungee.getTranslation("antibot_proxy", new Object[0]));
        return;
      } 
      if (checkManager.getReconnectCheck().check(remoteAddress)) {
        disconnect(this.bungee.getTranslation("antibot_reconnect", new Object[] { Integer.valueOf(Math.max(0, FlameCord.getInstance().getFlameCordConfiguration().getAntibotReconnectAttempts() - addressData.getTotalConnections())), Integer.valueOf(Math.max(0, FlameCord.getInstance().getFlameCordConfiguration().getAntibotReconnectPings() - addressData.getTotalPings())) }));
        return;
      } 
      if (checkManager.getCountryCheck().check(remoteAddress)) {
        disconnect(this.bungee.getTranslation("antibot_country", new Object[] { addressData.getCountry() }));
        return;
      } 
    } 
    this.thisState = State.HANDSHAKE;
    this.ch.setProtocol(Protocol.HANDSHAKE);
    if (((PlayerHandshakeEvent)this.bungee.getPluginManager().callEvent((Event)new PlayerHandshakeEvent(this, handshake))).isCancelled()) {
      this.ch.close();
      return;
    } 
    switch (handshake.getRequestedProtocol()) {
      case 1:
        if (this.bungee.getConfig().isLogPings() && FlameCord.getInstance().getFlameCordConfiguration().isLoggerInitialhandler())
          this.bungee.getLogger().log(Level.INFO, "{0} has pinged", this); 
        this.thisState = State.STATUS;
        this.ch.setProtocol(Protocol.STATUS);
        addressData.addPing();
        if (checkManager.getRatelimitCheck().check(remoteAddress, protocol)) {
          this.ch.close();
          return;
        } 
        this.processing = false;
        return;
      case 2:
        if (BungeeCord.getInstance().getConfig().isLogInitialHandlerConnections() && FlameCord.getInstance().getFlameCordConfiguration().isLoggerInitialhandler())
          this.bungee.getLogger().log(Level.INFO, "{0} has connected", this); 
        this.thisState = State.USERNAME;
        this.ch.setProtocol(Protocol.LOGIN);
        if (!ProtocolConstants.SUPPORTED_VERSION_IDS.contains(Integer.valueOf(handshake.getProtocolVersion()))) {
          if (handshake.getProtocolVersion() > this.bungee.getProtocolVersion()) {
            disconnect(this.bungee.getTranslation("outdated_server", new Object[] { this.bungee.getGameVersion() }));
          } else {
            disconnect(this.bungee.getTranslation("outdated_client", new Object[] { this.bungee.getGameVersion() }));
          } 
          return;
        } 
        this.processing = false;
        return;
    } 
    throw new QuietException("Cannot request protocol " + handshake.getRequestedProtocol());
  }
  
  public void handle(LoginRequest loginRequest) throws Exception {
    Preconditions.checkState((this.thisState == State.USERNAME), "Not expecting USERNAME");
    Preconditions.checkState(!this.processing, "Cannot request USERNAME while processing another packet");
    this.processing = true;
    if (!FlameCord.getInstance().getFlameCordConfiguration().isAllowInvalidNames() && !AllowedCharacters.isValidName(loginRequest.getData(), this.onlineMode)) {
      disconnect(this.bungee.getTranslation("name_invalid", new Object[0]));
      return;
    } 
    if ((BungeeCord.getInstance()).config.isEnforceSecureProfile() && getVersion() < 761) {
      if (this.handshake.getProtocolVersion() < 759)
        disconnect(this.bungee.getTranslation("secure_profile_unsupported", new Object[0])); 
      PlayerPublicKey publicKey = loginRequest.getPublicKey();
      if (publicKey == null) {
        disconnect(this.bungee.getTranslation("secure_profile_required", new Object[0]));
        return;
      } 
      if (Instant.ofEpochMilli(publicKey.getExpiry()).isBefore(Instant.now())) {
        disconnect(this.bungee.getTranslation("secure_profile_expired", new Object[0]));
        return;
      } 
      if (getVersion() < 760)
        if (!EncryptionUtil.check(publicKey, null)) {
          disconnect(this.bungee.getTranslation("secure_profile_invalid", new Object[0]));
          return;
        }  
    } 
    this.loginRequest = loginRequest;
    int limit = (BungeeCord.getInstance()).config.getPlayerLimit();
    if (limit > 0 && this.bungee.getOnlineCount() >= limit) {
      disconnect(this.bungee.getTranslation("proxy_full", new Object[0]));
      return;
    } 
    CheckManager checkManager = FlameCord.getInstance().getCheckManager();
    AddressData addressData = FlameCord.getInstance().getAddressDataManager().getAddressData(this.ch.getRemoteAddress());
    String nickname = loginRequest.getData();
    addressData.addNickname(nickname);
    if (checkManager.getAccountsCheck().check(this.ch.getRemoteAddress(), nickname)) {
      disconnect(this.bungee.getTranslation("antibot_accounts", new Object[] { Integer.valueOf(addressData.getNicknames().size()) }));
      return;
    } 
    if (checkManager.getNicknameCheck().check(this.ch.getRemoteAddress())) {
      disconnect(this.bungee.getTranslation("antibot_nickname", new Object[] { loginRequest.getData() }));
      return;
    } 
    if (!isOnlineMode() && this.bungee.getPlayer(getUniqueId()) != null) {
      disconnect(this.bungee.getTranslation("already_connected_proxy", new Object[0]));
      return;
    } 
    Callback<PreLoginEvent> callback = new Callback<PreLoginEvent>() {
        public void done(PreLoginEvent result, Throwable error) {
          if (result.isCancelled()) {
            BaseComponent[] reason = result.getCancelReasonComponents();
            InitialHandler.this.disconnect((reason != null) ? reason : TextComponent.fromLegacyText(InitialHandler.this.bungee.getTranslation("kick_message", new Object[0])));
            return;
          } 
          if (InitialHandler.this.ch.isClosed())
            return; 
          if (InitialHandler.this.onlineMode) {
            InitialHandler.this.thisState = InitialHandler.State.ENCRYPT;
            InitialHandler.this.unsafe().sendPacket((DefinedPacket)(InitialHandler.this.request = EncryptionUtil.encryptRequest()));
          } else {
            InitialHandler.this.thisState = InitialHandler.State.FINISHING;
            InitialHandler.this.finish();
          } 
          InitialHandler.this.processing = false;
        }
      };
    this.bungee.getPluginManager().callEvent((Event)new PreLoginEvent(this, callback));
  }
  
  public void handle(EncryptionResponse encryptResponse) throws Exception {
    Preconditions.checkState((this.thisState == State.ENCRYPT), "Not expecting ENCRYPT");
    Preconditions.checkState(EncryptionUtil.check(this.loginRequest.getPublicKey(), encryptResponse, this.request), "Invalid verification");
    this.thisState = State.FINISHING;
    Preconditions.checkState(!this.processing, "Cannot request ENCRYPT while processing another packet");
    this.processing = true;
    SecretKey sharedKey = EncryptionUtil.getSecret(encryptResponse, this.request);
    if (sharedKey instanceof javax.crypto.spec.SecretKeySpec && (
      sharedKey.getEncoded()).length != 16) {
      this.ch.close();
      return;
    } 
    BungeeCipher decrypt = EncryptionUtil.getCipher(false, sharedKey);
    this.ch.addBefore("frame-decoder", "decrypt", (ChannelHandler)new CipherDecoder(decrypt));
    BungeeCipher encrypt = EncryptionUtil.getCipher(true, sharedKey);
    this.ch.addBefore("frame-prepender", "encrypt", (ChannelHandler)new CipherEncoder(encrypt));
    String encName = URLEncoder.encode(getName(), "UTF-8");
    MessageDigest sha = MessageDigest.getInstance("SHA-1");
    for (byte[] bit : new byte[][] { this.request
        
        .getServerId().getBytes("ISO_8859_1"), sharedKey.getEncoded(), EncryptionUtil.keys.getPublic().getEncoded() })
      sha.update(bit); 
    String encodedHash = URLEncoder.encode((new BigInteger(sha.digest())).toString(16), "UTF-8");
    String preventProxy = ((BungeeCord.getInstance()).config.isPreventProxyConnections() && getSocketAddress() instanceof InetSocketAddress) ? ("&ip=" + URLEncoder.encode(getAddress().getAddress().getHostAddress(), "UTF-8")) : "";
    String authURL = String.format(MOJANG_AUTH_URL, new Object[] { encName, encodedHash, preventProxy });
    Callback<String> handler = new Callback<String>() {
        public void done(String result, Throwable error) {
          if (error == null) {
            LoginResult obj = (LoginResult)(BungeeCord.getInstance()).gson.fromJson(result, LoginResult.class);
            if (obj != null && obj.getId() != null) {
              InitialHandler.this.loginProfile = obj;
              InitialHandler.this.name = obj.getName();
              if (InitialHandler.this.uniqueId == null)
                InitialHandler.this.uniqueId = Util.getUUID(obj.getId()); 
              InitialHandler.this.finish();
              return;
            } 
            InitialHandler.this.disconnect(InitialHandler.this.bungee.getTranslation("offline_mode_player", new Object[0]));
          } else {
            InitialHandler.this.disconnect(InitialHandler.this.bungee.getTranslation("mojang_fail", new Object[0]));
            InitialHandler.this.bungee.getLogger().log(Level.SEVERE, "Error authenticating " + InitialHandler.this.getName() + " with minecraft.net", error);
          } 
        }
      };
    HttpClient.get(authURL, this.ch.getHandle().eventLoop(), handler);
  }
  
  private void finish() {
    this.offlineId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + getName()).getBytes(Charsets.UTF_8));
    if (this.uniqueId == null)
      this.uniqueId = this.offlineId; 
    if ((BungeeCord.getInstance()).config.isEnforceSecureProfile())
      if (getVersion() >= 760 && getVersion() < 761) {
        boolean secure = false;
        try {
          secure = EncryptionUtil.check(this.loginRequest.getPublicKey(), this.uniqueId);
        } catch (GeneralSecurityException generalSecurityException) {}
        if (!secure) {
          disconnect(this.bungee.getTranslation("secure_profile_invalid", new Object[0]));
          return;
        } 
      }  
    if (isOnlineMode()) {
      ProxiedPlayer oldName = this.bungee.getPlayer(getName());
      if (oldName != null)
        disconnect(this.bungee.getTranslation("already_connected_proxy", new Object[0])); 
      ProxiedPlayer oldID = this.bungee.getPlayer(getUniqueId());
      if (oldID != null)
        disconnect(this.bungee.getTranslation("already_connected_proxy", new Object[0])); 
    } else {
      ProxiedPlayer oldName = this.bungee.getPlayer(getName());
      if (oldName != null) {
        disconnect(this.bungee.getTranslation("already_connected_proxy", new Object[0]));
        return;
      } 
    } 
    Callback<LoginEvent> complete = new Callback<LoginEvent>() {
        public void done(LoginEvent result, Throwable error) {
          if (result.isCancelled()) {
            BaseComponent[] reason = result.getCancelReasonComponents();
            InitialHandler.this.disconnect((reason != null) ? reason : TextComponent.fromLegacyText(InitialHandler.this.bungee.getTranslation("kick_message", new Object[0])));
            return;
          } 
          if (InitialHandler.this.ch.isClosed())
            return; 
          InitialHandler.this.ch.getHandle().eventLoop().execute(new Runnable() {
                public void run() {
                  if (!InitialHandler.this.ch.isClosing()) {
                    ServerInfo server;
                    UserConnection userCon = new UserConnection((ProxyServer)InitialHandler.this.bungee, InitialHandler.this.ch, InitialHandler.this.getName(), InitialHandler.this);
                    userCon.setCompressionThreshold((BungeeCord.getInstance()).config.getCompressionThreshold());
                    userCon.init();
                    InitialHandler.this.unsafe.sendPacket((DefinedPacket)new LoginSuccess(InitialHandler.this.getUniqueId(), InitialHandler.this.getName(), (InitialHandler.this.loginProfile == null) ? null : InitialHandler.this.loginProfile.getProperties()));
                    InitialHandler.this.ch.setProtocol(Protocol.GAME);
                    ((HandlerBoss)InitialHandler.this.ch.getHandle().pipeline().get(HandlerBoss.class)).setHandler(new UpstreamBridge((ProxyServer)InitialHandler.this.bungee, userCon));
                    InitialHandler.this.bungee.getPluginManager().callEvent((Event)new PostLoginEvent((ProxiedPlayer)userCon));
                    if (InitialHandler.this.bungee.getReconnectHandler() != null) {
                      server = InitialHandler.this.bungee.getReconnectHandler().getServer((ProxiedPlayer)userCon);
                    } else {
                      server = AbstractReconnectHandler.getForcedHost(InitialHandler.this);
                    } 
                    if (server == null)
                      server = InitialHandler.this.bungee.getServerInfo(InitialHandler.this.listener.getDefaultServer()); 
                    userCon.connect(server, null, true, ServerConnectEvent.Reason.JOIN_PROXY);
                  } 
                }
              });
        }
      };
    this.bungee.getPluginManager().callEvent((Event)new LoginEvent(this, complete, getLoginProfile()));
  }
  
  public void handle(LoginPayloadResponse response) throws Exception {
    disconnect("Unexpected custom LoginPayloadResponse");
  }
  
  public void disconnect(String reason) {
    if (canSendKickMessage()) {
      disconnect(TextComponent.fromLegacyTextFast(reason));
    } else {
      this.ch.close();
    } 
  }
  
  public void disconnect(BaseComponent... reason) {
    if (canSendKickMessage()) {
      this.ch.delayedClose(new Kick(ComponentSerializer.toString(reason)));
    } else {
      this.ch.close();
    } 
  }
  
  public void disconnect(BaseComponent reason) {
    disconnect(new BaseComponent[] { reason });
  }
  
  public String getName() {
    return (this.name != null) ? this.name : ((this.loginRequest == null) ? null : this.loginRequest.getData());
  }
  
  public int getVersion() {
    return (this.handshake == null) ? -1 : this.handshake.getProtocolVersion();
  }
  
  public InetSocketAddress getAddress() {
    return (InetSocketAddress)getSocketAddress();
  }
  
  public SocketAddress getSocketAddress() {
    return this.ch.getRemoteAddress();
  }
  
  public Connection.Unsafe unsafe() {
    return this.unsafe;
  }
  
  public void setOnlineMode(boolean onlineMode) {
    Preconditions.checkState((this.thisState == State.USERNAME), "Can only set online mode status whilst state is username");
    this.onlineMode = onlineMode;
  }
  
  public void setUniqueId(UUID uuid) {
    Preconditions.checkState((this.thisState == State.USERNAME), "Can only set uuid while state is username");
    this.uniqueId = uuid;
  }
  
  public String getUUID() {
    return UUIDUtils.undash(this.uniqueId.toString());
  }
  
  public String toString() {
    return "[" + getSocketAddress() + ((getName() != null) ? ("|" + getName()) : "") + "] <-> InitialHandler";
  }
  
  public boolean isConnected() {
    return !this.ch.isClosed();
  }
  
  public void relayMessage(PluginMessage input) throws Exception {
    if (input.getTag().equals("REGISTER") || input.getTag().equals("minecraft:register")) {
      String content = new String(input.getData(), StandardCharsets.UTF_8);
      for (String id : content.split("\000")) {
        Preconditions.checkState((this.registeredChannels.size() <= this.bungee.getConfig().getPluginChannelLimit()), "Too many registered channels. This limit can be configured in the waterfall.yml");
        Preconditions.checkArgument((id.length() <= this.bungee.getConfig().getPluginChannelNameLimit()), "Channel name too long. This limit can be configured in the waterfall.yml");
        this.registeredChannels.add(id);
      } 
    } else if (input.getTag().equals("UNREGISTER") || input.getTag().equals("minecraft:unregister")) {
      String content = new String(input.getData(), StandardCharsets.UTF_8);
      for (String id : content.split("\000"))
        this.registeredChannels.remove(id); 
    } else if (input.getTag().equals("MC|Brand") || input.getTag().equals("minecraft:brand")) {
      this.brandMessage = input;
    } 
  }
}
