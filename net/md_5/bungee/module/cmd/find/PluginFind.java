package net.md_5.bungee.module.cmd.find;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class PluginFind extends Plugin {
  public void onEnable() {
    getProxy().getPluginManager().registerCommand(this, (Command)new CommandFind());
  }
}
