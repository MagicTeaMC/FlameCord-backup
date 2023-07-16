package net.md_5.bungee.module.cmd.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CommandList extends Command implements TabExecutor {
  public CommandList() {
    super("glist", "bungeecord.command.list", new String[0]);
  }
  
  public void execute(CommandSender sender, String[] args) {
    boolean hideEmptyServers = (args.length == 0 || !args[0].equalsIgnoreCase("all"));
    sender.sendMessage(ProxyServer.getInstance().getTranslation("command_list_format", new Object[0]));
    for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
      if (!server.canAccess(sender))
        continue; 
      Collection<ProxiedPlayer> serverPlayers = server.getPlayers();
      if (hideEmptyServers && serverPlayers.isEmpty())
        continue; 
      List<String> players = new ArrayList<>();
      for (ProxiedPlayer player : serverPlayers)
        players.add(player.getDisplayName()); 
      Collections.sort(players, String.CASE_INSENSITIVE_ORDER);
      sender.sendMessage(ProxyServer.getInstance().getTranslation("command_list", new Object[] { server.getName(), Integer.valueOf(players.size()), String.join(ChatColor.RESET + ", ", (Iterable)players) }));
    } 
    sender.sendMessage(ProxyServer.getInstance().getTranslation("total_players", new Object[] { Integer.valueOf(ProxyServer.getInstance().getOnlineCount()) }));
  }
  
  public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
    return (args.length > 1) ? Collections.<String>emptyList() : Collections.<String>singletonList("all");
  }
}
