package net.md_5.bungee.module.reconnect.yaml;

import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

public class PluginYaml extends Plugin {
  public void onEnable() {
    for (ListenerInfo info : getProxy().getConfig().getListeners()) {
      if (!info.isForceDefault() && getProxy().getReconnectHandler() == null) {
        getProxy().setReconnectHandler((ReconnectHandler)new YamlReconnectHandler());
        break;
      } 
    } 
  }
}
