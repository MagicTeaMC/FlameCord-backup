package net.md_5.bungee.api.event;

import lombok.NonNull;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class ServerConnectEvent extends Event implements Cancellable {
  private final ProxiedPlayer player;
  
  @NonNull
  private ServerInfo target;
  
  private final Reason reason;
  
  private final ServerConnectRequest request;
  
  private boolean cancelled;
  
  public void setTarget(@NonNull ServerInfo target) {
    if (target == null)
      throw new NullPointerException("target is marked non-null but is null"); 
    this.target = target;
  }
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public String toString() {
    return "ServerConnectEvent(player=" + getPlayer() + ", target=" + getTarget() + ", reason=" + getReason() + ", request=" + getRequest() + ", cancelled=" + isCancelled() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ServerConnectEvent))
      return false; 
    ServerConnectEvent other = (ServerConnectEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (isCancelled() != other.isCancelled())
      return false; 
    Object this$player = getPlayer(), other$player = other.getPlayer();
    if ((this$player == null) ? (other$player != null) : !this$player.equals(other$player))
      return false; 
    Object this$target = getTarget(), other$target = other.getTarget();
    if ((this$target == null) ? (other$target != null) : !this$target.equals(other$target))
      return false; 
    Object this$reason = getReason(), other$reason = other.getReason();
    if ((this$reason == null) ? (other$reason != null) : !this$reason.equals(other$reason))
      return false; 
    Object this$request = getRequest(), other$request = other.getRequest();
    return !((this$request == null) ? (other$request != null) : !this$request.equals(other$request));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ServerConnectEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isCancelled() ? 79 : 97);
    Object $player = getPlayer();
    result = result * 59 + (($player == null) ? 43 : $player.hashCode());
    Object $target = getTarget();
    result = result * 59 + (($target == null) ? 43 : $target.hashCode());
    Object $reason = getReason();
    result = result * 59 + (($reason == null) ? 43 : $reason.hashCode());
    Object $request = getRequest();
    return result * 59 + (($request == null) ? 43 : $request.hashCode());
  }
  
  public ProxiedPlayer getPlayer() {
    return this.player;
  }
  
  @NonNull
  public ServerInfo getTarget() {
    return this.target;
  }
  
  public Reason getReason() {
    return this.reason;
  }
  
  public ServerConnectRequest getRequest() {
    return this.request;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  @Deprecated
  public ServerConnectEvent(ProxiedPlayer player, ServerInfo target) {
    this(player, target, Reason.UNKNOWN);
  }
  
  @Deprecated
  public ServerConnectEvent(ProxiedPlayer player, ServerInfo target, Reason reason) {
    this(player, target, reason, null);
  }
  
  public ServerConnectEvent(ProxiedPlayer player, ServerInfo target, Reason reason, ServerConnectRequest request) {
    this.player = player;
    this.target = target;
    this.reason = reason;
    this.request = request;
  }
  
  public enum Reason {
    LOBBY_FALLBACK, COMMAND, SERVER_DOWN_REDIRECT, KICK_REDIRECT, PLUGIN_MESSAGE, JOIN_PROXY, PLUGIN, UNKNOWN;
  }
}
