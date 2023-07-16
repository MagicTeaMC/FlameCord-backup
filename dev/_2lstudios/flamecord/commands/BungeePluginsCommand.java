package dev._2lstudios.flamecord.commands;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.MessagesConfiguration;
import java.util.Collection;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePluginsCommand extends Command {
  public BungeePluginsCommand() {
    super("bplugins");
  }
  
  public void execute(CommandSender sender, String[] args) {
    FlameCord flameCord = FlameCord.getInstance();
    MessagesConfiguration messagesConfiguration = flameCord.getMessagesConfiguration();
    if (sender.hasPermission("flamecord.usage")) {
      Collection<Plugin> plugins = BungeeCord.getInstance().getPluginManager().getPlugins();
      int amount = plugins.size();
      String header = FlameCord.getInstance().getMessagesConfiguration().getTranslation("flamecord_bplugins_header", new Object[] { Integer.valueOf(amount) });
      String separator = FlameCord.getInstance().getMessagesConfiguration().getTranslation("flamecord_bplugins_separator", new Object[0]);
      StringBuilder stringBuilder = new StringBuilder(header);
      boolean first = true;
      for (Plugin plugin : plugins) {
        stringBuilder.append((first ? "" : separator) + plugin.getDescription().getName());
        if (first)
          first = false; 
      } 
      sender.sendMessage(TextComponent.fromLegacyText(stringBuilder.toString()));
    } else {
      sender.sendMessage(
          TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_bplugins_nopermission", new Object[0])));
    } 
  }
}
