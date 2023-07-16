package net.md_5.bungee.api;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface ReconnectHandler {
  ServerInfo getServer(ProxiedPlayer paramProxiedPlayer);
  
  void setServer(ProxiedPlayer paramProxiedPlayer);
  
  void save();
  
  void close();
}
