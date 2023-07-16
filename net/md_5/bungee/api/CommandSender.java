package net.md_5.bungee.api;

import java.util.Collection;
import net.md_5.bungee.api.chat.BaseComponent;

public interface CommandSender {
  String getName();
  
  @Deprecated
  void sendMessage(String paramString);
  
  @Deprecated
  void sendMessages(String... paramVarArgs);
  
  void sendMessage(BaseComponent... paramVarArgs);
  
  void sendMessage(BaseComponent paramBaseComponent);
  
  Collection<String> getGroups();
  
  void addGroups(String... paramVarArgs);
  
  void removeGroups(String... paramVarArgs);
  
  boolean hasPermission(String paramString);
  
  void setPermission(String paramString, boolean paramBoolean);
  
  Collection<String> getPermissions();
}
