package net.md_5.bungee.api.event;

import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class ServerDisconnectEvent extends Event {
  @NonNull
  private final ProxiedPlayer player;
  
  @NonNull
  private final ServerInfo target;
  
  public ServerDisconnectEvent(@NonNull ProxiedPlayer player, @NonNull ServerInfo target) {
    if (player == null)
      throw new NullPointerException("player is marked non-null but is null"); 
    if (target == null)
      throw new NullPointerException("target is marked non-null but is null"); 
    this.player = player;
    this.target = target;
  }
  
  public String toString() {
    return "ServerDisconnectEvent(player=" + getPlayer() + ", target=" + getTarget() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ServerDisconnectEvent))
      return false; 
    ServerDisconnectEvent other = (ServerDisconnectEvent)o;
    if (!other.canEqual(this))
      return false; 
    Object this$player = getPlayer(), other$player = other.getPlayer();
    if ((this$player == null) ? (other$player != null) : !this$player.equals(other$player))
      return false; 
    Object this$target = getTarget(), other$target = other.getTarget();
    return !((this$target == null) ? (other$target != null) : !this$target.equals(other$target));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ServerDisconnectEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $player = getPlayer();
    result = result * 59 + (($player == null) ? 43 : $player.hashCode());
    Object $target = getTarget();
    return result * 59 + (($target == null) ? 43 : $target.hashCode());
  }
  
  @NonNull
  public ProxiedPlayer getPlayer() {
    return this.player;
  }
  
  @NonNull
  public ServerInfo getTarget() {
    return this.target;
  }
}
