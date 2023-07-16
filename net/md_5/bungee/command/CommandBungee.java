package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CommandBungee extends Command {
  public CommandBungee() {
    super("bungee");
  }
  
  public void execute(CommandSender sender, String[] args) {
    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&eThis server is running &c" + ProxyServer.getInstance().getName() + "&e version &a" + ProxyServer.getInstance().getVersion() + "&e by &bArkFlame Development&e.")));
  }
}
