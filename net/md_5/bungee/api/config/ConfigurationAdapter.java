package net.md_5.bungee.api.config;

import java.util.Collection;
import java.util.Map;

public interface ConfigurationAdapter {
  void load();
  
  int getInt(String paramString, int paramInt);
  
  String getString(String paramString1, String paramString2);
  
  boolean getBoolean(String paramString, boolean paramBoolean);
  
  Collection<?> getList(String paramString, Collection<?> paramCollection);
  
  Map<String, ServerInfo> getServers();
  
  Collection<ListenerInfo> getListeners();
  
  Collection<String> getGroups(String paramString);
  
  Collection<String> getPermissions(String paramString);
}
