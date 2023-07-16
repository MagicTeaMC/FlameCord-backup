package net.md_5.bungee.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Event;

public class CommandReload extends Command {
  public CommandReload() {
    super("greload", "bungeecord.command.reload", new String[0]);
  }
  
  public void execute(CommandSender sender, String[] args) {
    (BungeeCord.getInstance()).config.load();
    BungeeCord.getInstance().reloadMessages();
    BungeeCord.getInstance().stopListeners();
    BungeeCord.getInstance().startListeners();
    BungeeCord.getInstance().getPluginManager().callEvent((Event)new ProxyReloadEvent(sender));
    sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.RED.toString() + "FlameCord has been reloaded. This is NOT advisable and you will not be supported with any issues that arise! Please restart FlameCord ASAP.");
  }
}
