package io.github.waterfallmc.waterfall.event;

import java.util.Map;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.TargetedEvent;
import net.md_5.bungee.api.plugin.Command;

public class ProxyDefineCommandsEvent extends TargetedEvent {
  private final Map<String, Command> commands;
  
  public String toString() {
    return "ProxyDefineCommandsEvent(super=" + super.toString() + ", commands=" + getCommands() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ProxyDefineCommandsEvent))
      return false; 
    ProxyDefineCommandsEvent other = (ProxyDefineCommandsEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (!super.equals(o))
      return false; 
    Object<String, Command> this$commands = (Object<String, Command>)getCommands(), other$commands = (Object<String, Command>)other.getCommands();
    return !((this$commands == null) ? (other$commands != null) : !this$commands.equals(other$commands));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ProxyDefineCommandsEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = super.hashCode();
    Object<String, Command> $commands = (Object<String, Command>)getCommands();
    return result * 59 + (($commands == null) ? 43 : $commands.hashCode());
  }
  
  public Map<String, Command> getCommands() {
    return this.commands;
  }
  
  public ProxyDefineCommandsEvent(Connection sender, Connection receiver, Map<String, Command> commands) {
    super(sender, receiver);
    this.commands = commands;
  }
}
