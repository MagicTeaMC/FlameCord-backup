package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class ServerKickEvent extends Event implements Cancellable {
  private boolean cancelled;
  
  private final ProxiedPlayer player;
  
  private final ServerInfo kickedFrom;
  
  private BaseComponent[] kickReasonComponent;
  
  private ServerInfo cancelServer;
  
  private State state;
  
  private Cause cause;
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public void setKickReasonComponent(BaseComponent[] kickReasonComponent) {
    this.kickReasonComponent = kickReasonComponent;
  }
  
  public void setCancelServer(ServerInfo cancelServer) {
    this.cancelServer = cancelServer;
  }
  
  public void setState(State state) {
    this.state = state;
  }
  
  public void setCause(Cause cause) {
    this.cause = cause;
  }
  
  public String toString() {
    return "ServerKickEvent(cancelled=" + isCancelled() + ", player=" + getPlayer() + ", kickedFrom=" + getKickedFrom() + ", kickReasonComponent=" + Arrays.deepToString((Object[])getKickReasonComponent()) + ", cancelServer=" + getCancelServer() + ", state=" + getState() + ", cause=" + getCause() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ServerKickEvent))
      return false; 
    ServerKickEvent other = (ServerKickEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (isCancelled() != other.isCancelled())
      return false; 
    Object this$player = getPlayer(), other$player = other.getPlayer();
    if ((this$player == null) ? (other$player != null) : !this$player.equals(other$player))
      return false; 
    Object this$kickedFrom = getKickedFrom(), other$kickedFrom = other.getKickedFrom();
    if ((this$kickedFrom == null) ? (other$kickedFrom != null) : !this$kickedFrom.equals(other$kickedFrom))
      return false; 
    if (!Arrays.deepEquals((Object[])getKickReasonComponent(), (Object[])other.getKickReasonComponent()))
      return false; 
    Object this$cancelServer = getCancelServer(), other$cancelServer = other.getCancelServer();
    if ((this$cancelServer == null) ? (other$cancelServer != null) : !this$cancelServer.equals(other$cancelServer))
      return false; 
    Object this$state = getState(), other$state = other.getState();
    if ((this$state == null) ? (other$state != null) : !this$state.equals(other$state))
      return false; 
    Object this$cause = getCause(), other$cause = other.getCause();
    return !((this$cause == null) ? (other$cause != null) : !this$cause.equals(other$cause));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ServerKickEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isCancelled() ? 79 : 97);
    Object $player = getPlayer();
    result = result * 59 + (($player == null) ? 43 : $player.hashCode());
    Object $kickedFrom = getKickedFrom();
    result = result * 59 + (($kickedFrom == null) ? 43 : $kickedFrom.hashCode());
    result = result * 59 + Arrays.deepHashCode((Object[])getKickReasonComponent());
    Object $cancelServer = getCancelServer();
    result = result * 59 + (($cancelServer == null) ? 43 : $cancelServer.hashCode());
    Object $state = getState();
    result = result * 59 + (($state == null) ? 43 : $state.hashCode());
    Object $cause = getCause();
    return result * 59 + (($cause == null) ? 43 : $cause.hashCode());
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public ProxiedPlayer getPlayer() {
    return this.player;
  }
  
  public ServerInfo getKickedFrom() {
    return this.kickedFrom;
  }
  
  public BaseComponent[] getKickReasonComponent() {
    return this.kickReasonComponent;
  }
  
  public ServerInfo getCancelServer() {
    return this.cancelServer;
  }
  
  public State getState() {
    return this.state;
  }
  
  public Cause getCause() {
    return this.cause;
  }
  
  public enum State {
    CONNECTING, CONNECTED, UNKNOWN;
  }
  
  public enum Cause {
    SERVER, LOST_CONNECTION, EXCEPTION, UNKNOWN;
  }
  
  @Deprecated
  public ServerKickEvent(ProxiedPlayer player, BaseComponent[] kickReasonComponent, ServerInfo cancelServer) {
    this(player, kickReasonComponent, cancelServer, State.UNKNOWN);
  }
  
  @Deprecated
  public ServerKickEvent(ProxiedPlayer player, BaseComponent[] kickReasonComponent, ServerInfo cancelServer, State state) {
    this(player, player.getServer().getInfo(), kickReasonComponent, cancelServer, state);
  }
  
  @Deprecated
  public ServerKickEvent(ProxiedPlayer player, ServerInfo kickedFrom, BaseComponent[] kickReasonComponent, ServerInfo cancelServer, State state) {
    this(player, kickedFrom, kickReasonComponent, cancelServer, state, Cause.UNKNOWN);
  }
  
  public ServerKickEvent(ProxiedPlayer player, ServerInfo kickedFrom, BaseComponent[] kickReasonComponent, ServerInfo cancelServer, State state, Cause cause) {
    this.player = player;
    this.kickedFrom = kickedFrom;
    this.kickReasonComponent = kickReasonComponent;
    this.cancelServer = cancelServer;
    this.state = state;
    this.cause = cause;
  }
  
  @Deprecated
  public String getKickReason() {
    return BaseComponent.toLegacyText(this.kickReasonComponent);
  }
  
  @Deprecated
  public void setKickReason(String reason) {
    this.kickReasonComponent = TextComponent.fromLegacyText(reason);
  }
}
