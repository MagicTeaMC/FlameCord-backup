package net.md_5.bungee.api;

import java.util.Collection;
import java.util.Map;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;

@Deprecated
public interface ProxyConfig {
  void updateServerIPs();
  
  int getTimeout();
  
  String getUuid();
  
  Collection<ListenerInfo> getListeners();
  
  @Deprecated
  Map<String, ServerInfo> getServers();
  
  Map<String, ServerInfo> getServersCopy();
  
  ServerInfo getServerInfo(String paramString);
  
  ServerInfo addServer(ServerInfo paramServerInfo);
  
  boolean addServers(Collection<ServerInfo> paramCollection);
  
  ServerInfo removeServerNamed(String paramString);
  
  ServerInfo removeServer(ServerInfo paramServerInfo);
  
  boolean removeServersNamed(Collection<String> paramCollection);
  
  boolean removeServers(Collection<ServerInfo> paramCollection);
  
  boolean isOnlineMode();
  
  boolean isLogCommands();
  
  int getRemotePingCache();
  
  int getPlayerLimit();
  
  Collection<String> getDisabledCommands();
  
  int getServerConnectTimeout();
  
  int getRemotePingTimeout();
  
  @Deprecated
  int getThrottle();
  
  @Deprecated
  boolean isIpForward();
  
  @Deprecated
  String getFavicon();
  
  Favicon getFaviconObject();
  
  boolean isLogInitialHandlerConnections();
  
  String getGameVersion();
  
  boolean isUseNettyDnsResolver();
  
  int getTabThrottle();
  
  boolean isDisableModernTabLimiter();
  
  boolean isDisableTabListRewrite();
  
  int getPluginChannelLimit();
  
  int getPluginChannelNameLimit();
}
