package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PostLoginEvent extends Event {
  private final ProxiedPlayer player;
  
  public PostLoginEvent(ProxiedPlayer player) {
    this.player = player;
  }
  
  public String toString() {
    return "PostLoginEvent(player=" + getPlayer() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PostLoginEvent))
      return false; 
    PostLoginEvent other = (PostLoginEvent)o;
    if (!other.canEqual(this))
      return false; 
    Object this$player = getPlayer(), other$player = other.getPlayer();
    return !((this$player == null) ? (other$player != null) : !this$player.equals(other$player));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PostLoginEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $player = getPlayer();
    return result * 59 + (($player == null) ? 43 : $player.hashCode());
  }
  
  public ProxiedPlayer getPlayer() {
    return this.player;
  }
}
