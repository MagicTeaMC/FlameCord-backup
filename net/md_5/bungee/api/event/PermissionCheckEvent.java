package net.md_5.bungee.api.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Event;

public class PermissionCheckEvent extends Event {
  private final CommandSender sender;
  
  private final String permission;
  
  private boolean hasPermission;
  
  public void setHasPermission(boolean hasPermission) {
    this.hasPermission = hasPermission;
  }
  
  public PermissionCheckEvent(CommandSender sender, String permission, boolean hasPermission) {
    this.sender = sender;
    this.permission = permission;
    this.hasPermission = hasPermission;
  }
  
  public String toString() {
    return "PermissionCheckEvent(sender=" + getSender() + ", permission=" + getPermission() + ", hasPermission=" + this.hasPermission + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PermissionCheckEvent))
      return false; 
    PermissionCheckEvent other = (PermissionCheckEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (this.hasPermission != other.hasPermission)
      return false; 
    Object this$sender = getSender(), other$sender = other.getSender();
    if ((this$sender == null) ? (other$sender != null) : !this$sender.equals(other$sender))
      return false; 
    Object this$permission = getPermission(), other$permission = other.getPermission();
    return !((this$permission == null) ? (other$permission != null) : !this$permission.equals(other$permission));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PermissionCheckEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (this.hasPermission ? 79 : 97);
    Object $sender = getSender();
    result = result * 59 + (($sender == null) ? 43 : $sender.hashCode());
    Object $permission = getPermission();
    return result * 59 + (($permission == null) ? 43 : $permission.hashCode());
  }
  
  public CommandSender getSender() {
    return this.sender;
  }
  
  public String getPermission() {
    return this.permission;
  }
  
  public boolean hasPermission() {
    return this.hasPermission;
  }
}
