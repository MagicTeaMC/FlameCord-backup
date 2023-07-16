package net.md_5.bungee.api.scheduler;

import net.md_5.bungee.api.plugin.Plugin;

public interface ScheduledTask {
  int getId();
  
  Plugin getOwner();
  
  Runnable getTask();
  
  void cancel();
}
