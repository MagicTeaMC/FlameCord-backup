package net.md_5.bungee.api.connection;

import net.md_5.bungee.api.config.ServerInfo;

public interface Server extends Connection {
  ServerInfo getInfo();
  
  void sendData(String paramString, byte[] paramArrayOfbyte);
}
