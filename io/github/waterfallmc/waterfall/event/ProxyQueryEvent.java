package io.github.waterfallmc.waterfall.event;

import io.github.waterfallmc.waterfall.QueryResult;
import lombok.NonNull;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Event;

public class ProxyQueryEvent extends Event {
  @NonNull
  private final ListenerInfo listener;
  
  @NonNull
  private QueryResult result;
  
  public ProxyQueryEvent(@NonNull ListenerInfo listener, @NonNull QueryResult result) {
    if (listener == null)
      throw new NullPointerException("listener is marked non-null but is null"); 
    if (result == null)
      throw new NullPointerException("result is marked non-null but is null"); 
    this.listener = listener;
    this.result = result;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ProxyQueryEvent))
      return false; 
    ProxyQueryEvent other = (ProxyQueryEvent)o;
    if (!other.canEqual(this))
      return false; 
    Object this$listener = getListener(), other$listener = other.getListener();
    if ((this$listener == null) ? (other$listener != null) : !this$listener.equals(other$listener))
      return false; 
    Object this$result = getResult(), other$result = other.getResult();
    return !((this$result == null) ? (other$result != null) : !this$result.equals(other$result));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ProxyQueryEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $listener = getListener();
    result = result * 59 + (($listener == null) ? 43 : $listener.hashCode());
    Object $result = getResult();
    return result * 59 + (($result == null) ? 43 : $result.hashCode());
  }
  
  public void setResult(@NonNull QueryResult result) {
    if (result == null)
      throw new NullPointerException("result is marked non-null but is null"); 
    this.result = result;
  }
  
  public String toString() {
    return "ProxyQueryEvent(listener=" + getListener() + ", result=" + getResult() + ")";
  }
  
  @NonNull
  public ListenerInfo getListener() {
    return this.listener;
  }
  
  @NonNull
  public QueryResult getResult() {
    return this.result;
  }
}
