package net.md_5.bungee.api.event;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class ServerSwitchEvent extends Event {
  private final ProxiedPlayer player;
  
  private final ServerInfo from;
  
  public ServerSwitchEvent(ProxiedPlayer player, ServerInfo from) {
    this.player = player;
    this.from = from;
  }
  
  public String toString() {
    return "ServerSwitchEvent(player=" + getPlayer() + ", from=" + getFrom() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ServerSwitchEvent))
      return false; 
    ServerSwitchEvent other = (ServerSwitchEvent)o;
    if (!other.canEqual(this))
      return false; 
    Object this$player = getPlayer(), other$player = other.getPlayer();
    if ((this$player == null) ? (other$player != null) : !this$player.equals(other$player))
      return false; 
    Object this$from = getFrom(), other$from = other.getFrom();
    return !((this$from == null) ? (other$from != null) : !this$from.equals(other$from));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ServerSwitchEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $player = getPlayer();
    result = result * 59 + (($player == null) ? 43 : $player.hashCode());
    Object $from = getFrom();
    return result * 59 + (($from == null) ? 43 : $from.hashCode());
  }
  
  public ProxiedPlayer getPlayer() {
    return this.player;
  }
  
  public ServerInfo getFrom() {
    return this.from;
  }
}
