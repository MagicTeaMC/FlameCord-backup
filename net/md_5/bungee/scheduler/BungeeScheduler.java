package net.md_5.bungee.scheduler;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class BungeeScheduler implements TaskScheduler {
  private final Object lock = new Object();
  
  private final AtomicInteger taskCounter = new AtomicInteger();
  
  private final TIntObjectMap<BungeeTask> tasks = TCollections.synchronizedMap((TIntObjectMap)new TIntObjectHashMap());
  
  private final Multimap<Plugin, BungeeTask> tasksByPlugin = Multimaps.synchronizedMultimap((Multimap)HashMultimap.create());
  
  private final TaskScheduler.Unsafe unsafe = new TaskScheduler.Unsafe() {
      public ExecutorService getExecutorService(Plugin plugin) {
        return plugin.getExecutorService();
      }
    };
  
  public void cancel(int id) {
    BungeeTask task = (BungeeTask)this.tasks.get(id);
    Preconditions.checkArgument((task != null), "No task with id %s", id);
    task.cancel();
  }
  
  void cancel0(BungeeTask task) {
    synchronized (this.lock) {
      this.tasks.remove(task.getId());
      this.tasksByPlugin.values().remove(task);
    } 
  }
  
  public void cancel(ScheduledTask task) {
    task.cancel();
  }
  
  public int cancel(Plugin plugin) {
    Set<ScheduledTask> toRemove = new HashSet<>();
    synchronized (this.lock) {
      for (ScheduledTask task : this.tasksByPlugin.get(plugin))
        toRemove.add(task); 
    } 
    for (ScheduledTask task : toRemove)
      cancel(task); 
    return toRemove.size();
  }
  
  public ScheduledTask runAsync(Plugin owner, Runnable task) {
    return schedule(owner, task, 0L, TimeUnit.MILLISECONDS);
  }
  
  public ScheduledTask schedule(Plugin owner, Runnable task, long delay, TimeUnit unit) {
    return schedule(owner, task, delay, 0L, unit);
  }
  
  public ScheduledTask schedule(Plugin owner, Runnable task, long delay, long period, TimeUnit unit) {
    Preconditions.checkNotNull(owner, "owner");
    Preconditions.checkNotNull(task, "task");
    BungeeTask prepared = new BungeeTask(this, this.taskCounter.getAndIncrement(), owner, task, delay, period, unit);
    synchronized (this.lock) {
      this.tasks.put(prepared.getId(), prepared);
      this.tasksByPlugin.put(owner, prepared);
    } 
    owner.getExecutorService().execute(prepared);
    return prepared;
  }
  
  public TaskScheduler.Unsafe unsafe() {
    return this.unsafe;
  }
}
