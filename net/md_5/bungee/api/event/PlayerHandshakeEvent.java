package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.protocol.packet.Handshake;

public class PlayerHandshakeEvent extends Event implements Cancellable {
  public String toString() {
    return "PlayerHandshakeEvent(cancelled=" + isCancelled() + ", connection=" + getConnection() + ", handshake=" + getHandshake() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PlayerHandshakeEvent))
      return false; 
    PlayerHandshakeEvent other = (PlayerHandshakeEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (isCancelled() != other.isCancelled())
      return false; 
    Object this$connection = getConnection(), other$connection = other.getConnection();
    if ((this$connection == null) ? (other$connection != null) : !this$connection.equals(other$connection))
      return false; 
    Object this$handshake = getHandshake(), other$handshake = other.getHandshake();
    return !((this$handshake == null) ? (other$handshake != null) : !this$handshake.equals(other$handshake));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PlayerHandshakeEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isCancelled() ? 79 : 97);
    Object $connection = getConnection();
    result = result * 59 + (($connection == null) ? 43 : $connection.hashCode());
    Object $handshake = getHandshake();
    return result * 59 + (($handshake == null) ? 43 : $handshake.hashCode());
  }
  
  private boolean cancelled = false;
  
  private final PendingConnection connection;
  
  private final Handshake handshake;
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public PendingConnection getConnection() {
    return this.connection;
  }
  
  public Handshake getHandshake() {
    return this.handshake;
  }
  
  public PlayerHandshakeEvent(PendingConnection connection, Handshake handshake) {
    this.connection = connection;
    this.handshake = handshake;
  }
}
