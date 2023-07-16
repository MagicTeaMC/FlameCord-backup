package net.md_5.bungee.connection;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.antibot.CheckManager;
import io.netty.channel.Channel;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClientChat;
import net.md_5.bungee.protocol.packet.ClientCommand;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PlayerListItemRemove;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.TabCompleteRequest;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.md_5.bungee.util.AllowedCharacters;

public class UpstreamBridge extends PacketHandler {
  private final ProxyServer bungee;
  
  private final UserConnection con;
  
  private long lastTabCompletion = -1L;
  
  public UpstreamBridge(ProxyServer bungee, UserConnection con) {
    this.bungee = bungee;
    this.con = con;
    BungeeCord.getInstance().addConnection(con);
    con.getTabListHandler().onConnect();
    con.unsafe().sendPacket((DefinedPacket)BungeeCord.getInstance().registerChannels(con.getPendingConnection().getVersion()));
  }
  
  public void exception(Throwable t) throws Exception {
    this.con.disconnect(Util.exception(t));
  }
  
  public void disconnected(ChannelWrapper channel) throws Exception {
    PlayerDisconnectEvent event = new PlayerDisconnectEvent((ProxiedPlayer)this.con);
    this.bungee.getPluginManager().callEvent((Event)event);
    this.con.getTabListHandler().onDisconnect();
    BungeeCord.getInstance().removeConnection(this.con);
    if (this.con.getServer() != null) {
      PlayerListItem oldPacket = new PlayerListItem();
      oldPacket.setAction(PlayerListItem.Action.REMOVE_PLAYER);
      PlayerListItem.Item item = new PlayerListItem.Item();
      item.setUuid(this.con.getUniqueId());
      oldPacket.setItems(new PlayerListItem.Item[] { item });
      PlayerListItemRemove newPacket = new PlayerListItemRemove();
      newPacket.setUuids(new UUID[] { this.con
            
            .getUniqueId() });
      for (ProxiedPlayer player : this.con.getServer().getInfo().getPlayers()) {
        if (player.getPendingConnection().getVersion() >= 761) {
          player.unsafe().sendPacket((DefinedPacket)newPacket);
          continue;
        } 
        if (ProtocolConstants.isAfterOrEq(player.getPendingConnection().getVersion(), 47))
          player.unsafe().sendPacket((DefinedPacket)oldPacket); 
      } 
      this.con.getServer().disconnect("Quitting");
    } 
  }
  
  public void writabilityChanged(ChannelWrapper channel) throws Exception {
    if (this.con.getServer() != null) {
      Channel server = this.con.getServer().getCh().getHandle();
      if (channel.getHandle().isWritable()) {
        server.config().setAutoRead(true);
      } else {
        server.config().setAutoRead(false);
      } 
    } 
  }
  
  public boolean shouldHandle(PacketWrapper packet) throws Exception {
    return (this.con.getServer() != null || packet.packet instanceof PluginMessage);
  }
  
  public void handle(PacketWrapper packet) throws Exception {
    if (this.con.getServer() != null)
      this.con.getServer().getCh().write(packet); 
  }
  
  public void handle(KeepAlive alive) throws Exception {
    ServerConnection.KeepAliveData keepAliveData = this.con.getServer().getKeepAlives().peek();
    if (keepAliveData != null && alive.getRandomId() == keepAliveData.getId()) {
      Preconditions.checkState((keepAliveData == this.con.getServer().getKeepAlives().poll()), "keepalive queue mismatch");
      int newPing = (int)(System.currentTimeMillis() - keepAliveData.getTime());
      this.con.getTabListHandler().onPingChange(newPing);
      this.con.setPing(newPing);
    } else {
      throw CancelSendSignal.INSTANCE;
    } 
  }
  
  public void handle(Chat chat) throws Exception {
    String message = handleChat(chat.getMessage());
    if (message != null) {
      chat.setMessage(message);
      this.con.getServer().unsafe().sendPacket((DefinedPacket)chat);
    } 
    throw CancelSendSignal.INSTANCE;
  }
  
  public void handle(ClientChat chat) throws Exception {
    handleChat(chat.getMessage());
  }
  
  public void handle(ClientCommand command) throws Exception {
    handleChat("/" + command.getCommand());
  }
  
  private String handleChat(String message) {
    boolean empty = true;
    for (int index = 0, length = message.length(); index < length; index++) {
      char c = message.charAt(index);
      if (!AllowedCharacters.isChatAllowedCharacter(c)) {
        this.con.disconnect(this.bungee.getTranslation("illegal_chat_characters", new Object[] { Util.unicode(c) }));
        throw CancelSendSignal.INSTANCE;
      } 
      if (empty && !Character.isWhitespace(c))
        empty = false; 
    } 
    if (empty) {
      this.con.disconnect("Chat message is empty");
      throw CancelSendSignal.INSTANCE;
    } 
    CheckManager checkManager = FlameCord.getInstance().getCheckManager();
    SocketAddress socketAddress = this.con.getCh().getRemoteAddress();
    if (checkManager.getFastChatCheck().check(socketAddress)) {
      this.con.disconnect(this.bungee.getTranslation("antibot_fastchat", new Object[0]));
      throw CancelSendSignal.INSTANCE;
    } 
    if (checkManager.getPasswordCheck().check(socketAddress, message)) {
      this.con.disconnect(this.bungee.getTranslation("antibot_password", new Object[] { Integer.valueOf(checkManager.getPasswordCheck().getRepeatCount()) }));
      throw CancelSendSignal.INSTANCE;
    } 
    ChatEvent chatEvent = new ChatEvent((Connection)this.con, (Connection)this.con.getServer(), message);
    if (!((ChatEvent)this.bungee.getPluginManager().callEvent((Event)chatEvent)).isCancelled()) {
      message = chatEvent.getMessage();
      if (!chatEvent.isCommand() || !this.bungee.getPluginManager().dispatchCommand((CommandSender)this.con, message.substring(1)))
        return message; 
    } 
    throw CancelSendSignal.INSTANCE;
  }
  
  public void handle(TabCompleteRequest tabComplete) throws Exception {
    if (this.bungee.getConfig().getTabThrottle() > 0 && this.con
      .getPendingConnection().getVersion() >= 393 && 
      !this.bungee.getConfig().isDisableModernTabLimiter()) {
      long now = System.currentTimeMillis();
      if (this.lastTabCompletion > 0L && now - this.lastTabCompletion <= this.bungee.getConfig().getTabThrottle())
        throw CancelSendSignal.INSTANCE; 
      this.lastTabCompletion = now;
    } 
    List<String> suggestions = new ArrayList<>();
    boolean isRegisteredCommand = false;
    boolean isCommand = tabComplete.getCursor().startsWith("/");
    if (isCommand)
      isRegisteredCommand = this.bungee.getPluginManager().dispatchCommand((CommandSender)this.con, tabComplete.getCursor().substring(1), suggestions); 
    TabCompleteEvent tabCompleteEvent = new TabCompleteEvent((Connection)this.con, (Connection)this.con.getServer(), tabComplete.getCursor(), suggestions);
    this.bungee.getPluginManager().callEvent((Event)tabCompleteEvent);
    if (tabCompleteEvent.isCancelled())
      throw CancelSendSignal.INSTANCE; 
    List<String> results = tabCompleteEvent.getSuggestions();
    if (!results.isEmpty()) {
      if (this.con.getPendingConnection().getVersion() < 393) {
        this.con.unsafe().sendPacket((DefinedPacket)new TabCompleteResponse(results));
      } else {
        int start = tabComplete.getCursor().lastIndexOf(' ') + 1;
        int end = tabComplete.getCursor().length();
        StringRange range = StringRange.between(start, end);
        List<Suggestion> brigadier = new LinkedList<>();
        for (String s : results)
          brigadier.add(new Suggestion(range, s)); 
        this.con.unsafe().sendPacket((DefinedPacket)new TabCompleteResponse(tabComplete.getTransactionId(), new Suggestions(range, brigadier)));
      } 
      throw CancelSendSignal.INSTANCE;
    } 
    if (isRegisteredCommand)
      throw CancelSendSignal.INSTANCE; 
    if (isCommand && this.con.getPendingConnection().getVersion() < 393) {
      int lastSpace = tabComplete.getCursor().lastIndexOf(' ');
      if (lastSpace == -1)
        this.con.setLastCommandTabbed(tabComplete.getCursor().substring(1)); 
    } 
  }
  
  public void handle(ClientSettings settings) throws Exception {
    this.con.setSettings(settings);
    SettingsChangedEvent settingsEvent = new SettingsChangedEvent((ProxiedPlayer)this.con);
    this.bungee.getPluginManager().callEvent((Event)settingsEvent);
  }
  
  public void handle(PluginMessage pluginMessage) throws Exception {
    if (pluginMessage.getTag().equals("BungeeCord"))
      throw CancelSendSignal.INSTANCE; 
    if ((BungeeCord.getInstance()).config.isForgeSupport()) {
      if (pluginMessage.getTag().equals("FML") && pluginMessage.getStream().readUnsignedByte() == 1)
        throw CancelSendSignal.INSTANCE; 
      if (pluginMessage.getTag().equals("FML|HS")) {
        this.con.getForgeClientHandler().handle(pluginMessage);
        throw CancelSendSignal.INSTANCE;
      } 
      if (this.con.getServer() != null && !this.con.getServer().isForgeServer() && (pluginMessage.getData()).length > 32767)
        throw CancelSendSignal.INSTANCE; 
    } 
    PluginMessageEvent event = new PluginMessageEvent((Connection)this.con, (Connection)this.con.getServer(), pluginMessage.getTag(), (byte[])pluginMessage.getData().clone());
    if (((PluginMessageEvent)this.bungee.getPluginManager().callEvent((Event)event)).isCancelled())
      throw CancelSendSignal.INSTANCE; 
    this.con.getPendingConnection().relayMessage(pluginMessage);
  }
  
  public String toString() {
    return "[" + this.con.getAddress() + "|" + this.con.getName() + "] -> UpstreamBridge";
  }
}
