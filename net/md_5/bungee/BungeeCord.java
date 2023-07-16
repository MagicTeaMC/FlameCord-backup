package net.md_5.bungee;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.commands.BungeeIPCommand;
import dev._2lstudios.flamecord.commands.BungeePluginsCommand;
import dev._2lstudios.flamecord.commands.FlameCordCommand;
import dev._2lstudios.flamecord.configuration.ModulesConfiguration;
import dev._2lstudios.flamecord.natives.Natives;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.waterfallmc.waterfall.conf.WaterfallConfiguration;
import io.github.waterfallmc.waterfall.event.ProxyExceptionEvent;
import io.github.waterfallmc.waterfall.exception.ProxyException;
import io.github.waterfallmc.waterfall.exception.ProxyPluginEnableDisableException;
import io.github.waterfallmc.waterfall.log4j.WaterfallLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.KeybindComponentSerializer;
import net.md_5.bungee.chat.ScoreComponentSerializer;
import net.md_5.bungee.chat.SelectorComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;
import net.md_5.bungee.command.CommandBungee;
import net.md_5.bungee.command.CommandEnd;
import net.md_5.bungee.command.CommandIP;
import net.md_5.bungee.command.CommandPerms;
import net.md_5.bungee.command.CommandReload;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.module.ModuleManager;
import net.md_5.bungee.module.cmd.alert.CommandAlert;
import net.md_5.bungee.module.cmd.alert.CommandAlertRaw;
import net.md_5.bungee.module.cmd.find.CommandFind;
import net.md_5.bungee.module.cmd.list.CommandList;
import net.md_5.bungee.module.cmd.send.CommandSend;
import net.md_5.bungee.module.cmd.server.CommandServer;
import net.md_5.bungee.module.reconnect.yaml.YamlReconnectHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.query.RemoteQuery;
import net.md_5.bungee.scheduler.BungeeScheduler;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.apache.logging.log4j.LogManager;

public class BungeeCord extends ProxyServer {
  public volatile boolean isRunning;
  
  public final Configuration config = (Configuration)new WaterfallConfiguration();
  
  private Map<String, Format> messageFormats;
  
  public EventLoopGroup bossEventLoopGroup;
  
  public EventLoopGroup workerEventLoopGroup;
  
  public Configuration getConfig() {
    return this.config;
  }
  
  private final Timer saveThread = new Timer("Reconnect Saver");
  
  private final Collection<Channel> listeners = new HashSet<>();
  
  private final Map<String, UserConnection> connections = (Map<String, UserConnection>)new CaseInsensitiveMap();
  
  private final Map<UUID, UserConnection> connectionsByOfflineUUID = new HashMap<>();
  
  private final Map<UUID, UserConnection> connectionsByUUID = new HashMap<>();
  
  private final ReadWriteLock connectionLock = new ReentrantReadWriteLock();
  
  private final ReentrantLock shutdownLock = new ReentrantLock();
  
  public final PluginManager pluginManager;
  
  private ReconnectHandler reconnectHandler;
  
  public PluginManager getPluginManager() {
    return this.pluginManager;
  }
  
  public ReconnectHandler getReconnectHandler() {
    return this.reconnectHandler;
  }
  
  public void setReconnectHandler(ReconnectHandler reconnectHandler) {
    this.reconnectHandler = reconnectHandler;
  }
  
  private ConfigurationAdapter configurationAdapter = (ConfigurationAdapter)new YamlConfig();
  
  public ConfigurationAdapter getConfigurationAdapter() {
    return this.configurationAdapter;
  }
  
  public void setConfigurationAdapter(ConfigurationAdapter configurationAdapter) {
    this.configurationAdapter = configurationAdapter;
  }
  
  private final Collection<String> pluginChannels = new HashSet<>();
  
  private final File pluginsFolder = new File("plugins");
  
  public File getPluginsFolder() {
    return this.pluginsFolder;
  }
  
  private final BungeeScheduler scheduler = new BungeeScheduler();
  
  private final Logger logger;
  
  public BungeeScheduler getScheduler() {
    return this.scheduler;
  }
  
  public Logger getLogger() {
    return this.logger;
  }
  
  public final Gson gson = (new GsonBuilder())
    .registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
    .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
    .registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
    .registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer())
    .registerTypeAdapter(ScoreComponent.class, new ScoreComponentSerializer())
    .registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer())
    .registerTypeAdapter(ServerPing.PlayerInfo.class, new PlayerInfoSerializer())
    .registerTypeAdapter(Favicon.class, Favicon.getFaviconTypeAdapter()).create();
  
  public final Gson gsonLegacy = (new GsonBuilder())
    .registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
    .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
    .registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
    .registerTypeAdapter(ServerPing.PlayerInfo.class, new PlayerInfoSerializer(4))
    .registerTypeAdapter(Favicon.class, Favicon.getFaviconTypeAdapter()).create();
  
  private ConnectionThrottle connectionThrottle;
  
  public ConnectionThrottle getConnectionThrottle() {
    return this.connectionThrottle;
  }
  
  private final ModuleManager moduleManager = new ModuleManager();
  
  @SuppressFBWarnings({"DM_DEFAULT_ENCODING"})
  public BungeeCord() throws IOException {
    registerChannel("BungeeCord");
    Preconditions.checkState(((new File(".")).getAbsolutePath().indexOf('!') == -1), "Cannot use FlameCord in directory with ! in path.");
    reloadMessages();
    System.setProperty("library.jansi.version", "BungeeCord");
    this.logger = WaterfallLogger.create();
    this.pluginManager = new PluginManager(this);
    this.logger.log(Level.INFO, "FlameCord is using " + Natives.getCompressorFactory().getName() + " compression");
  }
  
  public static BungeeCord getInstance() {
    return (BungeeCord)ProxyServer.getInstance();
  }
  
  @SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
  public void start() throws Exception {
    System.setProperty("io.netty.selectorAutoRebuildThreshold", "0");
    if (System.getProperty("io.netty.leakDetectionLevel") == null && System.getProperty("io.netty.leakDetection.level") == null)
      ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED); 
    this.bossEventLoopGroup = PipelineUtils.newEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Boss IO Thread #%1$d").build());
    this.workerEventLoopGroup = PipelineUtils.newEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Worker IO Thread #%1$d").build());
    this.pluginsFolder.mkdir();
    this.pluginManager.detectPlugins(this.pluginsFolder);
    this.pluginManager.loadPlugins();
    this.config.load();
    Collection<String> whitelistedAddresses = new HashSet<>();
    for (ServerInfo serverInfo : getServers().values())
      whitelistedAddresses.add(serverInfo.getSocketAddress().toString()); 
    FlameCord.initialize(this.logger, whitelistedAddresses);
    if (this.config.isForgeSupport()) {
      registerChannel("FML");
      registerChannel("FML|HS");
      registerChannel("FORGE");
    } 
    this.isRunning = true;
    loadModules();
    this.pluginManager.enablePlugins();
    if (this.config.getThrottle() > 0)
      this.connectionThrottle = new ConnectionThrottle(this.config.getThrottle(), this.config.getThrottleLimit()); 
    startListeners();
    this.saveThread.scheduleAtFixedRate(new TimerTask() {
          public void run() {
            if (BungeeCord.this.getReconnectHandler() != null)
              BungeeCord.this.getReconnectHandler().save(); 
          }
        },  0L, TimeUnit.MINUTES.toMillis(5L));
    Runtime.getRuntime().addShutdownHook(new Thread() {
          public void run() {
            BungeeCord.this.independentThreadStop(BungeeCord.this.getTranslation("restart", new Object[0]), false);
          }
        });
  }
  
  public void startListeners() {
    for (ListenerInfo info : this.config.getListeners()) {
      if (info.isProxyProtocol()) {
        getLogger().log(Level.WARNING, "Using PROXY protocol for listener {0}, please ensure this listener is adequately firewalled.", info.getSocketAddress());
        if (this.connectionThrottle != null) {
          this.connectionThrottle = null;
          getLogger().log(Level.WARNING, "Since PROXY protocol is in use, internal connection throttle has been disabled.");
        } 
      } 
      ChannelFutureListener listener = new ChannelFutureListener() {
          public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
              BungeeCord.this.listeners.add(future.channel());
              BungeeCord.this.getLogger().log(Level.INFO, "Listening on {0}", info.getSocketAddress());
            } else {
              BungeeCord.this.getLogger().log(Level.WARNING, "Could not bind to host " + info.getSocketAddress(), future.cause());
            } 
          }
        };
      ((ServerBootstrap)((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap())
        .channelFactory(PipelineUtils.getServerChannelFactory(info.getSocketAddress())))
        .option(ChannelOption.SO_REUSEADDR, Boolean.valueOf(true)))
        .childAttr(PipelineUtils.LISTENER, info)
        .childHandler((ChannelHandler)PipelineUtils.SERVER_CHILD)
        .group(this.bossEventLoopGroup, this.workerEventLoopGroup)
        .localAddress(info.getSocketAddress()))
        .bind().addListener((GenericFutureListener)listener);
      if (info.isQueryEnabled()) {
        Preconditions.checkArgument(info.getSocketAddress() instanceof InetSocketAddress, "Can only create query listener on UDP address");
        ChannelFutureListener bindListener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
              if (future.isSuccess()) {
                BungeeCord.this.listeners.add(future.channel());
                BungeeCord.this.getLogger().log(Level.INFO, "Started query on {0}", future.channel().localAddress());
              } else {
                BungeeCord.this.getLogger().log(Level.WARNING, "Could not bind to host " + info.getSocketAddress(), future.cause());
              } 
            }
          };
        (new RemoteQuery(this, info)).start(PipelineUtils.getDatagramChannel(), new InetSocketAddress(info.getHost().getAddress(), info.getQueryPort()), this.workerEventLoopGroup, bindListener);
      } 
    } 
  }
  
  public void stopListeners() {
    for (Channel listener : this.listeners) {
      getLogger().log(Level.INFO, "Closing listener {0}", listener);
      try {
        listener.close().syncUninterruptibly();
      } catch (ChannelException ex) {
        getLogger().severe("Could not close listen thread");
      } 
    } 
    this.listeners.clear();
  }
  
  public void stop() {
    stop(getTranslation("restart", new Object[0]));
  }
  
  public void stop(final String reason) {
    (new Thread("Shutdown Thread") {
        public void run() {
          BungeeCord.this.independentThreadStop(reason, true);
        }
      }).start();
  }
  
  @SuppressFBWarnings({"DM_EXIT"})
  private void independentThreadStop(String reason, boolean callSystemExit) {
    this.shutdownLock.lock();
    if (!this.isRunning) {
      this.shutdownLock.unlock();
      return;
    } 
    this.isRunning = false;
    stopListeners();
    getLogger().info("Closing pending connections");
    this.connectionLock.readLock().lock();
    try {
      getLogger().log(Level.INFO, "Disconnecting {0} connections", Integer.valueOf(this.connections.size()));
      for (UserConnection user : this.connections.values())
        user.disconnect(reason); 
    } finally {
      this.connectionLock.readLock().unlock();
    } 
    try {
      Thread.sleep(500L);
    } catch (InterruptedException interruptedException) {}
    if (this.reconnectHandler != null) {
      getLogger().info("Saving reconnect locations");
      this.reconnectHandler.save();
      this.reconnectHandler.close();
    } 
    this.saveThread.cancel();
    getLogger().info("Disabling plugins");
    for (Plugin plugin : Lists.reverse(new ArrayList(this.pluginManager.getPlugins()))) {
      try {
        plugin.onDisable();
        for (Handler handler : plugin.getLogger().getHandlers())
          handler.close(); 
      } catch (Throwable t) {
        String msg = "Exception disabling plugin " + plugin.getDescription().getName();
        getLogger().log(Level.SEVERE, msg, t);
        this.pluginManager.callEvent((Event)new ProxyExceptionEvent((ProxyException)new ProxyPluginEnableDisableException(msg, t, plugin)));
      } 
      getScheduler().cancel(plugin);
      plugin.getExecutorService().shutdownNow();
    } 
    getLogger().info("Closing IO threads");
    this.bossEventLoopGroup.shutdownGracefully();
    this.workerEventLoopGroup.shutdownGracefully();
    while (true) {
      try {
        this.bossEventLoopGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        this.workerEventLoopGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        break;
      } catch (InterruptedException interruptedException) {}
    } 
    FlameCord.getInstance().shutdown();
    getLogger().info("Shutting down FlameCord linux command thread");
    getLogger().info("Thank you and goodbye");
    LogManager.shutdown();
    this.shutdownLock.unlock();
    if (callSystemExit)
      System.exit(0); 
  }
  
  public void broadcast(DefinedPacket packet) {
    this.connectionLock.readLock().lock();
    try {
      for (UserConnection con : this.connections.values())
        con.unsafe().sendPacket(packet); 
    } finally {
      this.connectionLock.readLock().unlock();
    } 
  }
  
  public String getName() {
    return "FlameCord";
  }
  
  public String getVersion() {
    return "1.5.8";
  }
  
  public final void reloadMessages() {
    ResourceBundle baseBundle;
    Map<String, Format> cachedFormats = new HashMap<>();
    File file = new File("messages.properties");
    if (file.isFile())
      try (FileReader rd = new FileReader(file)) {
        cacheResourceBundle(cachedFormats, new PropertyResourceBundle(rd));
      } catch (IOException ex) {
        getLogger().log(Level.SEVERE, "Could not load custom messages.properties", ex);
      }  
    try {
      baseBundle = ResourceBundle.getBundle("messages");
    } catch (MissingResourceException ex) {
      baseBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
    } 
    cacheResourceBundle(cachedFormats, baseBundle);
    this.messageFormats = Collections.unmodifiableMap(cachedFormats);
  }
  
  private void cacheResourceBundle(Map<String, Format> map, ResourceBundle resourceBundle) {
    Enumeration<String> keys = resourceBundle.getKeys();
    while (keys.hasMoreElements())
      map.computeIfAbsent(keys.nextElement(), key -> new MessageFormat(resourceBundle.getString(key))); 
  }
  
  public String getTranslation(String name, Object... args) {
    return FlameCord.getInstance().getMessagesConfiguration().getTranslation(name, args);
  }
  
  public Collection<ProxiedPlayer> getPlayers() {
    this.connectionLock.readLock().lock();
    try {
      return (Collection)Collections.unmodifiableCollection(new HashSet(this.connections.values()));
    } finally {
      this.connectionLock.readLock().unlock();
    } 
  }
  
  public int getOnlineCount() {
    return this.connections.size();
  }
  
  public ProxiedPlayer getPlayer(String name) {
    this.connectionLock.readLock().lock();
    try {
      return this.connections.get(name);
    } finally {
      this.connectionLock.readLock().unlock();
    } 
  }
  
  public UserConnection getPlayerByOfflineUUID(UUID uuid) {
    if (uuid.version() != 3)
      return null; 
    this.connectionLock.readLock().lock();
    try {
      return this.connectionsByOfflineUUID.get(uuid);
    } finally {
      this.connectionLock.readLock().unlock();
    } 
  }
  
  public ProxiedPlayer getPlayer(UUID uuid) {
    this.connectionLock.readLock().lock();
    try {
      return this.connectionsByUUID.get(uuid);
    } finally {
      this.connectionLock.readLock().unlock();
    } 
  }
  
  public Map<String, ServerInfo> getServers() {
    return (Map<String, ServerInfo>)this.config.getServers();
  }
  
  public Map<String, ServerInfo> getServersCopy() {
    return this.config.getServersCopy();
  }
  
  public ServerInfo getServerInfo(String name) {
    return this.config.getServerInfo(name);
  }
  
  public void registerChannel(String channel) {
    synchronized (this.pluginChannels) {
      this.pluginChannels.add(channel);
    } 
  }
  
  public void unregisterChannel(String channel) {
    synchronized (this.pluginChannels) {
      this.pluginChannels.remove(channel);
    } 
  }
  
  public Collection<String> getChannels() {
    synchronized (this.pluginChannels) {
      return Collections.unmodifiableCollection(this.pluginChannels);
    } 
  }
  
  public PluginMessage registerChannels(int protocolVersion) {
    if (protocolVersion >= 393)
      return new PluginMessage("minecraft:register", String.join("\000", Iterables.transform(this.pluginChannels, PluginMessage.MODERNISE)).getBytes(Charsets.UTF_8), false); 
    return new PluginMessage("REGISTER", String.join("\000", (Iterable)this.pluginChannels).getBytes(Charsets.UTF_8), false);
  }
  
  public int getProtocolVersion() {
    return ((Integer)ProtocolConstants.SUPPORTED_VERSION_IDS.get(ProtocolConstants.SUPPORTED_VERSION_IDS.size() - 1)).intValue();
  }
  
  public String getGameVersion() {
    return getConfig().getGameVersion();
  }
  
  public ServerInfo constructServerInfo(String name, InetSocketAddress address, String motd, boolean restricted) {
    return constructServerInfo(name, address, motd, restricted);
  }
  
  public ServerInfo constructServerInfo(String name, SocketAddress address, String motd, boolean restricted) {
    return new BungeeServerInfo(name, address, motd, restricted);
  }
  
  public CommandSender getConsole() {
    return (CommandSender)ConsoleCommandSender.getInstance();
  }
  
  public void broadcast(String message) {
    broadcast(TextComponent.fromLegacyText(message));
  }
  
  public void broadcast(BaseComponent... message) {
    getConsole().sendMessage(BaseComponent.toLegacyText(message));
    for (ProxiedPlayer player : getPlayers())
      player.sendMessage(message); 
  }
  
  public void broadcast(BaseComponent message) {
    getConsole().sendMessage(message.toLegacyText());
    for (ProxiedPlayer player : getPlayers())
      player.sendMessage(message); 
  }
  
  public void addConnection(UserConnection con) {
    UUID offlineId = con.getPendingConnection().getOfflineId();
    if (offlineId != null && offlineId.version() != 3)
      throw new IllegalArgumentException("Offline UUID must be a name-based UUID"); 
    this.connectionLock.writeLock().lock();
    try {
      this.connections.put(con.getName(), con);
      this.connectionsByUUID.put(con.getUniqueId(), con);
      this.connectionsByOfflineUUID.put(offlineId, con);
    } finally {
      this.connectionLock.writeLock().unlock();
    } 
  }
  
  public void removeConnection(UserConnection con) {
    this.connectionLock.writeLock().lock();
    try {
      if (this.connections.get(con.getName()) == con) {
        this.connections.remove(con.getName());
        this.connectionsByUUID.remove(con.getUniqueId());
        this.connectionsByOfflineUUID.remove(con.getPendingConnection().getOfflineId());
      } 
    } finally {
      this.connectionLock.writeLock().unlock();
    } 
  }
  
  public Collection<String> getDisabledCommands() {
    return this.config.getDisabledCommands();
  }
  
  public Collection<ProxiedPlayer> matchPlayer(final String partialName) {
    Preconditions.checkNotNull(partialName, "partialName");
    ProxiedPlayer exactMatch = getPlayer(partialName);
    if (exactMatch != null)
      return Collections.singleton(exactMatch); 
    return Sets.newHashSet(Iterables.filter(getPlayers(), new Predicate<ProxiedPlayer>() {
            public boolean apply(ProxiedPlayer input) {
              return (input == null) ? false : input.getName().toLowerCase(Locale.ROOT).startsWith(partialName.toLowerCase(Locale.ROOT));
            }
          }));
  }
  
  public Title createTitle() {
    return new BungeeTitle();
  }
  
  public void loadModules() {
    ModulesConfiguration modulesConfiguration = FlameCord.getInstance().getModulesConfiguration();
    this.pluginManager.registerCommand(null, (Command)new CommandEnd());
    this.pluginManager.registerCommand(null, (Command)new CommandBungee());
    if (modulesConfiguration.reloadEnabled)
      this.pluginManager.registerCommand(null, (Command)new CommandReload()); 
    if (modulesConfiguration.ipEnabled)
      this.pluginManager.registerCommand(null, (Command)new CommandIP()); 
    if (modulesConfiguration.permsEnabled)
      this.pluginManager.registerCommand(null, (Command)new CommandPerms()); 
    if (modulesConfiguration.alertEnabled) {
      this.pluginManager.registerCommand(null, (Command)new CommandAlert());
      this.pluginManager.registerCommand(null, (Command)new CommandAlertRaw());
    } 
    if (modulesConfiguration.findEnabled)
      this.pluginManager.registerCommand(null, (Command)new CommandFind()); 
    if (modulesConfiguration.listEnabled)
      this.pluginManager.registerCommand(null, (Command)new CommandList()); 
    if (modulesConfiguration.sendEnabled)
      this.pluginManager.registerCommand(null, (Command)new CommandSend()); 
    if (modulesConfiguration.serverEnabled)
      this.pluginManager.registerCommand(null, (Command)new CommandServer()); 
    if (modulesConfiguration.reconnectEnabled)
      for (ListenerInfo info : getConfig().getListeners()) {
        if (!info.isForceDefault() && getReconnectHandler() == null) {
          setReconnectHandler((ReconnectHandler)new YamlReconnectHandler());
          break;
        } 
      }  
    this.pluginManager.registerCommand(null, (Command)new FlameCordCommand(this));
    this.pluginManager.registerCommand(null, (Command)new BungeePluginsCommand());
    this.pluginManager.registerCommand(null, (Command)new BungeeIPCommand());
  }
}
