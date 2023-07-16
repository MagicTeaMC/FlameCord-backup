package net.md_5.bungee.api.plugin;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import net.md_5.bungee.api.CommandSender;

public abstract class Command {
  private final String name;
  
  private final String permission;
  
  private final String[] aliases;
  
  private String permissionMessage;
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Command))
      return false; 
    Command other = (Command)o;
    if (!other.canEqual(this))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$permission = getPermission(), other$permission = other.getPermission();
    if ((this$permission == null) ? (other$permission != null) : !this$permission.equals(other$permission))
      return false; 
    if (!Arrays.deepEquals((Object[])getAliases(), (Object[])other.getAliases()))
      return false; 
    Object this$permissionMessage = getPermissionMessage(), other$permissionMessage = other.getPermissionMessage();
    return !((this$permissionMessage == null) ? (other$permissionMessage != null) : !this$permissionMessage.equals(other$permissionMessage));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Command;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $permission = getPermission();
    result = result * 59 + (($permission == null) ? 43 : $permission.hashCode());
    result = result * 59 + Arrays.deepHashCode((Object[])getAliases());
    Object $permissionMessage = getPermissionMessage();
    return result * 59 + (($permissionMessage == null) ? 43 : $permissionMessage.hashCode());
  }
  
  public String toString() {
    return "Command(name=" + getName() + ", permission=" + getPermission() + ", aliases=" + Arrays.deepToString((Object[])getAliases()) + ", permissionMessage=" + getPermissionMessage() + ")";
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getPermission() {
    return this.permission;
  }
  
  public String[] getAliases() {
    return this.aliases;
  }
  
  protected void setPermissionMessage(String permissionMessage) {
    this.permissionMessage = permissionMessage;
  }
  
  public String getPermissionMessage() {
    return this.permissionMessage;
  }
  
  public Command(String name) {
    this(name, null, new String[0]);
  }
  
  public Command(String name, String permission, String... aliases) {
    Preconditions.checkArgument((name != null), "name");
    this.name = name;
    this.permission = permission;
    this.aliases = aliases;
    this.permissionMessage = null;
  }
  
  public boolean hasPermission(CommandSender sender) {
    return (this.permission == null || this.permission.isEmpty() || sender.hasPermission(this.permission));
  }
  
  public abstract void execute(CommandSender paramCommandSender, String[] paramArrayOfString);
}
