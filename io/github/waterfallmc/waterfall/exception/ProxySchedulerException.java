package io.github.waterfallmc.waterfall.exception;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class ProxySchedulerException extends ProxyPluginException {
  private final ScheduledTask task;
  
  public ProxySchedulerException(String message, Throwable cause, ScheduledTask task) {
    super(message, cause, task.getOwner());
    this.task = (ScheduledTask)Preconditions.checkNotNull(task, "task");
  }
  
  public ProxySchedulerException(Throwable cause, ScheduledTask task) {
    super(cause, task.getOwner());
    this.task = (ScheduledTask)Preconditions.checkNotNull(task, "task");
  }
  
  protected ProxySchedulerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ScheduledTask task) {
    super(message, cause, enableSuppression, writableStackTrace, task.getOwner());
    this.task = (ScheduledTask)Preconditions.checkNotNull(task, "task");
  }
  
  public ScheduledTask getTask() {
    return this.task;
  }
}
