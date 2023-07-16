package net.md_5.bungee.module.cmd.server;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CommandServer extends Command implements TabExecutor {
  public CommandServer() {
    super("server", "bungeecord.command.server", new String[0]);
  }
  
  public void execute(CommandSender sender, String[] args) {
    Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
    if (args.length == 0) {
      if (sender instanceof ProxiedPlayer)
        sender.sendMessage(ProxyServer.getInstance().getTranslation("current_server", new Object[] { ((ProxiedPlayer)sender).getServer().getInfo().getName() })); 
      ComponentBuilder serverList = (new ComponentBuilder()).appendLegacy(ProxyServer.getInstance().getTranslation("server_list", new Object[0]));
      boolean first = true;
      for (ServerInfo server : servers.values()) {
        if (server.canAccess(sender)) {
          TextComponent serverTextComponent = new TextComponent(first ? server.getName() : (", " + server.getName()));
          int count = server.getPlayers().size();
          serverTextComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(
                  
                  ProxyServer.getInstance().getTranslation("server_command_hover_players", new Object[] { Integer.valueOf(count) }) + "\n")).appendLegacy(ProxyServer.getInstance().getTranslation("click_to_connect", new Object[0])).create()));
          serverTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + server.getName()));
          serverList.append((BaseComponent)serverTextComponent);
          first = false;
        } 
      } 
      sender.sendMessage(serverList.create());
    } else {
      if (!(sender instanceof ProxiedPlayer))
        return; 
      ProxiedPlayer player = (ProxiedPlayer)sender;
      ServerInfo server = servers.get(args[0]);
      if (server == null) {
        player.sendMessage(ProxyServer.getInstance().getTranslation("no_server", new Object[0]));
      } else if (!server.canAccess((CommandSender)player)) {
        player.sendMessage(ProxyServer.getInstance().getTranslation("no_server_permission", new Object[0]));
      } else {
        player.connect(server, ServerConnectEvent.Reason.COMMAND);
      } 
    } 
  }
  
  public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
    return (args.length > 1) ? Collections.EMPTY_LIST : Iterables.transform(Iterables.filter(ProxyServer.getInstance().getServersCopy().values(), new Predicate<ServerInfo>() {
            private final String lower = (args.length == 0) ? "" : args[0].toLowerCase(Locale.ROOT);
            
            public boolean apply(ServerInfo input) {
              return (input.getName().toLowerCase(Locale.ROOT).startsWith(this.lower) && input.canAccess(sender));
            }
          }), new Function<ServerInfo, String>() {
          public String apply(ServerInfo input) {
            return input.getName();
          }
        });
  }
}
