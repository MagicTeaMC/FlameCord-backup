package net.md_5.bungee.connection;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.unix.DomainSocketAddress;
import java.io.DataInput;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Position;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.Commands;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PlayerListItemRemove;
import net.md_5.bungee.protocol.packet.PlayerListItemUpdate;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.ServerData;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.md_5.bungee.protocol.packet.Team;
import net.md_5.bungee.tab.TabList;

public class DownstreamBridge extends PacketHandler {
  public DownstreamBridge(ProxyServer bungee, UserConnection con, ServerConnection server) {
    this.bungee = bungee;
    this.con = con;
    this.server = server;
  }
  
  private static final Command DUMMY_COMMAND = context -> 0;
  
  private final ProxyServer bungee;
  
  private final UserConnection con;
  
  private final ServerConnection server;
  
  public void exception(Throwable t) throws Exception {
    if (this.server.isObsolete())
      return; 
    ServerInfo def = this.con.updateAndGetNextServer((ServerInfo)this.server.getInfo());
    ServerKickEvent event = (ServerKickEvent)this.bungee.getPluginManager().callEvent((Event)new ServerKickEvent((ProxiedPlayer)this.con, (ServerInfo)this.server.getInfo(), TextComponent.fromLegacyText(this.bungee.getTranslation("server_went_down", new Object[0])), def, ServerKickEvent.State.CONNECTED, ServerKickEvent.Cause.EXCEPTION));
    if (event.isCancelled() && event.getCancelServer() != null) {
      this.server.setObsolete(true);
      this.con.connectNow(event.getCancelServer(), ServerConnectEvent.Reason.SERVER_DOWN_REDIRECT);
    } else {
      this.con.disconnect0(event.getKickReasonComponent());
    } 
  }
  
  public void disconnected(ChannelWrapper channel) throws Exception {
    this.server.getInfo().removePlayer((ProxiedPlayer)this.con);
    if (this.bungee.getReconnectHandler() != null)
      this.bungee.getReconnectHandler().setServer((ProxiedPlayer)this.con); 
    if (!this.server.isObsolete()) {
      ServerInfo def = this.con.updateAndGetNextServer((ServerInfo)this.server.getInfo());
      ServerKickEvent event = (ServerKickEvent)this.bungee.getPluginManager().callEvent((Event)new ServerKickEvent((ProxiedPlayer)this.con, (ServerInfo)this.server.getInfo(), TextComponent.fromLegacyText(this.bungee.getTranslation("lost_connection", new Object[0])), def, ServerKickEvent.State.CONNECTED, ServerKickEvent.Cause.LOST_CONNECTION));
      if (event.isCancelled() && event.getCancelServer() != null) {
        this.server.setObsolete(true);
        this.con.connectNow(event.getCancelServer());
      } else {
        this.con.disconnect0(event.getKickReasonComponent());
      } 
    } 
    ServerDisconnectEvent serverDisconnectEvent = new ServerDisconnectEvent((ProxiedPlayer)this.con, (ServerInfo)this.server.getInfo());
    this.bungee.getPluginManager().callEvent((Event)serverDisconnectEvent);
  }
  
  public boolean shouldHandle(PacketWrapper packet) throws Exception {
    return !this.server.isObsolete();
  }
  
  public void handle(PacketWrapper packet) throws Exception {
    this.con.sendPacket(packet);
  }
  
  public void handle(KeepAlive alive) throws Exception {
    int timeout = this.bungee.getConfig().getTimeout();
    if (timeout <= 0 || this.server.getKeepAlives().size() < timeout / 50)
      this.server.getKeepAlives().add(new ServerConnection.KeepAliveData(alive.getRandomId(), System.currentTimeMillis())); 
  }
  
  public void handle(PlayerListItem playerList) throws Exception {
    boolean skipRewrites = this.bungee.getConfig().isDisableTabListRewrite();
    this.con.getTabListHandler().onUpdate(skipRewrites ? playerList : TabList.rewrite(playerList));
    if (!skipRewrites)
      throw CancelSendSignal.INSTANCE; 
  }
  
  public void handle(PlayerListItemRemove playerList) throws Exception {
    this.con.getTabListHandler().onUpdate(TabList.rewrite(playerList));
    throw CancelSendSignal.INSTANCE;
  }
  
  public void handle(PlayerListItemUpdate playerList) throws Exception {
    this.con.getTabListHandler().onUpdate(TabList.rewrite(playerList));
    throw CancelSendSignal.INSTANCE;
  }
  
  public void handle(ScoreboardObjective objective) throws Exception {
    Objective oldObjective;
    Scoreboard serverScoreboard = this.con.getServerSentScoreboard();
    switch (objective.getAction()) {
      case 0:
        serverScoreboard.addObjective(new Objective(objective.getName(), objective.getValue(), (objective.getType() != null) ? objective.getType().toString() : null));
        return;
      case 1:
        serverScoreboard.removeObjective(objective.getName());
        return;
      case 2:
        oldObjective = serverScoreboard.getObjective(objective.getName());
        if (oldObjective != null) {
          oldObjective.setValue(objective.getValue());
          oldObjective.setType((objective.getType() != null) ? objective.getType().toString() : null);
        } 
        return;
    } 
    throw new IllegalArgumentException("Unknown objective action: " + objective.getAction());
  }
  
  public void handle(ScoreboardScore score) throws Exception {
    Score s;
    Scoreboard serverScoreboard = this.con.getServerSentScoreboard();
    switch (score.getAction()) {
      case 0:
        s = new Score(score.getItemName(), score.getScoreName(), score.getValue());
        serverScoreboard.removeScore(score.getItemName());
        serverScoreboard.addScore(s);
        return;
      case 1:
        serverScoreboard.removeScore(score.getItemName());
        return;
    } 
    throw new IllegalArgumentException("Unknown scoreboard action: " + score.getAction());
  }
  
  public void handle(ScoreboardDisplay displayScoreboard) throws Exception {
    Scoreboard serverScoreboard = this.con.getServerSentScoreboard();
    serverScoreboard.setName(displayScoreboard.getName());
    serverScoreboard.setPosition(Position.values()[displayScoreboard.getPosition()]);
  }
  
  public void handle(Team team) throws Exception {
    Team t;
    Scoreboard serverScoreboard = this.con.getServerSentScoreboard();
    if (team.getMode() == 1) {
      serverScoreboard.removeTeam(team.getName());
      return;
    } 
    if (team.getMode() == 0) {
      t = new Team(team.getName());
      serverScoreboard.addTeam(t);
    } else {
      t = serverScoreboard.getTeam(team.getName());
    } 
    if (t != null) {
      if (team.getMode() == 0 || team.getMode() == 2) {
        t.setDisplayName(team.getDisplayName());
        t.setPrefix(team.getPrefix());
        t.setSuffix(team.getSuffix());
        t.setFriendlyFire(team.getFriendlyFire());
        t.setNameTagVisibility(team.getNameTagVisibility());
        t.setCollisionRule(team.getCollisionRule());
        t.setColor(team.getColor());
      } 
      if (team.getPlayers() != null)
        for (String s : team.getPlayers()) {
          if (team.getMode() == 0 || team.getMode() == 3) {
            t.addPlayer(s);
          } else if (team.getMode() == 4) {
            t.removePlayer(s);
          } 
        }  
    } 
  }
  
  public void handle(PluginMessage pluginMessage) throws Exception {
    PluginMessageEvent event = new PluginMessageEvent((Connection)this.server, (Connection)this.con, pluginMessage.getTag(), (byte[])pluginMessage.getData().clone());
    if (((PluginMessageEvent)this.bungee.getPluginManager().callEvent((Event)event)).isCancelled())
      throw CancelSendSignal.INSTANCE; 
    if (pluginMessage.getTag().equals((this.con.getPendingConnection().getVersion() >= 393) ? "minecraft:brand" : "MC|Brand")) {
      if (ProtocolConstants.isAfterOrEq(this.con.getPendingConnection().getVersion(), 47)) {
        try {
          ByteBuf brand = Unpooled.wrappedBuffer(pluginMessage.getData());
          String serverBrand = DefinedPacket.readString(brand);
          brand.release();
          brand = ByteBufAllocator.DEFAULT.heapBuffer();
          DefinedPacket.writeString(this.bungee.getName() + " <- " + serverBrand, brand);
          pluginMessage.setData(brand);
          brand.release();
        } catch (Exception ProtocolHacksSuck) {
          return;
        } 
      } else {
        String serverBrand = new String(pluginMessage.getData(), StandardCharsets.UTF_8);
        pluginMessage.setData((this.bungee.getName() + " <- " + serverBrand).getBytes(StandardCharsets.UTF_8));
      } 
      this.con.unsafe().sendPacket((DefinedPacket)pluginMessage);
      throw CancelSendSignal.INSTANCE;
    } 
    if (pluginMessage.getTag().equals("BungeeCord")) {
      ProxiedPlayer proxiedPlayer4;
      String str1;
      ServerInfo server;
      ProxiedPlayer proxiedPlayer3;
      String name;
      ProxiedPlayer proxiedPlayer2;
      String target;
      ProxiedPlayer proxiedPlayer1;
      ServerInfo info;
      ProxiedPlayer player;
      String channel;
      ProxiedPlayer proxiedPlayer5;
      ServerInfo serverInfo1;
      String str2;
      BaseComponent[] message;
      short len;
      Server srv;
      ProxiedPlayer proxiedPlayer6;
      byte[] data, payload;
      ServerInfo serverInfo2;
      DataInput in = pluginMessage.getStream();
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      String subChannel = in.readUTF();
      switch (subChannel) {
        case "ForwardToPlayer":
          proxiedPlayer4 = this.bungee.getPlayer(in.readUTF());
          if (proxiedPlayer4 != null) {
            String str = in.readUTF();
            short s = in.readShort();
            byte[] arrayOfByte1 = new byte[s];
            in.readFully(arrayOfByte1);
            out.writeUTF(str);
            out.writeShort(arrayOfByte1.length);
            out.write(arrayOfByte1);
            byte[] arrayOfByte2 = out.toByteArray();
            proxiedPlayer4.getServer().sendData("BungeeCord", arrayOfByte2);
          } 
          out = null;
          break;
        case "Forward":
          str1 = in.readUTF();
          channel = in.readUTF();
          len = in.readShort();
          data = new byte[len];
          in.readFully(data);
          out.writeUTF(channel);
          out.writeShort(data.length);
          out.write(data);
          payload = out.toByteArray();
          out = null;
          switch (str1) {
            case "ALL":
              for (ServerInfo serverInfo : this.bungee.getServers().values()) {
                if (serverInfo != this.server.getInfo())
                  serverInfo.sendData("BungeeCord", payload); 
              } 
              break;
            case "ONLINE":
              for (ServerInfo serverInfo : this.bungee.getServers().values()) {
                if (serverInfo != this.server.getInfo())
                  serverInfo.sendData("BungeeCord", payload, false); 
              } 
              break;
          } 
          serverInfo2 = this.bungee.getServerInfo(str1);
          if (serverInfo2 != null)
            serverInfo2.sendData("BungeeCord", payload); 
          break;
        case "Connect":
          server = this.bungee.getServerInfo(in.readUTF());
          if (server != null)
            this.con.connect(server, ServerConnectEvent.Reason.PLUGIN_MESSAGE); 
          break;
        case "ConnectOther":
          proxiedPlayer3 = this.bungee.getPlayer(in.readUTF());
          if (proxiedPlayer3 != null) {
            ServerInfo serverInfo = this.bungee.getServerInfo(in.readUTF());
            if (serverInfo != null)
              proxiedPlayer3.connect(serverInfo); 
          } 
          break;
        case "GetPlayerServer":
          name = in.readUTF();
          proxiedPlayer5 = this.bungee.getPlayer(name);
          out.writeUTF("GetPlayerServer");
          out.writeUTF(name);
          if (proxiedPlayer5 == null) {
            out.writeUTF("");
            break;
          } 
          srv = proxiedPlayer5.getServer();
          if (srv == null) {
            out.writeUTF("");
            break;
          } 
          out.writeUTF(srv.getInfo().getName());
          break;
        case "IP":
          out.writeUTF("IP");
          if (this.con.getSocketAddress() instanceof InetSocketAddress) {
            out.writeUTF(this.con.getAddress().getHostString());
            out.writeInt(this.con.getAddress().getPort());
            break;
          } 
          out.writeUTF("unix://" + ((DomainSocketAddress)this.con.getSocketAddress()).path());
          out.writeInt(0);
          break;
        case "IPOther":
          proxiedPlayer2 = this.bungee.getPlayer(in.readUTF());
          if (proxiedPlayer2 != null) {
            out.writeUTF("IPOther");
            out.writeUTF(proxiedPlayer2.getName());
            if (proxiedPlayer2.getSocketAddress() instanceof InetSocketAddress) {
              InetSocketAddress address = (InetSocketAddress)proxiedPlayer2.getSocketAddress();
              out.writeUTF(address.getHostString());
              out.writeInt(address.getPort());
              break;
            } 
            out.writeUTF("unix://" + ((DomainSocketAddress)proxiedPlayer2.getSocketAddress()).path());
            out.writeInt(0);
          } 
          break;
        case "PlayerCount":
          target = in.readUTF();
          out.writeUTF("PlayerCount");
          if (target.equals("ALL")) {
            out.writeUTF("ALL");
            out.writeInt(this.bungee.getOnlineCount());
            break;
          } 
          serverInfo1 = this.bungee.getServerInfo(target);
          if (serverInfo1 != null) {
            out.writeUTF(serverInfo1.getName());
            out.writeInt(serverInfo1.getPlayers().size());
          } 
          break;
        case "PlayerList":
          target = in.readUTF();
          out.writeUTF("PlayerList");
          if (target.equals("ALL")) {
            out.writeUTF("ALL");
            out.writeUTF(Util.csv(this.bungee.getPlayers()));
            break;
          } 
          serverInfo1 = this.bungee.getServerInfo(target);
          if (serverInfo1 != null) {
            out.writeUTF(serverInfo1.getName());
            out.writeUTF(Util.csv(serverInfo1.getPlayers()));
          } 
          break;
        case "GetServers":
          out.writeUTF("GetServers");
          out.writeUTF(Util.csv(this.bungee.getServers().keySet()));
          break;
        case "Message":
          target = in.readUTF();
          str2 = in.readUTF();
          if (target.equals("ALL")) {
            for (ProxiedPlayer proxiedPlayer : this.bungee.getPlayers())
              proxiedPlayer.sendMessage(str2); 
            break;
          } 
          proxiedPlayer6 = this.bungee.getPlayer(target);
          if (proxiedPlayer6 != null)
            proxiedPlayer6.sendMessage(str2); 
          break;
        case "MessageRaw":
          target = in.readUTF();
          message = ComponentSerializer.parse(in.readUTF());
          if (target.equals("ALL")) {
            for (ProxiedPlayer proxiedPlayer : this.bungee.getPlayers())
              proxiedPlayer.sendMessage(message); 
            break;
          } 
          proxiedPlayer6 = this.bungee.getPlayer(target);
          if (proxiedPlayer6 != null)
            proxiedPlayer6.sendMessage(message); 
          break;
        case "GetServer":
          out.writeUTF("GetServer");
          out.writeUTF(this.server.getInfo().getName());
          break;
        case "UUID":
          out.writeUTF("UUID");
          out.writeUTF(this.con.getUUID());
          break;
        case "UUIDOther":
          proxiedPlayer1 = this.bungee.getPlayer(in.readUTF());
          if (proxiedPlayer1 != null) {
            out.writeUTF("UUIDOther");
            out.writeUTF(proxiedPlayer1.getName());
            out.writeUTF(proxiedPlayer1.getUUID());
          } 
          break;
        case "ServerIP":
          info = this.bungee.getServerInfo(in.readUTF());
          if (info != null && !info.getAddress().isUnresolved()) {
            out.writeUTF("ServerIP");
            out.writeUTF(info.getName());
            out.writeUTF(info.getAddress().getAddress().getHostAddress());
            out.writeShort(info.getAddress().getPort());
          } 
          break;
        case "KickPlayer":
          player = this.bungee.getPlayer(in.readUTF());
          if (player != null) {
            String kickReason = in.readUTF();
            player.disconnect((BaseComponent)new TextComponent(kickReason));
          } 
          break;
        case "KickPlayerRaw":
          player = this.bungee.getPlayer(in.readUTF());
          if (player != null) {
            BaseComponent[] kickReason = ComponentSerializer.parse(in.readUTF());
            player.disconnect(kickReason);
          } 
          break;
      } 
      if (out != null) {
        byte[] b = out.toByteArray();
        if (b.length != 0)
          this.server.sendData("BungeeCord", b); 
      } 
      throw CancelSendSignal.INSTANCE;
    } 
  }
  
  public void handle(Kick kick) throws Exception {
    ServerInfo def = this.con.updateAndGetNextServer((ServerInfo)this.server.getInfo());
    if (Objects.equals(this.server.getInfo(), def))
      def = null; 
    ServerKickEvent event = (ServerKickEvent)this.bungee.getPluginManager().callEvent((Event)new ServerKickEvent((ProxiedPlayer)this.con, (ServerInfo)this.server.getInfo(), ComponentSerializer.parse(kick.getMessage()), def, ServerKickEvent.State.CONNECTED, ServerKickEvent.Cause.SERVER));
    if (event.isCancelled() && event.getCancelServer() != null) {
      this.con.connectNow(event.getCancelServer(), ServerConnectEvent.Reason.KICK_REDIRECT);
    } else {
      this.con.disconnect0(event.getKickReasonComponent());
    } 
    this.server.setObsolete(true);
    throw CancelSendSignal.INSTANCE;
  }
  
  public void handle(SetCompression setCompression) throws Exception {
    this.server.getCh().setCompressionThreshold(setCompression.getThreshold());
  }
  
  public void handle(TabCompleteResponse tabCompleteResponse) throws Exception {
    List<String> commands = tabCompleteResponse.getCommands();
    if (commands == null) {
      commands = Lists.transform(tabCompleteResponse.getSuggestions().getList(), new Function<Suggestion, String>() {
            public String apply(Suggestion input) {
              return input.getText();
            }
          });
    } else {
      String last = this.con.getLastCommandTabbed();
      if (last != null) {
        String commandName = last.toLowerCase(Locale.ROOT);
        commands.addAll((Collection<? extends String>)this.bungee.getPluginManager().getCommands().stream()
            .filter(entry -> {
                String lowerCase = ((String)entry.getKey()).toLowerCase(Locale.ROOT);
                return (lowerCase.startsWith(commandName) && ((Command)entry.getValue()).hasPermission((CommandSender)this.con) && !this.bungee.getDisabledCommands().contains(lowerCase));
              }).map(stringCommandEntry -> '/' + (String)stringCommandEntry.getKey())
            .collect(Collectors.toList()));
        commands.sort(null);
        this.con.setLastCommandTabbed(null);
      } 
    } 
    TabCompleteResponseEvent tabCompleteResponseEvent = new TabCompleteResponseEvent((Connection)this.server, (Connection)this.con, new ArrayList<>(commands));
    if (!((TabCompleteResponseEvent)this.bungee.getPluginManager().callEvent((Event)tabCompleteResponseEvent)).isCancelled()) {
      if (!commands.equals(tabCompleteResponseEvent.getSuggestions()))
        if (tabCompleteResponse.getCommands() != null) {
          tabCompleteResponse.setCommands(tabCompleteResponseEvent.getSuggestions());
        } else {
          final StringRange range = tabCompleteResponse.getSuggestions().getRange();
          tabCompleteResponse.setSuggestions(new Suggestions(range, Lists.transform(tabCompleteResponseEvent.getSuggestions(), new Function<String, Suggestion>() {
                    public Suggestion apply(String input) {
                      return new Suggestion(range, input);
                    }
                  })));
        }  
      this.con.unsafe().sendPacket((DefinedPacket)tabCompleteResponse);
    } 
    throw CancelSendSignal.INSTANCE;
  }
  
  public void handle(BossBar bossBar) {
    switch (bossBar.getAction()) {
      case 0:
        this.con.getSentBossBars().add(bossBar.getUuid());
        break;
      case 1:
        this.con.getSentBossBars().remove(bossBar.getUuid());
        break;
    } 
  }
  
  public void handle(Respawn respawn) {
    this.con.setDimension(respawn.getDimension());
  }
  
  public void handle(Commands commands) throws Exception {
    boolean modified = false;
    Map<String, Command> commandMap = new HashMap<>();
    for (Map.Entry<String, Command> commandEntry : (Iterable<Map.Entry<String, Command>>)this.bungee.getPluginManager().getCommands()) {
      if (!this.bungee.getDisabledCommands().contains(commandEntry.getKey()) && commands
        .getRoot().getChild(commandEntry.getKey()) == null && ((Command)commandEntry
        .getValue()).hasPermission((CommandSender)this.con))
        commandMap.put(commandEntry.getKey(), commandEntry.getValue()); 
    } 
    ProxyDefineCommandsEvent event = new ProxyDefineCommandsEvent((Connection)this.server, (Connection)this.con, commandMap);
    this.bungee.getPluginManager().callEvent((Event)event);
    for (Map.Entry<String, Command> command : (Iterable<Map.Entry<String, Command>>)event.getCommands().entrySet()) {
      CommandNode dummy = ((LiteralArgumentBuilder)LiteralArgumentBuilder.literal(command.getKey()).executes(DUMMY_COMMAND)).then(RequiredArgumentBuilder.argument("args", (ArgumentType)StringArgumentType.greedyString()).suggests(Commands.SuggestionRegistry.ASK_SERVER).executes(DUMMY_COMMAND)).build();
      commands.getRoot().addChild(dummy);
      modified = true;
    } 
    if (modified) {
      this.con.unsafe().sendPacket((DefinedPacket)commands);
      throw CancelSendSignal.INSTANCE;
    } 
  }
  
  public void handle(ServerData serverData) throws Exception {
    throw CancelSendSignal.INSTANCE;
  }
  
  public String toString() {
    return "[" + this.con.getAddress() + "|" + this.con.getName() + "] <-> DownstreamBridge <-> [" + this.server.getInfo().getName() + "]";
  }
}
