package net.md_5.bungee.api.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.plugin.Plugin;

public interface TaskScheduler {
  void cancel(int paramInt);
  
  void cancel(ScheduledTask paramScheduledTask);
  
  int cancel(Plugin paramPlugin);
  
  ScheduledTask runAsync(Plugin paramPlugin, Runnable paramRunnable);
  
  ScheduledTask schedule(Plugin paramPlugin, Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit);
  
  ScheduledTask schedule(Plugin paramPlugin, Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit);
  
  Unsafe unsafe();
  
  public static interface Unsafe {
    ExecutorService getExecutorService(Plugin param1Plugin);
  }
}
