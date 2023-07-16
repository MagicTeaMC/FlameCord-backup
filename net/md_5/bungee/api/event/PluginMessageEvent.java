package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Cancellable;

public class PluginMessageEvent extends TargetedEvent implements Cancellable {
  private boolean cancelled;
  
  private final String tag;
  
  private final byte[] data;
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public String toString() {
    return "PluginMessageEvent(super=" + super.toString() + ", cancelled=" + isCancelled() + ", tag=" + getTag() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PluginMessageEvent))
      return false; 
    PluginMessageEvent other = (PluginMessageEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (!super.equals(o))
      return false; 
    if (isCancelled() != other.isCancelled())
      return false; 
    Object this$tag = getTag(), other$tag = other.getTag();
    return ((this$tag == null) ? (other$tag != null) : !this$tag.equals(other$tag)) ? false : (!!Arrays.equals(getData(), other.getData()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PluginMessageEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = super.hashCode();
    result = result * 59 + (isCancelled() ? 79 : 97);
    Object $tag = getTag();
    result = result * 59 + (($tag == null) ? 43 : $tag.hashCode());
    return result * 59 + Arrays.hashCode(getData());
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public String getTag() {
    return this.tag;
  }
  
  public byte[] getData() {
    return this.data;
  }
  
  public PluginMessageEvent(Connection sender, Connection receiver, String tag, byte[] data) {
    super(sender, receiver);
    this.tag = tag;
    this.data = data;
  }
}
