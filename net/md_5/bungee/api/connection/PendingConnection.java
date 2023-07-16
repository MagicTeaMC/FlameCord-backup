package net.md_5.bungee.api.connection;

import java.net.InetSocketAddress;
import java.util.UUID;
import net.md_5.bungee.api.config.ListenerInfo;

public interface PendingConnection extends Connection {
  String getName();
  
  int getVersion();
  
  InetSocketAddress getVirtualHost();
  
  ListenerInfo getListener();
  
  @Deprecated
  String getUUID();
  
  UUID getUniqueId();
  
  void setUniqueId(UUID paramUUID);
  
  boolean isOnlineMode();
  
  void setOnlineMode(boolean paramBoolean);
  
  boolean isLegacy();
}
