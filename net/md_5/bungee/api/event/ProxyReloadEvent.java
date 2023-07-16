package net.md_5.bungee.api.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Event;

public class ProxyReloadEvent extends Event {
  private final CommandSender sender;
  
  public ProxyReloadEvent(CommandSender sender) {
    this.sender = sender;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ProxyReloadEvent))
      return false; 
    ProxyReloadEvent other = (ProxyReloadEvent)o;
    if (!other.canEqual(this))
      return false; 
    Object this$sender = getSender(), other$sender = other.getSender();
    return !((this$sender == null) ? (other$sender != null) : !this$sender.equals(other$sender));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ProxyReloadEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $sender = getSender();
    return result * 59 + (($sender == null) ? 43 : $sender.hashCode());
  }
  
  public CommandSender getSender() {
    return this.sender;
  }
}
