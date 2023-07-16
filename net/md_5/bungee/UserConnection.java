package net.md_5.bungee;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import dev._2lstudios.flamecord.FlameCord;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;
import lombok.NonNull;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.SkinConfiguration;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.forge.ForgeClientHandler;
import net.md_5.bungee.forge.ForgeServerHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.SystemChat;
import net.md_5.bungee.protocol.packet.Title;
import net.md_5.bungee.tab.ServerUnique;
import net.md_5.bungee.tab.TabList;
import net.md_5.bungee.util.CaseInsensitiveSet;
import net.md_5.bungee.util.ChatComponentTransformer;
import net.md_5.bungee.util.QuietException;

public final class UserConnection implements ProxiedPlayer {
  @NonNull
  private final ProxyServer bungee;
  
  @NonNull
  private final ChannelWrapper ch;
  
  @NonNull
  private final String name;
  
  private final InitialHandler pendingConnection;
  
  private ServerConnection server;
  
  private Object dimension;
  
  public UserConnection(@NonNull ProxyServer bungee, @NonNull ChannelWrapper ch, @NonNull String name, InitialHandler pendingConnection) {
    if (bungee == null)
      throw new NullPointerException("bungee is marked non-null but is null"); 
    if (ch == null)
      throw new NullPointerException("ch is marked non-null but is null"); 
    if (name == null)
      throw new NullPointerException("name is marked non-null but is null"); 
    this.bungee = bungee;
    this.ch = ch;
    this.name = name;
    this.pendingConnection = pendingConnection;
  }
  
  @NonNull
  public ChannelWrapper getCh() {
    return this.ch;
  }
  
  @NonNull
  public String getName() {
    return this.name;
  }
  
  public InitialHandler getPendingConnection() {
    return this.pendingConnection;
  }
  
  public ServerConnection getServer() {
    return this.server;
  }
  
  public void setServer(ServerConnection server) {
    this.server = server;
  }
  
  public Object getDimension() {
    return this.dimension;
  }
  
  public void setDimension(Object dimension) {
    this.dimension = dimension;
  }
  
  private boolean dimensionChange = true;
  
  public boolean isDimensionChange() {
    return this.dimensionChange;
  }
  
  public void setDimensionChange(boolean dimensionChange) {
    this.dimensionChange = dimensionChange;
  }
  
  private final Collection<ServerInfo> pendingConnects = new HashSet<>();
  
  public Collection<ServerInfo> getPendingConnects() {
    return this.pendingConnects;
  }
  
  private int ping = 100;
  
  private ServerInfo reconnectServer;
  
  private TabList tabListHandler;
  
  private int gamemode;
  
  public int getPing() {
    return this.ping;
  }
  
  public void setPing(int ping) {
    this.ping = ping;
  }
  
  public ServerInfo getReconnectServer() {
    return this.reconnectServer;
  }
  
  public void setReconnectServer(ServerInfo reconnectServer) {
    this.reconnectServer = reconnectServer;
  }
  
  public TabList getTabListHandler() {
    return this.tabListHandler;
  }
  
  public int getGamemode() {
    return this.gamemode;
  }
  
  public void setGamemode(int gamemode) {
    this.gamemode = gamemode;
  }
  
  private int compressionThreshold = -1;
  
  private Queue<String> serverJoinQueue;
  
  public int getCompressionThreshold() {
    return this.compressionThreshold;
  }
  
  public void setServerJoinQueue(Queue<String> serverJoinQueue) {
    this.serverJoinQueue = serverJoinQueue;
  }
  
  private final Collection<String> groups = (Collection<String>)new CaseInsensitiveSet();
  
  private final Collection<String> permissions = (Collection<String>)new CaseInsensitiveSet();
  
  private int clientEntityId;
  
  private int serverEntityId;
  
  private ClientSettings settings;
  
  public int getClientEntityId() {
    return this.clientEntityId;
  }
  
  public void setClientEntityId(int clientEntityId) {
    this.clientEntityId = clientEntityId;
  }
  
  public int getServerEntityId() {
    return this.serverEntityId;
  }
  
  public void setServerEntityId(int serverEntityId) {
    this.serverEntityId = serverEntityId;
  }
  
  public ClientSettings getSettings() {
    return this.settings;
  }
  
  private final Scoreboard serverSentScoreboard = new Scoreboard();
  
  public Scoreboard getServerSentScoreboard() {
    return this.serverSentScoreboard;
  }
  
  private final Collection<UUID> sentBossBars = new HashSet<>();
  
  private String lastCommandTabbed;
  
  public Collection<UUID> getSentBossBars() {
    return this.sentBossBars;
  }
  
  public String getLastCommandTabbed() {
    return this.lastCommandTabbed;
  }
  
  public void setLastCommandTabbed(String lastCommandTabbed) {
    this.lastCommandTabbed = lastCommandTabbed;
  }
  
  public Multimap<Integer, Integer> getPotions() {
    return this.potions;
  }
  
  private final Multimap<Integer, Integer> potions = (Multimap<Integer, Integer>)HashMultimap.create();
  
  private String displayName;
  
  private Locale locale;
  
  private ForgeClientHandler forgeClientHandler;
  
  private ForgeServerHandler forgeServerHandler;
  
  public String getDisplayName() {
    return this.displayName;
  }
  
  public ForgeClientHandler getForgeClientHandler() {
    return this.forgeClientHandler;
  }
  
  public void setForgeClientHandler(ForgeClientHandler forgeClientHandler) {
    this.forgeClientHandler = forgeClientHandler;
  }
  
  public ForgeServerHandler getForgeServerHandler() {
    return this.forgeServerHandler;
  }
  
  public void setForgeServerHandler(ForgeServerHandler forgeServerHandler) {
    this.forgeServerHandler = forgeServerHandler;
  }
  
  private final Connection.Unsafe unsafe = new Connection.Unsafe() {
      public void sendPacket(DefinedPacket packet) {
        UserConnection.this.ch.write(packet);
      }
    };
  
  public void init() {
    this.displayName = this.name;
    this.tabListHandler = (TabList)new ServerUnique(this);
    Collection<String> g = this.bungee.getConfigurationAdapter().getGroups(this.name);
    g.addAll(this.bungee.getConfigurationAdapter().getGroups(getUniqueId().toString()));
    for (String s : g) {
      addGroups(new String[] { s });
    } 
    this.forgeClientHandler = new ForgeClientHandler(this);
    if (getPendingConnection().getExtraDataInHandshake().contains("\000FML\000"))
      this.forgeClientHandler.setFmlTokenInHandshake(true); 
  }
  
  public void sendPacket(PacketWrapper packet) {
    this.ch.write(packet);
  }
  
  @Deprecated
  public boolean isActive() {
    return !this.ch.isClosed();
  }
  
  public void setDisplayName(String name) {
    Preconditions.checkNotNull(name, "displayName");
    this.displayName = name;
  }
  
  public void connect(ServerInfo target) {
    connect(target, (Callback<Boolean>)null, ServerConnectEvent.Reason.PLUGIN);
  }
  
  public void connect(ServerInfo target, ServerConnectEvent.Reason reason) {
    connect(target, (Callback<Boolean>)null, false, reason);
  }
  
  public void connect(ServerInfo target, Callback<Boolean> callback) {
    connect(target, callback, false, ServerConnectEvent.Reason.PLUGIN);
  }
  
  public void connect(ServerInfo target, Callback<Boolean> callback, ServerConnectEvent.Reason reason) {
    connect(target, callback, false, reason);
  }
  
  @Deprecated
  public void connectNow(ServerInfo target) {
    connectNow(target, ServerConnectEvent.Reason.UNKNOWN);
  }
  
  public void connectNow(ServerInfo target, ServerConnectEvent.Reason reason) {
    this.dimensionChange = true;
    connect(target, reason);
  }
  
  public ServerInfo updateAndGetNextServer(ServerInfo currentTarget) {
    if (this.serverJoinQueue == null)
      this.serverJoinQueue = new LinkedList<>(getPendingConnection().getListener().getServerPriority()); 
    ServerInfo next = null;
    while (!this.serverJoinQueue.isEmpty()) {
      ServerInfo candidate = ProxyServer.getInstance().getServerInfo(this.serverJoinQueue.remove());
      if (!Objects.equals(currentTarget, candidate)) {
        next = candidate;
        break;
      } 
    } 
    return next;
  }
  
  public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry) {
    connect(info, callback, retry, ServerConnectEvent.Reason.PLUGIN);
  }
  
  public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry, ServerConnectEvent.Reason reason) {
    connect(info, callback, retry, reason, this.bungee.getConfig().getServerConnectTimeout());
  }
  
  public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry, int timeout) {
    connect(info, callback, retry, ServerConnectEvent.Reason.PLUGIN, timeout);
  }
  
  public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry, ServerConnectEvent.Reason reason, int timeout) {
    connect(info, callback, retry, reason, timeout, true);
  }
  
  public void connect(ServerInfo info, final Callback<Boolean> callback, boolean retry, ServerConnectEvent.Reason reason, int timeout, boolean sendFeedback) {
    Preconditions.checkNotNull(info, "info");
    ServerConnectRequest.Builder builder = ServerConnectRequest.builder().retry(retry).reason(reason).target(info).sendFeedback(sendFeedback);
    builder.connectTimeout(timeout);
    if (callback != null)
      builder.callback(new Callback<ServerConnectRequest.Result>() {
            public void done(ServerConnectRequest.Result result, Throwable error) {
              callback.done((result == ServerConnectRequest.Result.SUCCESS) ? Boolean.TRUE : Boolean.FALSE, error);
            }
          }); 
    connect(builder.build());
  }
  
  public void connect(final ServerConnectRequest request) {
    Preconditions.checkNotNull(request, "request");
    final Callback<ServerConnectRequest.Result> callback = request.getCallback();
    ServerConnectEvent event = new ServerConnectEvent(this, request.getTarget(), request.getReason(), request);
    if (((ServerConnectEvent)this.bungee.getPluginManager().callEvent((Event)event)).isCancelled()) {
      if (callback != null)
        callback.done(ServerConnectRequest.Result.EVENT_CANCEL, null); 
      if (getServer() == null && !this.ch.isClosing())
        throw new QuietException("A plugin cancelled ServerConnectEvent with no server or disconnect."); 
      return;
    } 
    final BungeeServerInfo target = (BungeeServerInfo)event.getTarget();
    if (getServer() != null && Objects.equals(getServer().getInfo(), target)) {
      if (callback != null)
        callback.done(ServerConnectRequest.Result.ALREADY_CONNECTED, null); 
      if (request.isSendFeedback())
        sendMessage(this.bungee.getTranslation("already_connected", new Object[0])); 
      return;
    } 
    if (this.pendingConnects.contains(target)) {
      if (callback != null)
        callback.done(ServerConnectRequest.Result.ALREADY_CONNECTING, null); 
      if (request.isSendFeedback())
        sendMessage(this.bungee.getTranslation("already_connecting", new Object[0])); 
      return;
    } 
    this.pendingConnects.add(target);
    ChannelInitializer initializer = new ChannelInitializer() {
        protected void initChannel(Channel ch) throws Exception {
          PipelineUtils.BASE_SERVERSIDE.initChannel(ch);
          ch.pipeline().addAfter("frame-decoder", "packet-decoder", (ChannelHandler)new MinecraftDecoder(Protocol.HANDSHAKE, false, UserConnection.this.getPendingConnection().getVersion()));
          ch.pipeline().addAfter("frame-prepender", "packet-encoder", (ChannelHandler)new MinecraftEncoder(Protocol.HANDSHAKE, false, UserConnection.this.getPendingConnection().getVersion()));
          ((HandlerBoss)ch.pipeline().get(HandlerBoss.class)).setHandler(new ServerConnector(UserConnection.this.bungee, UserConnection.this, target));
        }
      };
    ChannelFutureListener listener = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture future) throws Exception {
          if (callback != null)
            callback.done(future.isSuccess() ? ServerConnectRequest.Result.SUCCESS : ServerConnectRequest.Result.FAIL, future.cause()); 
          if (!future.isSuccess()) {
            future.channel().close();
            UserConnection.this.pendingConnects.remove(target);
            InetSocketAddress targetAddress = target.getAddress();
            InetAddress targetInetAddress = targetAddress.getAddress();
            InetSocketAddress updated = (InetSocketAddress)Util.getAddr(targetAddress.getHostName() + ":" + targetAddress.getPort());
            InetAddress updatedAddress = updated.getAddress();
            if (updatedAddress != null && targetInetAddress != null && !updatedAddress.getHostAddress().equals(targetInetAddress.getHostAddress())) {
              ProxyServer.getInstance().getConfig().updateServerIPs();
              UserConnection.this.connect(ProxyServer.getInstance().getServerInfo(target.getName()), (Callback<Boolean>)null, false, ServerConnectEvent.Reason.UNKNOWN);
            } else {
              ServerInfo def = UserConnection.this.updateAndGetNextServer(target);
              if (request.isRetry() && def != null && (UserConnection.this.getServer() == null || def != UserConnection.this.getServer().getInfo())) {
                UserConnection.this.sendMessage(UserConnection.this.bungee.getTranslation("fallback_lobby", new Object[0]));
                UserConnection.this.connect(def, (Callback<Boolean>)null, true, ServerConnectEvent.Reason.LOBBY_FALLBACK);
              } else if (UserConnection.this.dimensionChange) {
                UserConnection.this.disconnect(UserConnection.this.bungee.getTranslation("fallback_kick", new Object[] { future.cause().getClass().getName() }));
              } else {
                UserConnection.this.sendMessage(UserConnection.this.bungee.getTranslation("fallback_kick", new Object[] { future.cause().getClass().getName() }));
              } 
            } 
          } 
        }
      };
    Bootstrap b = ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).channelFactory(PipelineUtils.getChannelFactory(target.getAddress()))).group((EventLoopGroup)this.ch.getHandle().eventLoop())).handler((ChannelHandler)initializer)).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf(request.getConnectTimeout()))).remoteAddress(target.getAddress());
    ListenerInfo listenerInfo = getPendingConnection().getListener();
    if (listenerInfo.isSetLocalAddress() && !PlatformDependent.isWindows() && listenerInfo.getSocketAddress() instanceof InetSocketAddress)
      b.localAddress(listenerInfo.getHost().getHostString(), 0); 
    b.connect().addListener((GenericFutureListener)listener);
  }
  
  private String connectionFailMessage(Throwable cause) {
    if (FlameCord.getInstance().getFlameCordConfiguration().isLoggerDetailedConnection()) {
      this.bungee.getLogger().log(Level.WARNING, "Error occurred processing connection for " + this.name + " " + Util.exception(cause, false));
      return this.groups.contains("admin") ? cause.getMessage() : cause.getClass().getName();
    } 
    return cause.getClass().getName();
  }
  
  public void disconnect(String reason) {
    disconnect0(TextComponent.fromLegacyText(reason));
  }
  
  public void disconnect(BaseComponent... reason) {
    disconnect0(reason);
  }
  
  public void disconnect(BaseComponent reason) {
    disconnect0(new BaseComponent[] { reason });
  }
  
  public void disconnect0(BaseComponent... reason) {
    if (!this.ch.isClosing()) {
      this.bungee.getLogger().log(Level.INFO, "[{0}] disconnected with: {1}", new Object[] { getName(), BaseComponent.toLegacyText(reason) });
      this.ch.close(new Kick(ComponentSerializer.toString(reason)));
      if (this.server != null) {
        this.server.setObsolete(true);
        this.server.disconnect("Quitting");
      } 
    } 
  }
  
  public void chat(String message) {
    Preconditions.checkState((this.server != null), "Not connected to server");
    if (getPendingConnection().getVersion() >= 759)
      throw new UnsupportedOperationException("Cannot spoof chat on this client version!"); 
    this.server.getCh().write(new Chat(message));
  }
  
  public void sendMessage(String message) {
    if (!message.equals(""))
      sendMessage(TextComponent.fromLegacyText(message)); 
  }
  
  public void sendMessages(String... messages) {
    for (String message : messages)
      sendMessage(message); 
  }
  
  public void sendMessage(BaseComponent... message) {
    sendMessage(ChatMessageType.SYSTEM, message);
  }
  
  public void sendMessage(BaseComponent message) {
    sendMessage(ChatMessageType.SYSTEM, message);
  }
  
  public void sendMessage(ChatMessageType position, BaseComponent... message) {
    sendMessage(position, (UUID)null, message);
  }
  
  public void sendMessage(ChatMessageType position, BaseComponent message) {
    sendMessage(position, (UUID)null, new BaseComponent[] { message });
  }
  
  public void sendMessage(UUID sender, BaseComponent... message) {
    sendMessage(ChatMessageType.CHAT, sender, message);
  }
  
  public void sendMessage(UUID sender, BaseComponent message) {
    sendMessage(ChatMessageType.CHAT, sender, new BaseComponent[] { message });
  }
  
  private void sendMessage(ChatMessageType position, UUID sender, String message) {
    if (getPendingConnection().getVersion() >= 759) {
      if (position == ChatMessageType.CHAT)
        position = ChatMessageType.SYSTEM; 
      unsafe().sendPacket((DefinedPacket)new SystemChat(message, position.ordinal()));
    } else {
      unsafe().sendPacket((DefinedPacket)new Chat(message, (byte)position.ordinal(), sender));
    } 
  }
  
  private void sendMessage(ChatMessageType position, UUID sender, BaseComponent... message) {
    message = ChatComponentTransformer.getInstance().transform(this, true, message);
    if (position == ChatMessageType.ACTION_BAR && getPendingConnection().getVersion() < 755 && getPendingConnection().getVersion() >= 47) {
      if (getPendingConnection().getVersion() <= 210) {
        sendMessage(position, sender, ComponentSerializer.toString((BaseComponent)new TextComponent(BaseComponent.toLegacyText(message))));
      } else {
        Title title = new Title();
        title.setAction(Title.Action.ACTIONBAR);
        title.setText(ComponentSerializer.toString(message));
        this.unsafe.sendPacket((DefinedPacket)title);
      } 
    } else {
      sendMessage(position, sender, ComponentSerializer.toString(message));
    } 
  }
  
  public void sendData(String channel, byte[] data) {
    unsafe().sendPacket((DefinedPacket)new PluginMessage(channel, data, this.forgeClientHandler.isForgeUser()));
  }
  
  public InetSocketAddress getAddress() {
    return (InetSocketAddress)getSocketAddress();
  }
  
  public SocketAddress getSocketAddress() {
    return this.ch.getRemoteAddress();
  }
  
  public Collection<String> getGroups() {
    return Collections.unmodifiableCollection(this.groups);
  }
  
  public void addGroups(String... groups) {
    for (String group : groups) {
      this.groups.add(group);
      for (String permission : this.bungee.getConfigurationAdapter().getPermissions(group))
        setPermission(permission, true); 
    } 
  }
  
  public void removeGroups(String... groups) {
    for (String group : groups) {
      this.groups.remove(group);
      for (String permission : this.bungee.getConfigurationAdapter().getPermissions(group))
        setPermission(permission, false); 
    } 
  }
  
  public boolean hasPermission(String permission) {
    return ((PermissionCheckEvent)this.bungee.getPluginManager().callEvent((Event)new PermissionCheckEvent((CommandSender)this, permission, this.permissions.contains(permission)))).hasPermission();
  }
  
  public void setPermission(String permission, boolean value) {
    if (value) {
      this.permissions.add(permission);
    } else {
      this.permissions.remove(permission);
    } 
  }
  
  public Collection<String> getPermissions() {
    return Collections.unmodifiableCollection(this.permissions);
  }
  
  public String toString() {
    return this.name;
  }
  
  public Connection.Unsafe unsafe() {
    return this.unsafe;
  }
  
  public String getUUID() {
    return getPendingConnection().getUUID();
  }
  
  public UUID getUniqueId() {
    return getPendingConnection().getUniqueId();
  }
  
  public void setSettings(ClientSettings settings) {
    this.settings = settings;
    this.locale = null;
  }
  
  public Locale getLocale() {
    return (this.locale == null && this.settings != null) ? (this.locale = Locale.forLanguageTag(this.settings.getLocale().replace('_', '-'))) : this.locale;
  }
  
  public byte getViewDistance() {
    return (this.settings != null) ? this.settings.getViewDistance() : 10;
  }
  
  public ProxiedPlayer.ChatMode getChatMode() {
    if (this.settings == null)
      return ProxiedPlayer.ChatMode.SHOWN; 
    switch (this.settings.getChatFlags()) {
      default:
        return ProxiedPlayer.ChatMode.SHOWN;
      case 1:
        return ProxiedPlayer.ChatMode.COMMANDS_ONLY;
      case 2:
        break;
    } 
    return ProxiedPlayer.ChatMode.HIDDEN;
  }
  
  public boolean hasChatColors() {
    return (this.settings == null || this.settings.isChatColours());
  }
  
  public SkinConfiguration getSkinParts() {
    return (this.settings != null) ? new PlayerSkinConfiguration(this.settings.getSkinParts()) : PlayerSkinConfiguration.SKIN_SHOW_ALL;
  }
  
  public ProxiedPlayer.MainHand getMainHand() {
    return (this.settings == null || this.settings.getMainHand() == 1) ? ProxiedPlayer.MainHand.RIGHT : ProxiedPlayer.MainHand.LEFT;
  }
  
  public boolean isForgeUser() {
    return this.forgeClientHandler.isForgeUser();
  }
  
  public Map<String, String> getModList() {
    if (this.forgeClientHandler.getClientModList() == null)
      return (Map<String, String>)ImmutableMap.of(); 
    return (Map<String, String>)ImmutableMap.copyOf(this.forgeClientHandler.getClientModList());
  }
  
  public void setTabHeader(BaseComponent header, BaseComponent footer) {
    if (ProtocolConstants.isBeforeOrEq(this.pendingConnection.getVersion(), 5))
      return; 
    header = ChatComponentTransformer.getInstance().transform(this, true, new BaseComponent[] { header })[0];
    footer = ChatComponentTransformer.getInstance().transform(this, true, new BaseComponent[] { footer })[0];
    unsafe().sendPacket((DefinedPacket)new PlayerListHeaderFooter(
          ComponentSerializer.toString(header), 
          ComponentSerializer.toString(footer)));
  }
  
  public void setTabHeader(BaseComponent[] header, BaseComponent[] footer) {
    if (ProtocolConstants.isBeforeOrEq(this.pendingConnection.getVersion(), 5))
      return; 
    header = ChatComponentTransformer.getInstance().transform(this, true, header);
    footer = ChatComponentTransformer.getInstance().transform(this, true, footer);
    unsafe().sendPacket((DefinedPacket)new PlayerListHeaderFooter(
          ComponentSerializer.toString(header), 
          ComponentSerializer.toString(footer)));
  }
  
  public void resetTabHeader() {
    setTabHeader((BaseComponent)null, (BaseComponent)null);
  }
  
  public void sendTitle(Title title) {
    title.send(this);
  }
  
  public String getExtraDataInHandshake() {
    return getPendingConnection().getExtraDataInHandshake();
  }
  
  public void setCompressionThreshold(int compressionThreshold) {
    if (ProtocolConstants.isBeforeOrEq(this.pendingConnection.getVersion(), 5))
      return; 
    if (!this.ch.isClosing() && this.compressionThreshold == -1 && compressionThreshold >= 0) {
      this.compressionThreshold = compressionThreshold;
      this.unsafe.sendPacket((DefinedPacket)new SetCompression(compressionThreshold));
      this.ch.setCompressionThreshold(compressionThreshold);
    } 
  }
  
  public boolean isConnected() {
    return !this.ch.isClosed();
  }
  
  public Scoreboard getScoreboard() {
    return this.serverSentScoreboard;
  }
}
