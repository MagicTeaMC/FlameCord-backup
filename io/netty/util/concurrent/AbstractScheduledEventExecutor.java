package io.netty.util.concurrent;

import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledEventExecutor extends AbstractEventExecutor {
  private static final Comparator<ScheduledFutureTask<?>> SCHEDULED_FUTURE_TASK_COMPARATOR = new Comparator<ScheduledFutureTask<?>>() {
      public int compare(ScheduledFutureTask<?> o1, ScheduledFutureTask<?> o2) {
        return o1.compareTo(o2);
      }
    };
  
  private static final long START_TIME = System.nanoTime();
  
  static final Runnable WAKEUP_TASK = new Runnable() {
      public void run() {}
    };
  
  PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue;
  
  long nextTaskId;
  
  protected AbstractScheduledEventExecutor() {}
  
  protected AbstractScheduledEventExecutor(EventExecutorGroup parent) {
    super(parent);
  }
  
  protected long getCurrentTimeNanos() {
    return defaultCurrentTimeNanos();
  }
  
  @Deprecated
  protected static long nanoTime() {
    return defaultCurrentTimeNanos();
  }
  
  static long defaultCurrentTimeNanos() {
    return System.nanoTime() - START_TIME;
  }
  
  static long deadlineNanos(long nanoTime, long delay) {
    long deadlineNanos = nanoTime + delay;
    return (deadlineNanos < 0L) ? Long.MAX_VALUE : deadlineNanos;
  }
  
  protected static long deadlineToDelayNanos(long deadlineNanos) {
    return ScheduledFutureTask.deadlineToDelayNanos(defaultCurrentTimeNanos(), deadlineNanos);
  }
  
  protected static long initialNanoTime() {
    return START_TIME;
  }
  
  PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue() {
    if (this.scheduledTaskQueue == null)
      this.scheduledTaskQueue = (PriorityQueue<ScheduledFutureTask<?>>)new DefaultPriorityQueue(SCHEDULED_FUTURE_TASK_COMPARATOR, 11); 
    return this.scheduledTaskQueue;
  }
  
  private static boolean isNullOrEmpty(Queue<ScheduledFutureTask<?>> queue) {
    return (queue == null || queue.isEmpty());
  }
  
  protected void cancelScheduledTasks() {
    assert inEventLoop();
    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
    if (isNullOrEmpty((Queue<ScheduledFutureTask<?>>)scheduledTaskQueue))
      return; 
    ScheduledFutureTask[] arrayOfScheduledFutureTask = (ScheduledFutureTask[])scheduledTaskQueue.toArray((Object[])new ScheduledFutureTask[0]);
    for (ScheduledFutureTask<?> task : arrayOfScheduledFutureTask)
      task.cancelWithoutRemove(false); 
    scheduledTaskQueue.clearIgnoringIndexes();
  }
  
  protected final Runnable pollScheduledTask() {
    return pollScheduledTask(getCurrentTimeNanos());
  }
  
  protected final Runnable pollScheduledTask(long nanoTime) {
    assert inEventLoop();
    ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
    if (scheduledTask == null || scheduledTask.deadlineNanos() - nanoTime > 0L)
      return null; 
    this.scheduledTaskQueue.remove();
    scheduledTask.setConsumed();
    return scheduledTask;
  }
  
  protected final long nextScheduledTaskNano() {
    ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
    return (scheduledTask != null) ? scheduledTask.delayNanos() : -1L;
  }
  
  protected final long nextScheduledTaskDeadlineNanos() {
    ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
    return (scheduledTask != null) ? scheduledTask.deadlineNanos() : -1L;
  }
  
  final ScheduledFutureTask<?> peekScheduledTask() {
    PriorityQueue<ScheduledFutureTask<?>> priorityQueue = this.scheduledTaskQueue;
    return (priorityQueue != null) ? priorityQueue.peek() : null;
  }
  
  protected final boolean hasScheduledTasks() {
    ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
    return (scheduledTask != null && scheduledTask.deadlineNanos() <= getCurrentTimeNanos());
  }
  
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    ObjectUtil.checkNotNull(command, "command");
    ObjectUtil.checkNotNull(unit, "unit");
    if (delay < 0L)
      delay = 0L; 
    validateScheduled0(delay, unit);
    return schedule(new ScheduledFutureTask(this, command, 
          
          deadlineNanos(getCurrentTimeNanos(), unit.toNanos(delay))));
  }
  
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    ObjectUtil.checkNotNull(callable, "callable");
    ObjectUtil.checkNotNull(unit, "unit");
    if (delay < 0L)
      delay = 0L; 
    validateScheduled0(delay, unit);
    return schedule(new ScheduledFutureTask<V>(this, callable, 
          deadlineNanos(getCurrentTimeNanos(), unit.toNanos(delay))));
  }
  
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    ObjectUtil.checkNotNull(command, "command");
    ObjectUtil.checkNotNull(unit, "unit");
    if (initialDelay < 0L)
      throw new IllegalArgumentException(
          String.format("initialDelay: %d (expected: >= 0)", new Object[] { Long.valueOf(initialDelay) })); 
    if (period <= 0L)
      throw new IllegalArgumentException(
          String.format("period: %d (expected: > 0)", new Object[] { Long.valueOf(period) })); 
    validateScheduled0(initialDelay, unit);
    validateScheduled0(period, unit);
    return schedule(new ScheduledFutureTask(this, command, 
          deadlineNanos(getCurrentTimeNanos(), unit.toNanos(initialDelay)), unit.toNanos(period)));
  }
  
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    ObjectUtil.checkNotNull(command, "command");
    ObjectUtil.checkNotNull(unit, "unit");
    if (initialDelay < 0L)
      throw new IllegalArgumentException(
          String.format("initialDelay: %d (expected: >= 0)", new Object[] { Long.valueOf(initialDelay) })); 
    if (delay <= 0L)
      throw new IllegalArgumentException(
          String.format("delay: %d (expected: > 0)", new Object[] { Long.valueOf(delay) })); 
    validateScheduled0(initialDelay, unit);
    validateScheduled0(delay, unit);
    return schedule(new ScheduledFutureTask(this, command, 
          deadlineNanos(getCurrentTimeNanos(), unit.toNanos(initialDelay)), -unit.toNanos(delay)));
  }
  
  private void validateScheduled0(long amount, TimeUnit unit) {
    validateScheduled(amount, unit);
  }
  
  @Deprecated
  protected void validateScheduled(long amount, TimeUnit unit) {}
  
  final void scheduleFromEventLoop(ScheduledFutureTask<?> task) {
    scheduledTaskQueue().add(task.setId(++this.nextTaskId));
  }
  
  private <V> ScheduledFuture<V> schedule(ScheduledFutureTask<V> task) {
    if (inEventLoop()) {
      scheduleFromEventLoop(task);
    } else {
      long deadlineNanos = task.deadlineNanos();
      if (beforeScheduledTaskSubmitted(deadlineNanos)) {
        execute(task);
      } else {
        lazyExecute(task);
        if (afterScheduledTaskSubmitted(deadlineNanos))
          execute(WAKEUP_TASK); 
      } 
    } 
    return task;
  }
  
  final void removeScheduled(ScheduledFutureTask<?> task) {
    assert task.isCancelled();
    if (inEventLoop()) {
      scheduledTaskQueue().removeTyped(task);
    } else {
      lazyExecute(task);
    } 
  }
  
  protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
    return true;
  }
  
  protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
    return true;
  }
}
