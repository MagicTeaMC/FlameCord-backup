package net.md_5.bungee.api.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Cancellable;

public class ChatEvent extends TargetedEvent implements Cancellable {
  private boolean cancelled;
  
  private String message;
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public String toString() {
    return "ChatEvent(super=" + super.toString() + ", cancelled=" + isCancelled() + ", message=" + getMessage() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ChatEvent))
      return false; 
    ChatEvent other = (ChatEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (!super.equals(o))
      return false; 
    if (isCancelled() != other.isCancelled())
      return false; 
    Object this$message = getMessage(), other$message = other.getMessage();
    return !((this$message == null) ? (other$message != null) : !this$message.equals(other$message));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ChatEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = super.hashCode();
    result = result * 59 + (isCancelled() ? 79 : 97);
    Object $message = getMessage();
    return result * 59 + (($message == null) ? 43 : $message.hashCode());
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public ChatEvent(Connection sender, Connection receiver, String message) {
    super(sender, receiver);
    this.message = message;
  }
  
  public boolean isCommand() {
    return (this.message.length() > 0 && this.message.charAt(0) == '/');
  }
  
  public boolean isProxyCommand() {
    if (!isCommand())
      return false; 
    int index = this.message.indexOf(" ");
    String commandName = (index == -1) ? this.message.substring(1) : this.message.substring(1, index);
    CommandSender sender = (getSender() instanceof CommandSender) ? (CommandSender)getSender() : null;
    return ProxyServer.getInstance().getPluginManager().isExecutableCommand(commandName, sender);
  }
}
