package net.md_5.bungee.api.event;

import java.util.List;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Cancellable;

public class TabCompleteEvent extends TargetedEvent implements Cancellable {
  private boolean cancelled;
  
  private final String cursor;
  
  private final List<String> suggestions;
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public String toString() {
    return "TabCompleteEvent(super=" + super.toString() + ", cancelled=" + isCancelled() + ", cursor=" + getCursor() + ", suggestions=" + getSuggestions() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof TabCompleteEvent))
      return false; 
    TabCompleteEvent other = (TabCompleteEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (!super.equals(o))
      return false; 
    if (isCancelled() != other.isCancelled())
      return false; 
    Object this$cursor = getCursor(), other$cursor = other.getCursor();
    if ((this$cursor == null) ? (other$cursor != null) : !this$cursor.equals(other$cursor))
      return false; 
    Object<String> this$suggestions = (Object<String>)getSuggestions(), other$suggestions = (Object<String>)other.getSuggestions();
    return !((this$suggestions == null) ? (other$suggestions != null) : !this$suggestions.equals(other$suggestions));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof TabCompleteEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = super.hashCode();
    result = result * 59 + (isCancelled() ? 79 : 97);
    Object $cursor = getCursor();
    result = result * 59 + (($cursor == null) ? 43 : $cursor.hashCode());
    Object<String> $suggestions = (Object<String>)getSuggestions();
    return result * 59 + (($suggestions == null) ? 43 : $suggestions.hashCode());
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public String getCursor() {
    return this.cursor;
  }
  
  public List<String> getSuggestions() {
    return this.suggestions;
  }
  
  public TabCompleteEvent(Connection sender, Connection receiver, String cursor, List<String> suggestions) {
    super(sender, receiver);
    this.cursor = cursor;
    this.suggestions = suggestions;
  }
}
