package net.md_5.bungee.api.event;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;

public class AsyncEvent<T> extends Event {
  private final Callback<T> done;
  
  private final Map<Plugin, AtomicInteger> intents;
  
  private final AtomicBoolean fired;
  
  private final AtomicInteger latch;
  
  public AsyncEvent(Callback<T> done) {
    this.intents = new ConcurrentHashMap<>();
    this.fired = new AtomicBoolean();
    this.latch = new AtomicInteger();
    this.done = done;
  }
  
  public String toString() {
    return "AsyncEvent(super=" + super.toString() + ", done=" + getDone() + ", intents=" + this.intents + ", fired=" + this.fired + ", latch=" + this.latch + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof AsyncEvent))
      return false; 
    AsyncEvent<?> other = (AsyncEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (!super.equals(o))
      return false; 
    Object<T> this$done = (Object<T>)getDone();
    Object<?> other$done = (Object<?>)other.getDone();
    if ((this$done == null) ? (other$done != null) : !this$done.equals(other$done))
      return false; 
    Object<Plugin, AtomicInteger> this$intents = (Object<Plugin, AtomicInteger>)this.intents, other$intents = (Object<Plugin, AtomicInteger>)other.intents;
    if ((this$intents == null) ? (other$intents != null) : !this$intents.equals(other$intents))
      return false; 
    Object this$fired = this.fired, other$fired = other.fired;
    if ((this$fired == null) ? (other$fired != null) : !this$fired.equals(other$fired))
      return false; 
    Object this$latch = this.latch, other$latch = other.latch;
    return !((this$latch == null) ? (other$latch != null) : !this$latch.equals(other$latch));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof AsyncEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = super.hashCode();
    Object<T> $done = (Object<T>)getDone();
    result = result * 59 + (($done == null) ? 43 : $done.hashCode());
    Object<Plugin, AtomicInteger> $intents = (Object<Plugin, AtomicInteger>)this.intents;
    result = result * 59 + (($intents == null) ? 43 : $intents.hashCode());
    Object $fired = this.fired;
    result = result * 59 + (($fired == null) ? 43 : $fired.hashCode());
    Object $latch = this.latch;
    return result * 59 + (($latch == null) ? 43 : $latch.hashCode());
  }
  
  public Callback<T> getDone() {
    return this.done;
  }
  
  public void postCall() {
    if (this.latch.get() == 0)
      this.done.done(this, null); 
    this.fired.set(true);
  }
  
  public void registerIntent(Plugin plugin) {
    Preconditions.checkState(!this.fired.get(), "Event %s has already been fired", this);
    AtomicInteger intentCount = this.intents.get(plugin);
    if (intentCount == null) {
      this.intents.put(plugin, new AtomicInteger(1));
    } else {
      intentCount.incrementAndGet();
    } 
    this.latch.incrementAndGet();
  }
  
  public void completeIntent(Plugin plugin) {
    AtomicInteger intentCount = this.intents.get(plugin);
    Preconditions.checkState((intentCount != null && intentCount.get() > 0), "Plugin %s has not registered intents for event %s", plugin, this);
    intentCount.decrementAndGet();
    if (this.fired.get()) {
      if (this.latch.decrementAndGet() == 0)
        this.done.done(this, null); 
    } else {
      this.latch.decrementAndGet();
    } 
  }
}
