package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Event;

public abstract class TargetedEvent extends Event {
  private final Connection sender;
  
  private final Connection receiver;
  
  public String toString() {
    return "TargetedEvent(sender=" + getSender() + ", receiver=" + getReceiver() + ")";
  }
  
  public TargetedEvent(Connection sender, Connection receiver) {
    this.sender = sender;
    this.receiver = receiver;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof TargetedEvent))
      return false; 
    TargetedEvent other = (TargetedEvent)o;
    if (!other.canEqual(this))
      return false; 
    Object this$sender = getSender(), other$sender = other.getSender();
    if ((this$sender == null) ? (other$sender != null) : !this$sender.equals(other$sender))
      return false; 
    Object this$receiver = getReceiver(), other$receiver = other.getReceiver();
    return !((this$receiver == null) ? (other$receiver != null) : !this$receiver.equals(other$receiver));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof TargetedEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $sender = getSender();
    result = result * 59 + (($sender == null) ? 43 : $sender.hashCode());
    Object $receiver = getReceiver();
    return result * 59 + (($receiver == null) ? 43 : $receiver.hashCode());
  }
  
  public Connection getSender() {
    return this.sender;
  }
  
  public Connection getReceiver() {
    return this.receiver;
  }
}
