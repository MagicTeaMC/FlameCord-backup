package io.github.waterfallmc.waterfall.exception;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyPluginMessageException extends ProxyPluginException {
  private final ProxiedPlayer player;
  
  private final String channel;
  
  private final byte[] data;
  
  public ProxyPluginMessageException(String message, Throwable cause, Plugin responsiblePlugin, ProxiedPlayer player, String channel, byte[] data) {
    super(message, cause, responsiblePlugin);
    this.player = (ProxiedPlayer)Preconditions.checkNotNull(player, "player");
    this.channel = (String)Preconditions.checkNotNull(channel, "channel");
    this.data = (byte[])Preconditions.checkNotNull(data, "data");
  }
  
  public ProxyPluginMessageException(Throwable cause, Plugin responsiblePlugin, ProxiedPlayer player, String channel, byte[] data) {
    super(cause, responsiblePlugin);
    this.player = (ProxiedPlayer)Preconditions.checkNotNull(player, "player");
    this.channel = (String)Preconditions.checkNotNull(channel, "channel");
    this.data = (byte[])Preconditions.checkNotNull(data, "data");
  }
  
  protected ProxyPluginMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Plugin responsiblePlugin, ProxiedPlayer player, String channel, byte[] data) {
    super(message, cause, enableSuppression, writableStackTrace, responsiblePlugin);
    this.player = (ProxiedPlayer)Preconditions.checkNotNull(player, "player");
    this.channel = (String)Preconditions.checkNotNull(channel, "channel");
    this.data = (byte[])Preconditions.checkNotNull(data, "data");
  }
  
  public String getChannel() {
    return this.channel;
  }
  
  public byte[] getData() {
    return this.data;
  }
  
  public ProxiedPlayer getPlayer() {
    return this.player;
  }
}
