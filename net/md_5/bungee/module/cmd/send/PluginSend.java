package net.md_5.bungee.module.cmd.send;

import net.md_5.bungee.api.plugin.Plugin;

public class PluginSend extends Plugin {
  public void onEnable() {
    getProxy().getPluginManager().registerCommand(this, new CommandSend());
  }
}
