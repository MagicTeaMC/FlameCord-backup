package net.md_5.bungee.api.connection;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.SkinConfiguration;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.score.Scoreboard;

public interface ProxiedPlayer extends Connection, CommandSender {
  String getDisplayName();
  
  void setDisplayName(String paramString);
  
  void sendMessage(ChatMessageType paramChatMessageType, BaseComponent... paramVarArgs);
  
  void sendMessage(ChatMessageType paramChatMessageType, BaseComponent paramBaseComponent);
  
  void sendMessage(UUID paramUUID, BaseComponent... paramVarArgs);
  
  void sendMessage(UUID paramUUID, BaseComponent paramBaseComponent);
  
  void connect(ServerInfo paramServerInfo);
  
  void connect(ServerInfo paramServerInfo, ServerConnectEvent.Reason paramReason);
  
  void connect(ServerInfo paramServerInfo, Callback<Boolean> paramCallback);
  
  void connect(ServerInfo paramServerInfo, Callback<Boolean> paramCallback, boolean paramBoolean);
  
  void connect(ServerInfo paramServerInfo, Callback<Boolean> paramCallback, boolean paramBoolean, int paramInt);
  
  void connect(ServerInfo paramServerInfo, Callback<Boolean> paramCallback, ServerConnectEvent.Reason paramReason);
  
  void connect(ServerInfo paramServerInfo, Callback<Boolean> paramCallback, boolean paramBoolean, ServerConnectEvent.Reason paramReason, int paramInt);
  
  void connect(ServerConnectRequest paramServerConnectRequest);
  
  Server getServer();
  
  int getPing();
  
  void sendData(String paramString, byte[] paramArrayOfbyte);
  
  PendingConnection getPendingConnection();
  
  void chat(String paramString);
  
  ServerInfo getReconnectServer();
  
  void setReconnectServer(ServerInfo paramServerInfo);
  
  @Deprecated
  String getUUID();
  
  UUID getUniqueId();
  
  Locale getLocale();
  
  byte getViewDistance();
  
  ChatMode getChatMode();
  
  boolean hasChatColors();
  
  SkinConfiguration getSkinParts();
  
  MainHand getMainHand();
  
  void setTabHeader(BaseComponent paramBaseComponent1, BaseComponent paramBaseComponent2);
  
  void setTabHeader(BaseComponent[] paramArrayOfBaseComponent1, BaseComponent[] paramArrayOfBaseComponent2);
  
  void resetTabHeader();
  
  void sendTitle(Title paramTitle);
  
  boolean isForgeUser();
  
  Map<String, String> getModList();
  
  Scoreboard getScoreboard();
  
  public enum ChatMode {
    SHOWN, COMMANDS_ONLY, HIDDEN;
  }
  
  public enum MainHand {
    LEFT, RIGHT;
  }
}
