package net.md_5.bungee.conf;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import gnu.trove.map.TMap;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.util.CaseInsensitiveMap;
import net.md_5.bungee.util.CaseInsensitiveSet;

public abstract class Configuration implements ProxyConfig {
  public void updateServerIPs() {
    CaseInsensitiveMap caseInsensitiveMap = new CaseInsensitiveMap();
    for (ServerInfo info : this.servers.values()) {
      InetSocketAddress updated = (InetSocketAddress)Util.getAddr(info.getAddress().getHostName() + ":" + info.getAddress().getPort());
      ServerInfo updatedInfo = ProxyServer.getInstance().constructServerInfo(info.getName(), updated, info.getMotd(), info.isRestricted());
      caseInsensitiveMap.put(info.getName(), updatedInfo);
    } 
    this.servers = (TMap<String, ServerInfo>)caseInsensitiveMap;
  }
  
  private int timeout = 17000;
  
  public int getTimeout() {
    return this.timeout;
  }
  
  private String uuid = UUID.randomUUID().toString();
  
  private Collection<ListenerInfo> listeners;
  
  public String getUuid() {
    return this.uuid;
  }
  
  public Collection<ListenerInfo> getListeners() {
    return this.listeners;
  }
  
  private final Object serversLock = new Object();
  
  private TMap<String, ServerInfo> servers;
  
  public Object getServersLock() {
    return this.serversLock;
  }
  
  public TMap<String, ServerInfo> getServers() {
    return this.servers;
  }
  
  private boolean onlineMode = true;
  
  private boolean enforceSecureProfile;
  
  private boolean logCommands;
  
  public boolean isOnlineMode() {
    return this.onlineMode;
  }
  
  public boolean isEnforceSecureProfile() {
    return this.enforceSecureProfile;
  }
  
  public boolean isLogCommands() {
    return this.logCommands;
  }
  
  private boolean logPings = true;
  
  public boolean isLogPings() {
    return this.logPings;
  }
  
  private int remotePingCache = -1;
  
  public int getRemotePingCache() {
    return this.remotePingCache;
  }
  
  private int playerLimit = -1;
  
  private Collection<String> disabledCommands;
  
  public int getPlayerLimit() {
    return this.playerLimit;
  }
  
  public Collection<String> getDisabledCommands() {
    return this.disabledCommands;
  }
  
  private int serverConnectTimeout = 5000;
  
  public int getServerConnectTimeout() {
    return this.serverConnectTimeout;
  }
  
  private int remotePingTimeout = 5000;
  
  public int getRemotePingTimeout() {
    return this.remotePingTimeout;
  }
  
  private int throttle = 4000;
  
  public int getThrottle() {
    return this.throttle;
  }
  
  private int throttleLimit = 3;
  
  private boolean ipForward;
  
  private Favicon favicon;
  
  public int getThrottleLimit() {
    return this.throttleLimit;
  }
  
  public boolean isIpForward() {
    return this.ipForward;
  }
  
  private int compressionThreshold = 256;
  
  private boolean preventProxyConnections;
  
  public int getCompressionThreshold() {
    return this.compressionThreshold;
  }
  
  public boolean isPreventProxyConnections() {
    return this.preventProxyConnections;
  }
  
  private boolean forgeSupport = true;
  
  public boolean isForgeSupport() {
    return this.forgeSupport;
  }
  
  public void load() {
    synchronized (this.serversLock) {
      ConfigurationAdapter adapter = ProxyServer.getInstance().getConfigurationAdapter();
      adapter.load();
      File fav = new File("server-icon.png");
      if (fav.exists())
        try {
          this.favicon = Favicon.create(ImageIO.read(fav));
        } catch (IOException|IllegalArgumentException ex) {
          ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not load server icon", ex);
        }  
      this.listeners = adapter.getListeners();
      this.timeout = adapter.getInt("timeout", this.timeout);
      this.uuid = adapter.getString("stats", this.uuid);
      this.onlineMode = adapter.getBoolean("online_mode", this.onlineMode);
      this.enforceSecureProfile = adapter.getBoolean("enforce_secure_profile", this.enforceSecureProfile);
      this.logCommands = adapter.getBoolean("log_commands", this.logCommands);
      this.logPings = adapter.getBoolean("log_pings", this.logPings);
      this.remotePingCache = adapter.getInt("remote_ping_cache", this.remotePingCache);
      this.playerLimit = adapter.getInt("player_limit", this.playerLimit);
      this.serverConnectTimeout = adapter.getInt("server_connect_timeout", this.serverConnectTimeout);
      this.remotePingTimeout = adapter.getInt("remote_ping_timeout", this.remotePingTimeout);
      this.throttle = adapter.getInt("connection_throttle", this.throttle);
      this.throttleLimit = adapter.getInt("connection_throttle_limit", this.throttleLimit);
      this.ipForward = adapter.getBoolean("ip_forward", this.ipForward);
      this.compressionThreshold = adapter.getInt("network_compression_threshold", this.compressionThreshold);
      this.preventProxyConnections = adapter.getBoolean("prevent_proxy_connections", this.preventProxyConnections);
      this.forgeSupport = adapter.getBoolean("forge_support", this.forgeSupport);
      this.disabledCommands = (Collection<String>)new CaseInsensitiveSet(adapter.getList("disabled_commands", Arrays.asList(new String[] { "disabledcommandhere" })));
      Preconditions.checkArgument((this.listeners != null && !this.listeners.isEmpty()), "No listeners defined.");
      Map<String, ServerInfo> newServers = adapter.getServers();
      Preconditions.checkArgument((newServers != null && !newServers.isEmpty()), "No servers defined");
      if (this.servers == null) {
        this.servers = (TMap<String, ServerInfo>)new CaseInsensitiveMap(newServers);
      } else {
        Map<String, ServerInfo> oldServers = getServersCopy();
        for (ServerInfo oldServer : oldServers.values()) {
          ServerInfo newServer = newServers.get(oldServer.getName());
          if ((newServer == null || !oldServer.getAddress().equals(newServer.getAddress())) && !oldServer.getPlayers().isEmpty()) {
            BungeeCord.getInstance().getLogger().info("Moving players off of server: " + oldServer.getName());
            for (ProxiedPlayer player : oldServer.getPlayers()) {
              ListenerInfo listener = player.getPendingConnection().getListener();
              String destinationName = (newServers.get(listener.getDefaultServer()) == null) ? listener.getDefaultServer() : listener.getFallbackServer();
              ServerInfo destination = newServers.get(destinationName);
              if (destination == null) {
                BungeeCord.getInstance().getLogger().severe("Couldn't find server " + listener.getDefaultServer() + " or " + listener.getFallbackServer() + " to put player " + player.getName() + " on");
                player.disconnect(BungeeCord.getInstance().getTranslation("fallback_kick", new Object[] { "Not found on reload" }));
                continue;
              } 
              player.connect(destination, (success, cause) -> {
                    if (!success.booleanValue()) {
                      BungeeCord.getInstance().getLogger().log(Level.WARNING, "Failed to connect " + player.getName() + " to " + destination.getName(), cause);
                      player.disconnect(BungeeCord.getInstance().getTranslation("fallback_kick", new Object[] { cause.getCause().getClass().getName() }));
                    } 
                  });
            } 
            continue;
          } 
          newServers.put(oldServer.getName(), oldServer);
        } 
        this.servers = (TMap<String, ServerInfo>)new CaseInsensitiveMap(newServers);
      } 
      for (ListenerInfo listener : this.listeners) {
        for (int i = 0; i < listener.getServerPriority().size(); i++) {
          String server = listener.getServerPriority().get(i);
          Preconditions.checkArgument(this.servers.containsKey(server), "Server %s (priority %s) is not defined", server, i);
        } 
        for (String server : listener.getForcedHosts().values()) {
          if (!this.servers.containsKey(server))
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Forced host server {0} is not defined", server); 
        } 
      } 
    } 
  }
  
  @Deprecated
  public String getFavicon() {
    return getFaviconObject().getEncoded();
  }
  
  public Favicon getFaviconObject() {
    return this.favicon;
  }
  
  public Map<String, ServerInfo> getServersCopy() {
    synchronized (this.serversLock) {
      return (Map<String, ServerInfo>)ImmutableMap.copyOf((Map)this.servers);
    } 
  }
  
  public ServerInfo getServerInfo(String name) {
    synchronized (this.serversLock) {
      return (ServerInfo)this.servers.get(name);
    } 
  }
  
  public ServerInfo addServer(ServerInfo server) {
    synchronized (this.serversLock) {
      return (ServerInfo)this.servers.put(server.getName(), server);
    } 
  }
  
  public boolean addServers(Collection<ServerInfo> servers) {
    synchronized (this.serversLock) {
      boolean changed = false;
      for (ServerInfo server : servers) {
        if (server != this.servers.put(server.getName(), server))
          changed = true; 
      } 
      return changed;
    } 
  }
  
  public ServerInfo removeServerNamed(String name) {
    synchronized (this.serversLock) {
      return (ServerInfo)this.servers.remove(name);
    } 
  }
  
  public ServerInfo removeServer(ServerInfo server) {
    synchronized (this.serversLock) {
      return (ServerInfo)this.servers.remove(server.getName());
    } 
  }
  
  public boolean removeServersNamed(Collection<String> names) {
    synchronized (this.serversLock) {
      return this.servers.keySet().removeAll(names);
    } 
  }
  
  public boolean removeServers(Collection<ServerInfo> servers) {
    synchronized (this.serversLock) {
      boolean changed = false;
      for (ServerInfo server : servers) {
        if (null != this.servers.remove(server.getName()))
          changed = true; 
      } 
      return changed;
    } 
  }
}
