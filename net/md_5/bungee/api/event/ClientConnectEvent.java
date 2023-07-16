package net.md_5.bungee.api.event;

import java.net.SocketAddress;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class ClientConnectEvent extends Event implements Cancellable {
  private boolean cancelled;
  
  private final SocketAddress socketAddress;
  
  private final ListenerInfo listener;
  
  public ClientConnectEvent(SocketAddress socketAddress, ListenerInfo listener) {
    this.socketAddress = socketAddress;
    this.listener = listener;
  }
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public String toString() {
    return "ClientConnectEvent(cancelled=" + isCancelled() + ", socketAddress=" + getSocketAddress() + ", listener=" + getListener() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ClientConnectEvent))
      return false; 
    ClientConnectEvent other = (ClientConnectEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (isCancelled() != other.isCancelled())
      return false; 
    Object this$socketAddress = getSocketAddress(), other$socketAddress = other.getSocketAddress();
    if ((this$socketAddress == null) ? (other$socketAddress != null) : !this$socketAddress.equals(other$socketAddress))
      return false; 
    Object this$listener = getListener(), other$listener = other.getListener();
    return !((this$listener == null) ? (other$listener != null) : !this$listener.equals(other$listener));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ClientConnectEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isCancelled() ? 79 : 97);
    Object $socketAddress = getSocketAddress();
    result = result * 59 + (($socketAddress == null) ? 43 : $socketAddress.hashCode());
    Object $listener = getListener();
    return result * 59 + (($listener == null) ? 43 : $listener.hashCode());
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public SocketAddress getSocketAddress() {
    return this.socketAddress;
  }
  
  public ListenerInfo getListener() {
    return this.listener;
  }
}
