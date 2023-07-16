package net.md_5.bungee.api.connection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.protocol.DefinedPacket;

public interface Connection {
  @Deprecated
  InetSocketAddress getAddress();
  
  SocketAddress getSocketAddress();
  
  @Deprecated
  void disconnect(String paramString);
  
  void disconnect(BaseComponent... paramVarArgs);
  
  void disconnect(BaseComponent paramBaseComponent);
  
  boolean isConnected();
  
  Unsafe unsafe();
  
  public static interface Unsafe {
    void sendPacket(DefinedPacket param1DefinedPacket);
  }
}
