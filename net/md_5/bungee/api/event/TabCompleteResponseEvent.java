package net.md_5.bungee.api.event;

import java.util.List;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Cancellable;

public class TabCompleteResponseEvent extends TargetedEvent implements Cancellable {
  private boolean cancelled;
  
  private final List<String> suggestions;
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public String toString() {
    return "TabCompleteResponseEvent(super=" + super.toString() + ", cancelled=" + isCancelled() + ", suggestions=" + getSuggestions() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof TabCompleteResponseEvent))
      return false; 
    TabCompleteResponseEvent other = (TabCompleteResponseEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (!super.equals(o))
      return false; 
    if (isCancelled() != other.isCancelled())
      return false; 
    Object<String> this$suggestions = (Object<String>)getSuggestions(), other$suggestions = (Object<String>)other.getSuggestions();
    return !((this$suggestions == null) ? (other$suggestions != null) : !this$suggestions.equals(other$suggestions));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof TabCompleteResponseEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = super.hashCode();
    result = result * 59 + (isCancelled() ? 79 : 97);
    Object<String> $suggestions = (Object<String>)getSuggestions();
    return result * 59 + (($suggestions == null) ? 43 : $suggestions.hashCode());
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public List<String> getSuggestions() {
    return this.suggestions;
  }
  
  public TabCompleteResponseEvent(Connection sender, Connection receiver, List<String> suggestions) {
    super(sender, receiver);
    this.suggestions = suggestions;
  }
}
