package net.md_5.bungee.api.config;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public class ListenerInfo {
  private final SocketAddress socketAddress;
  
  private final String motd;
  
  private final int maxPlayers;
  
  private final int tabListSize;
  
  private final List<String> serverPriority;
  
  private final boolean forceDefault;
  
  private final Map<String, String> forcedHosts;
  
  private final String tabListType;
  
  private final boolean setLocalAddress;
  
  private final boolean pingPassthrough;
  
  private final int queryPort;
  
  private final boolean queryEnabled;
  
  private final boolean proxyProtocol;
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ListenerInfo))
      return false; 
    ListenerInfo other = (ListenerInfo)o;
    if (!other.canEqual(this))
      return false; 
    if (getMaxPlayers() != other.getMaxPlayers())
      return false; 
    if (getTabListSize() != other.getTabListSize())
      return false; 
    if (isForceDefault() != other.isForceDefault())
      return false; 
    if (isSetLocalAddress() != other.isSetLocalAddress())
      return false; 
    if (isPingPassthrough() != other.isPingPassthrough())
      return false; 
    if (getQueryPort() != other.getQueryPort())
      return false; 
    if (isQueryEnabled() != other.isQueryEnabled())
      return false; 
    if (isProxyProtocol() != other.isProxyProtocol())
      return false; 
    Object this$socketAddress = getSocketAddress(), other$socketAddress = other.getSocketAddress();
    if ((this$socketAddress == null) ? (other$socketAddress != null) : !this$socketAddress.equals(other$socketAddress))
      return false; 
    Object this$motd = getMotd(), other$motd = other.getMotd();
    if ((this$motd == null) ? (other$motd != null) : !this$motd.equals(other$motd))
      return false; 
    Object<String> this$serverPriority = (Object<String>)getServerPriority(), other$serverPriority = (Object<String>)other.getServerPriority();
    if ((this$serverPriority == null) ? (other$serverPriority != null) : !this$serverPriority.equals(other$serverPriority))
      return false; 
    Object<String, String> this$forcedHosts = (Object<String, String>)getForcedHosts(), other$forcedHosts = (Object<String, String>)other.getForcedHosts();
    if ((this$forcedHosts == null) ? (other$forcedHosts != null) : !this$forcedHosts.equals(other$forcedHosts))
      return false; 
    Object this$tabListType = getTabListType(), other$tabListType = other.getTabListType();
    return !((this$tabListType == null) ? (other$tabListType != null) : !this$tabListType.equals(other$tabListType));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ListenerInfo;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getMaxPlayers();
    result = result * 59 + getTabListSize();
    result = result * 59 + (isForceDefault() ? 79 : 97);
    result = result * 59 + (isSetLocalAddress() ? 79 : 97);
    result = result * 59 + (isPingPassthrough() ? 79 : 97);
    result = result * 59 + getQueryPort();
    result = result * 59 + (isQueryEnabled() ? 79 : 97);
    result = result * 59 + (isProxyProtocol() ? 79 : 97);
    Object $socketAddress = getSocketAddress();
    result = result * 59 + (($socketAddress == null) ? 43 : $socketAddress.hashCode());
    Object $motd = getMotd();
    result = result * 59 + (($motd == null) ? 43 : $motd.hashCode());
    Object<String> $serverPriority = (Object<String>)getServerPriority();
    result = result * 59 + (($serverPriority == null) ? 43 : $serverPriority.hashCode());
    Object<String, String> $forcedHosts = (Object<String, String>)getForcedHosts();
    result = result * 59 + (($forcedHosts == null) ? 43 : $forcedHosts.hashCode());
    Object $tabListType = getTabListType();
    return result * 59 + (($tabListType == null) ? 43 : $tabListType.hashCode());
  }
  
  public String toString() {
    return "ListenerInfo(socketAddress=" + getSocketAddress() + ", motd=" + getMotd() + ", maxPlayers=" + getMaxPlayers() + ", tabListSize=" + getTabListSize() + ", serverPriority=" + getServerPriority() + ", forceDefault=" + isForceDefault() + ", forcedHosts=" + getForcedHosts() + ", tabListType=" + getTabListType() + ", setLocalAddress=" + isSetLocalAddress() + ", pingPassthrough=" + isPingPassthrough() + ", queryPort=" + getQueryPort() + ", queryEnabled=" + isQueryEnabled() + ", proxyProtocol=" + isProxyProtocol() + ")";
  }
  
  public ListenerInfo(SocketAddress socketAddress, String motd, int maxPlayers, int tabListSize, List<String> serverPriority, boolean forceDefault, Map<String, String> forcedHosts, String tabListType, boolean setLocalAddress, boolean pingPassthrough, int queryPort, boolean queryEnabled, boolean proxyProtocol) {
    this.socketAddress = socketAddress;
    this.motd = motd;
    this.maxPlayers = maxPlayers;
    this.tabListSize = tabListSize;
    this.serverPriority = serverPriority;
    this.forceDefault = forceDefault;
    this.forcedHosts = forcedHosts;
    this.tabListType = tabListType;
    this.setLocalAddress = setLocalAddress;
    this.pingPassthrough = pingPassthrough;
    this.queryPort = queryPort;
    this.queryEnabled = queryEnabled;
    this.proxyProtocol = proxyProtocol;
  }
  
  public SocketAddress getSocketAddress() {
    return this.socketAddress;
  }
  
  public String getMotd() {
    return this.motd;
  }
  
  public int getMaxPlayers() {
    return this.maxPlayers;
  }
  
  public int getTabListSize() {
    return this.tabListSize;
  }
  
  public List<String> getServerPriority() {
    return this.serverPriority;
  }
  
  public boolean isForceDefault() {
    return this.forceDefault;
  }
  
  public Map<String, String> getForcedHosts() {
    return this.forcedHosts;
  }
  
  public String getTabListType() {
    return this.tabListType;
  }
  
  public boolean isSetLocalAddress() {
    return this.setLocalAddress;
  }
  
  public boolean isPingPassthrough() {
    return this.pingPassthrough;
  }
  
  public int getQueryPort() {
    return this.queryPort;
  }
  
  public boolean isQueryEnabled() {
    return this.queryEnabled;
  }
  
  public boolean isProxyProtocol() {
    return this.proxyProtocol;
  }
  
  @Deprecated
  public ListenerInfo(InetSocketAddress host, String motd, int maxPlayers, int tabListSize, List<String> serverPriority, boolean forceDefault, Map<String, String> forcedHosts, String tabListType, boolean setLocalAddress, boolean pingPassthrough, int queryPort, boolean queryEnabled) {
    this(host, motd, maxPlayers, tabListSize, serverPriority, forceDefault, forcedHosts, tabListType, setLocalAddress, pingPassthrough, queryPort, queryEnabled, false);
  }
  
  @Deprecated
  public String getDefaultServer() {
    return this.serverPriority.get(0);
  }
  
  @Deprecated
  public String getFallbackServer() {
    return (this.serverPriority.size() > 1) ? this.serverPriority.get(1) : getDefaultServer();
  }
  
  @Deprecated
  public InetSocketAddress getHost() {
    return (InetSocketAddress)this.socketAddress;
  }
}
