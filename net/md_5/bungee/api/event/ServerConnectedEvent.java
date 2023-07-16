package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Event;

public class ServerConnectedEvent extends Event {
  private final ProxiedPlayer player;
  
  private final Server server;
  
  public ServerConnectedEvent(ProxiedPlayer player, Server server) {
    this.player = player;
    this.server = server;
  }
  
  public String toString() {
    return "ServerConnectedEvent(player=" + getPlayer() + ", server=" + getServer() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ServerConnectedEvent))
      return false; 
    ServerConnectedEvent other = (ServerConnectedEvent)o;
    if (!other.canEqual(this))
      return false; 
    Object this$player = getPlayer(), other$player = other.getPlayer();
    if ((this$player == null) ? (other$player != null) : !this$player.equals(other$player))
      return false; 
    Object this$server = getServer(), other$server = other.getServer();
    return !((this$server == null) ? (other$server != null) : !this$server.equals(other$server));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ServerConnectedEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $player = getPlayer();
    result = result * 59 + (($player == null) ? 43 : $player.hashCode());
    Object $server = getServer();
    return result * 59 + (($server == null) ? 43 : $server.hashCode());
  }
  
  public ProxiedPlayer getPlayer() {
    return this.player;
  }
  
  public Server getServer() {
    return this.server;
  }
}
