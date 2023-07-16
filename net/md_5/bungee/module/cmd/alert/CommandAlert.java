package net.md_5.bungee.module.cmd.alert;

import java.util.Locale;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CommandAlert extends Command {
  public CommandAlert() {
    super("alert", "bungeecord.command.alert", new String[0]);
  }
  
  public void execute(CommandSender sender, String[] args) {
    if (args.length == 0) {
      sender.sendMessage(ProxyServer.getInstance().getTranslation("message_needed", new Object[0]));
    } else {
      StringBuilder builder = new StringBuilder();
      if (args[0].toLowerCase(Locale.ROOT).startsWith("&h")) {
        args[0] = args[0].substring(2);
      } else {
        builder.append(ProxyServer.getInstance().getTranslation("alert", new Object[0]));
      } 
      for (String s : args) {
        builder.append(ChatColor.translateAlternateColorCodes('&', s));
        builder.append(" ");
      } 
      String message = builder.substring(0, builder.length() - 1);
      ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(message));
    } 
  }
}
