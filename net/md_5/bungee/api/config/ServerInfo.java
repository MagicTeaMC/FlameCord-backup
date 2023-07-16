package net.md_5.bungee.api.config;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface ServerInfo {
  String getName();
  
  @Deprecated
  InetSocketAddress getAddress();
  
  SocketAddress getSocketAddress();
  
  Collection<ProxiedPlayer> getPlayers();
  
  String getMotd();
  
  boolean isRestricted();
  
  String getPermission();
  
  boolean canAccess(CommandSender paramCommandSender);
  
  void sendData(String paramString, byte[] paramArrayOfbyte);
  
  boolean sendData(String paramString, byte[] paramArrayOfbyte, boolean paramBoolean);
  
  void ping(Callback<ServerPing> paramCallback);
}
