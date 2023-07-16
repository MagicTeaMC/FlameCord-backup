package dev._2lstudios.flamecord.commands;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.antibot.StatsData;
import dev._2lstudios.flamecord.configuration.MessagesConfiguration;
import java.util.Collection;
import java.util.HashSet;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

public class FlameCordCommand extends Command {
  private final BungeeCord bungeeCord;
  
  public FlameCordCommand(BungeeCord bungeeCord) {
    super("flamecord");
    this.bungeeCord = bungeeCord;
  }
  
  public void execute(CommandSender sender, String[] args) {
    FlameCord flameCord = FlameCord.getInstance();
    MessagesConfiguration messagesConfiguration = flameCord.getMessagesConfiguration();
    if (sender.hasPermission("flamecord.usage")) {
      if (args.length > 0) {
        Collection<String> whitelistedAddresses;
        StatsData statsData;
        int totalPings, totalConnections, lastPings, lastConnections;
        switch (args[0]) {
          case "reload":
            whitelistedAddresses = new HashSet<>();
            for (ServerInfo serverInfo : this.bungeeCord.getServers().values())
              whitelistedAddresses.add(serverInfo.getSocketAddress().toString()); 
            FlameCord.initialize(this.bungeeCord.getLogger(), whitelistedAddresses);
            sender.sendMessage(
                TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_reload", new Object[0])));
            return;
          case "stats":
            statsData = FlameCord.getInstance().getStatsData();
            totalPings = statsData.getTotalPings();
            totalConnections = statsData.getTotalConnections();
            lastPings = statsData.getLastPings();
            lastConnections = statsData.getLastConnections();
            sender.sendMessage(TextComponent.fromLegacyText(messagesConfiguration.getTranslation("antibot_stats", new Object[] { Integer.valueOf(totalPings), Integer.valueOf(totalConnections), Integer.valueOf(lastPings), Integer.valueOf(lastConnections) })));
            return;
          case "firewall":
            if (args.length > 2) {
              String ip = args[2];
              switch (args[1]) {
                case "add":
                  FlameCord.getInstance().getAddressDataManager().getAddressData(ip).firewall("Blacklisted by command");
                  sender.sendMessage(TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_firewall_add", new Object[] { ip })));
                  return;
                case "remove":
                  FlameCord.getInstance().getAddressDataManager().getAddressData(ip).unfirewall();
                  sender.sendMessage(TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_firewall_remove", new Object[] { ip })));
                  return;
              } 
              sender.sendMessage(TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_firewall_help", new Object[0])));
            } else {
              sender.sendMessage(TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_firewall_help", new Object[0])));
            } 
            return;
        } 
        sender.sendMessage(TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_help", new Object[] { this.bungeeCord.getVersion() })));
      } else {
        sender.sendMessage(
            TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_help", new Object[] { this.bungeeCord.getVersion() })));
      } 
    } else {
      sender.sendMessage(
          TextComponent.fromLegacyText(messagesConfiguration.getTranslation("flamecord_nopermission", new Object[0])));
    } 
  }
}
