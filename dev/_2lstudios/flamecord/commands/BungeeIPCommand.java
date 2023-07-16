package dev._2lstudios.flamecord.commands;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.MessagesConfiguration;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BungeeIPCommand extends Command {
  public BungeeIPCommand() {
    super("bip");
  }
  
  public void execute(CommandSender sender, String[] args) {
    FlameCord flameCord = FlameCord.getInstance();
    MessagesConfiguration messagesConfiguration = flameCord.getMessagesConfiguration();
    if (sender.hasPermission("flamecord.bip")) {
      if (args.length > 0) {
        ProxiedPlayer player = BungeeCord.getInstance().getPlayer(args[0]);
        if (player != null) {
          String message = messagesConfiguration.getTranslation("flamecord_bip", new Object[] { player.getDisplayName(), player
                .getUniqueId(), player.getSocketAddress(), Integer.valueOf(player.getPing()), player.getLocale(), 
                Byte.valueOf(player.getViewDistance()), player.getServer().getInfo().getName() });
          sender.sendMessage(TextComponent.fromLegacyText(message));
        } else {
          sender.sendMessage(
              
              TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_bip_offline", new Object[0])));
        } 
      } else {
        sender.sendMessage(
            TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_bip_usage", new Object[0])));
      } 
    } else {
      sender.sendMessage(
          TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_bip_nopermission", new Object[0])));
    } 
  }
}
