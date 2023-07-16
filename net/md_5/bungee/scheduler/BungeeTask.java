package net.md_5.bungee.scheduler;

import io.github.waterfallmc.waterfall.event.ProxyExceptionEvent;
import io.github.waterfallmc.waterfall.exception.ProxyException;
import io.github.waterfallmc.waterfall.exception.ProxySchedulerException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class BungeeTask implements Runnable, ScheduledTask {
  private final BungeeScheduler sched;
  
  private final int id;
  
  private final Plugin owner;
  
  private final Runnable task;
  
  private final long delay;
  
  private final long period;
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof BungeeTask))
      return false; 
    BungeeTask other = (BungeeTask)o;
    if (!other.canEqual(this))
      return false; 
    if (getId() != other.getId())
      return false; 
    if (getDelay() != other.getDelay())
      return false; 
    if (getPeriod() != other.getPeriod())
      return false; 
    Object this$sched = getSched(), other$sched = other.getSched();
    if ((this$sched == null) ? (other$sched != null) : !this$sched.equals(other$sched))
      return false; 
    Object this$owner = getOwner(), other$owner = other.getOwner();
    if ((this$owner == null) ? (other$owner != null) : !this$owner.equals(other$owner))
      return false; 
    Object this$task = getTask(), other$task = other.getTask();
    if ((this$task == null) ? (other$task != null) : !this$task.equals(other$task))
      return false; 
    Object this$running = getRunning(), other$running = other.getRunning();
    return !((this$running == null) ? (other$running != null) : !this$running.equals(other$running));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof BungeeTask;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getId();
    long $delay = getDelay();
    result = result * 59 + (int)($delay >>> 32L ^ $delay);
    long $period = getPeriod();
    result = result * 59 + (int)($period >>> 32L ^ $period);
    Object $sched = getSched();
    result = result * 59 + (($sched == null) ? 43 : $sched.hashCode());
    Object $owner = getOwner();
    result = result * 59 + (($owner == null) ? 43 : $owner.hashCode());
    Object $task = getTask();
    result = result * 59 + (($task == null) ? 43 : $task.hashCode());
    Object $running = getRunning();
    return result * 59 + (($running == null) ? 43 : $running.hashCode());
  }
  
  public String toString() {
    return "BungeeTask(sched=" + getSched() + ", id=" + getId() + ", owner=" + getOwner() + ", task=" + getTask() + ", delay=" + getDelay() + ", period=" + getPeriod() + ", running=" + getRunning() + ")";
  }
  
  public BungeeScheduler getSched() {
    return this.sched;
  }
  
  public int getId() {
    return this.id;
  }
  
  public Plugin getOwner() {
    return this.owner;
  }
  
  public Runnable getTask() {
    return this.task;
  }
  
  public long getDelay() {
    return this.delay;
  }
  
  public long getPeriod() {
    return this.period;
  }
  
  private final AtomicBoolean running = new AtomicBoolean(true);
  
  public AtomicBoolean getRunning() {
    return this.running;
  }
  
  public BungeeTask(BungeeScheduler sched, int id, Plugin owner, Runnable task, long delay, long period, TimeUnit unit) {
    this.sched = sched;
    this.id = id;
    this.owner = owner;
    this.task = task;
    this.delay = unit.toMillis(delay);
    this.period = unit.toMillis(period);
  }
  
  public void cancel() {
    boolean wasRunning = this.running.getAndSet(false);
    if (wasRunning)
      this.sched.cancel0(this); 
  }
  
  public void run() {
    if (this.delay > 0L)
      try {
        Thread.sleep(this.delay);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }  
    while (this.running.get()) {
      try {
        this.task.run();
      } catch (Throwable t) {
        String msg = String.format("Task %s encountered an exception", new Object[] { this });
        ProxyServer.getInstance().getLogger().log(Level.SEVERE, msg, t);
        ProxyServer.getInstance().getPluginManager().callEvent((Event)new ProxyExceptionEvent((ProxyException)new ProxySchedulerException(msg, t, this)));
      } 
      if (this.period <= 0L)
        break; 
      try {
        Thread.sleep(this.period);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      } 
    } 
    cancel();
  }
}
