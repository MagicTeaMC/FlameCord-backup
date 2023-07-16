package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadExecutorMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.jetbrains.annotations.Async.Schedule;

public abstract class SingleThreadEventExecutor extends AbstractScheduledEventExecutor implements OrderedEventExecutor {
  static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Math.max(16, 
      SystemPropertyUtil.getInt("io.netty.eventexecutor.maxPendingTasks", 2147483647));
  
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(SingleThreadEventExecutor.class);
  
  private static final int ST_NOT_STARTED = 1;
  
  private static final int ST_STARTED = 2;
  
  private static final int ST_SHUTTING_DOWN = 3;
  
  private static final int ST_SHUTDOWN = 4;
  
  private static final int ST_TERMINATED = 5;
  
  private static final Runnable NOOP_TASK = new Runnable() {
      public void run() {}
    };
  
  private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");
  
  private static final AtomicReferenceFieldUpdater<SingleThreadEventExecutor, ThreadProperties> PROPERTIES_UPDATER = AtomicReferenceFieldUpdater.newUpdater(SingleThreadEventExecutor.class, ThreadProperties.class, "threadProperties");
  
  private final Queue<Runnable> taskQueue;
  
  private volatile Thread thread;
  
  private volatile ThreadProperties threadProperties;
  
  private final Executor executor;
  
  private volatile boolean interrupted;
  
  private final CountDownLatch threadLock = new CountDownLatch(1);
  
  private final Set<Runnable> shutdownHooks = new LinkedHashSet<Runnable>();
  
  private final boolean addTaskWakesUp;
  
  private final int maxPendingTasks;
  
  private final RejectedExecutionHandler rejectedExecutionHandler;
  
  private long lastExecutionTime;
  
  private volatile int state = 1;
  
  private volatile long gracefulShutdownQuietPeriod;
  
  private volatile long gracefulShutdownTimeout;
  
  private long gracefulShutdownStartTime;
  
  private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
  
  protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
    this(parent, new ThreadPerTaskExecutor(threadFactory), addTaskWakesUp);
  }
  
  protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
    this(parent, new ThreadPerTaskExecutor(threadFactory), addTaskWakesUp, maxPendingTasks, rejectedHandler);
  }
  
  protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp) {
    this(parent, executor, addTaskWakesUp, DEFAULT_MAX_PENDING_EXECUTOR_TASKS, RejectedExecutionHandlers.reject());
  }
  
  protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
    super(parent);
    this.addTaskWakesUp = addTaskWakesUp;
    this.maxPendingTasks = Math.max(16, maxPendingTasks);
    this.executor = ThreadExecutorMap.apply(executor, this);
    this.taskQueue = newTaskQueue(this.maxPendingTasks);
    this.rejectedExecutionHandler = (RejectedExecutionHandler)ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
  }
  
  protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, Queue<Runnable> taskQueue, RejectedExecutionHandler rejectedHandler) {
    super(parent);
    this.addTaskWakesUp = addTaskWakesUp;
    this.maxPendingTasks = DEFAULT_MAX_PENDING_EXECUTOR_TASKS;
    this.executor = ThreadExecutorMap.apply(executor, this);
    this.taskQueue = (Queue<Runnable>)ObjectUtil.checkNotNull(taskQueue, "taskQueue");
    this.rejectedExecutionHandler = (RejectedExecutionHandler)ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
  }
  
  @Deprecated
  protected Queue<Runnable> newTaskQueue() {
    return newTaskQueue(this.maxPendingTasks);
  }
  
  protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
    return new LinkedBlockingQueue<Runnable>(maxPendingTasks);
  }
  
  protected void interruptThread() {
    Thread currentThread = this.thread;
    if (currentThread == null) {
      this.interrupted = true;
    } else {
      currentThread.interrupt();
    } 
  }
  
  protected Runnable pollTask() {
    assert inEventLoop();
    return pollTaskFrom(this.taskQueue);
  }
  
  protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
    while (true) {
      Runnable task = taskQueue.poll();
      if (task != WAKEUP_TASK)
        return task; 
    } 
  }
  
  protected Runnable takeTask() {
    assert inEventLoop();
    if (!(this.taskQueue instanceof BlockingQueue))
      throw new UnsupportedOperationException(); 
    BlockingQueue<Runnable> taskQueue = (BlockingQueue<Runnable>)this.taskQueue;
    while (true) {
      ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
      if (scheduledTask == null) {
        Runnable runnable = null;
        try {
          runnable = taskQueue.take();
          if (runnable == WAKEUP_TASK)
            runnable = null; 
        } catch (InterruptedException interruptedException) {}
        return runnable;
      } 
      long delayNanos = scheduledTask.delayNanos();
      Runnable task = null;
      if (delayNanos > 0L)
        try {
          task = taskQueue.poll(delayNanos, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
          return null;
        }  
      if (task == null) {
        fetchFromScheduledTaskQueue();
        task = taskQueue.poll();
      } 
      if (task != null)
        return task; 
    } 
  }
  
  private boolean fetchFromScheduledTaskQueue() {
    Runnable scheduledTask;
    if (this.scheduledTaskQueue == null || this.scheduledTaskQueue.isEmpty())
      return true; 
    long nanoTime = getCurrentTimeNanos();
    do {
      scheduledTask = pollScheduledTask(nanoTime);
      if (scheduledTask == null)
        return true; 
    } while (this.taskQueue.offer(scheduledTask));
    this.scheduledTaskQueue.add(scheduledTask);
    return false;
  }
  
  private boolean executeExpiredScheduledTasks() {
    if (this.scheduledTaskQueue == null || this.scheduledTaskQueue.isEmpty())
      return false; 
    long nanoTime = getCurrentTimeNanos();
    Runnable scheduledTask = pollScheduledTask(nanoTime);
    if (scheduledTask == null)
      return false; 
    do {
      safeExecute(scheduledTask);
    } while ((scheduledTask = pollScheduledTask(nanoTime)) != null);
    return true;
  }
  
  protected Runnable peekTask() {
    assert inEventLoop();
    return this.taskQueue.peek();
  }
  
  protected boolean hasTasks() {
    assert inEventLoop();
    return !this.taskQueue.isEmpty();
  }
  
  public int pendingTasks() {
    return this.taskQueue.size();
  }
  
  protected void addTask(Runnable task) {
    ObjectUtil.checkNotNull(task, "task");
    if (!offerTask(task))
      reject(task); 
  }
  
  final boolean offerTask(Runnable task) {
    if (isShutdown())
      reject(); 
    return this.taskQueue.offer(task);
  }
  
  protected boolean removeTask(Runnable task) {
    return this.taskQueue.remove(ObjectUtil.checkNotNull(task, "task"));
  }
  
  protected boolean runAllTasks() {
    boolean fetchedAll;
    assert inEventLoop();
    boolean ranAtLeastOne = false;
    do {
      fetchedAll = fetchFromScheduledTaskQueue();
      if (!runAllTasksFrom(this.taskQueue))
        continue; 
      ranAtLeastOne = true;
    } while (!fetchedAll);
    if (ranAtLeastOne)
      this.lastExecutionTime = getCurrentTimeNanos(); 
    afterRunningAllTasks();
    return ranAtLeastOne;
  }
  
  protected final boolean runScheduledAndExecutorTasks(int maxDrainAttempts) {
    int i;
    assert inEventLoop();
    int drainAttempt = 0;
    do {
      i = runExistingTasksFrom(this.taskQueue) | executeExpiredScheduledTasks();
    } while (i != 0 && ++drainAttempt < maxDrainAttempts);
    if (drainAttempt > 0)
      this.lastExecutionTime = getCurrentTimeNanos(); 
    afterRunningAllTasks();
    return (drainAttempt > 0);
  }
  
  protected final boolean runAllTasksFrom(Queue<Runnable> taskQueue) {
    Runnable task = pollTaskFrom(taskQueue);
    if (task == null)
      return false; 
    while (true) {
      safeExecute(task);
      task = pollTaskFrom(taskQueue);
      if (task == null)
        return true; 
    } 
  }
  
  private boolean runExistingTasksFrom(Queue<Runnable> taskQueue) {
    Runnable task = pollTaskFrom(taskQueue);
    if (task == null)
      return false; 
    int remaining = Math.min(this.maxPendingTasks, taskQueue.size());
    safeExecute(task);
    while (remaining-- > 0 && (task = taskQueue.poll()) != null)
      safeExecute(task); 
    return true;
  }
  
  protected boolean runAllTasks(long timeoutNanos) {
    long lastExecutionTime;
    fetchFromScheduledTaskQueue();
    Runnable task = pollTask();
    if (task == null) {
      afterRunningAllTasks();
      return false;
    } 
    long deadline = (timeoutNanos > 0L) ? (getCurrentTimeNanos() + timeoutNanos) : 0L;
    long runTasks = 0L;
    while (true) {
      safeExecute(task);
      runTasks++;
      if ((runTasks & 0x3FL) == 0L) {
        lastExecutionTime = getCurrentTimeNanos();
        if (lastExecutionTime >= deadline)
          break; 
      } 
      task = pollTask();
      if (task == null) {
        lastExecutionTime = getCurrentTimeNanos();
        break;
      } 
    } 
    afterRunningAllTasks();
    this.lastExecutionTime = lastExecutionTime;
    return true;
  }
  
  protected void afterRunningAllTasks() {}
  
  protected long delayNanos(long currentTimeNanos) {
    currentTimeNanos -= initialNanoTime();
    ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
    if (scheduledTask == null)
      return SCHEDULE_PURGE_INTERVAL; 
    return scheduledTask.delayNanos(currentTimeNanos);
  }
  
  protected long deadlineNanos() {
    ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
    if (scheduledTask == null)
      return getCurrentTimeNanos() + SCHEDULE_PURGE_INTERVAL; 
    return scheduledTask.deadlineNanos();
  }
  
  protected void updateLastExecutionTime() {
    this.lastExecutionTime = getCurrentTimeNanos();
  }
  
  protected void cleanup() {}
  
  protected void wakeup(boolean inEventLoop) {
    if (!inEventLoop)
      this.taskQueue.offer(WAKEUP_TASK); 
  }
  
  public boolean inEventLoop(Thread thread) {
    return (thread == this.thread);
  }
  
  public void addShutdownHook(final Runnable task) {
    if (inEventLoop()) {
      this.shutdownHooks.add(task);
    } else {
      execute(new Runnable() {
            public void run() {
              SingleThreadEventExecutor.this.shutdownHooks.add(task);
            }
          });
    } 
  }
  
  public void removeShutdownHook(final Runnable task) {
    if (inEventLoop()) {
      this.shutdownHooks.remove(task);
    } else {
      execute(new Runnable() {
            public void run() {
              SingleThreadEventExecutor.this.shutdownHooks.remove(task);
            }
          });
    } 
  }
  
  private boolean runShutdownHooks() {
    boolean ran = false;
    while (!this.shutdownHooks.isEmpty()) {
      List<Runnable> copy = new ArrayList<Runnable>(this.shutdownHooks);
      this.shutdownHooks.clear();
      for (Runnable task : copy) {
        try {
          runTask(task);
        } catch (Throwable t) {
          logger.warn("Shutdown hook raised an exception.", t);
        } finally {
          ran = true;
        } 
      } 
    } 
    if (ran)
      this.lastExecutionTime = getCurrentTimeNanos(); 
    return ran;
  }
  
  public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
    boolean wakeup;
    int oldState, newState;
    ObjectUtil.checkPositiveOrZero(quietPeriod, "quietPeriod");
    if (timeout < quietPeriod)
      throw new IllegalArgumentException("timeout: " + timeout + " (expected >= quietPeriod (" + quietPeriod + "))"); 
    ObjectUtil.checkNotNull(unit, "unit");
    if (isShuttingDown())
      return terminationFuture(); 
    boolean inEventLoop = inEventLoop();
    do {
      if (isShuttingDown())
        return terminationFuture(); 
      wakeup = true;
      oldState = this.state;
      if (inEventLoop) {
        newState = 3;
      } else {
        switch (oldState) {
          case 1:
          case 2:
            newState = 3;
            break;
          default:
            newState = oldState;
            wakeup = false;
            break;
        } 
      } 
    } while (!STATE_UPDATER.compareAndSet(this, oldState, newState));
    this.gracefulShutdownQuietPeriod = unit.toNanos(quietPeriod);
    this.gracefulShutdownTimeout = unit.toNanos(timeout);
    if (ensureThreadStarted(oldState))
      return this.terminationFuture; 
    if (wakeup) {
      this.taskQueue.offer(WAKEUP_TASK);
      if (!this.addTaskWakesUp)
        wakeup(inEventLoop); 
    } 
    return terminationFuture();
  }
  
  public Future<?> terminationFuture() {
    return this.terminationFuture;
  }
  
  @Deprecated
  public void shutdown() {
    boolean wakeup;
    int oldState, newState;
    if (isShutdown())
      return; 
    boolean inEventLoop = inEventLoop();
    do {
      if (isShuttingDown())
        return; 
      wakeup = true;
      oldState = this.state;
      if (inEventLoop) {
        newState = 4;
      } else {
        switch (oldState) {
          case 1:
          case 2:
          case 3:
            newState = 4;
            break;
          default:
            newState = oldState;
            wakeup = false;
            break;
        } 
      } 
    } while (!STATE_UPDATER.compareAndSet(this, oldState, newState));
    if (ensureThreadStarted(oldState))
      return; 
    if (wakeup) {
      this.taskQueue.offer(WAKEUP_TASK);
      if (!this.addTaskWakesUp)
        wakeup(inEventLoop); 
    } 
  }
  
  public boolean isShuttingDown() {
    return (this.state >= 3);
  }
  
  public boolean isShutdown() {
    return (this.state >= 4);
  }
  
  public boolean isTerminated() {
    return (this.state == 5);
  }
  
  protected boolean confirmShutdown() {
    if (!isShuttingDown())
      return false; 
    if (!inEventLoop())
      throw new IllegalStateException("must be invoked from an event loop"); 
    cancelScheduledTasks();
    if (this.gracefulShutdownStartTime == 0L)
      this.gracefulShutdownStartTime = getCurrentTimeNanos(); 
    if (runAllTasks() || runShutdownHooks()) {
      if (isShutdown())
        return true; 
      if (this.gracefulShutdownQuietPeriod == 0L)
        return true; 
      this.taskQueue.offer(WAKEUP_TASK);
      return false;
    } 
    long nanoTime = getCurrentTimeNanos();
    if (isShutdown() || nanoTime - this.gracefulShutdownStartTime > this.gracefulShutdownTimeout)
      return true; 
    if (nanoTime - this.lastExecutionTime <= this.gracefulShutdownQuietPeriod) {
      this.taskQueue.offer(WAKEUP_TASK);
      try {
        Thread.sleep(100L);
      } catch (InterruptedException interruptedException) {}
      return false;
    } 
    return true;
  }
  
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    ObjectUtil.checkNotNull(unit, "unit");
    if (inEventLoop())
      throw new IllegalStateException("cannot await termination of the current thread"); 
    this.threadLock.await(timeout, unit);
    return isTerminated();
  }
  
  public void execute(Runnable task) {
    execute0(task);
  }
  
  public void lazyExecute(Runnable task) {
    lazyExecute0(task);
  }
  
  private void execute0(@Schedule Runnable task) {
    ObjectUtil.checkNotNull(task, "task");
    execute(task, wakesUpForTask(task));
  }
  
  private void lazyExecute0(@Schedule Runnable task) {
    execute((Runnable)ObjectUtil.checkNotNull(task, "task"), false);
  }
  
  private void execute(Runnable task, boolean immediate) {
    boolean inEventLoop = inEventLoop();
    addTask(task);
    if (!inEventLoop) {
      startThread();
      if (isShutdown()) {
        boolean reject = false;
        try {
          if (removeTask(task))
            reject = true; 
        } catch (UnsupportedOperationException unsupportedOperationException) {}
        if (reject)
          reject(); 
      } 
    } 
    if (!this.addTaskWakesUp && immediate)
      wakeup(inEventLoop); 
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    throwIfInEventLoop("invokeAny");
    return super.invokeAny(tasks);
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    throwIfInEventLoop("invokeAny");
    return super.invokeAny(tasks, timeout, unit);
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    throwIfInEventLoop("invokeAll");
    return super.invokeAll(tasks);
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    throwIfInEventLoop("invokeAll");
    return super.invokeAll(tasks, timeout, unit);
  }
  
  private void throwIfInEventLoop(String method) {
    if (inEventLoop())
      throw new RejectedExecutionException("Calling " + method + " from within the EventLoop is not allowed"); 
  }
  
  public final ThreadProperties threadProperties() {
    ThreadProperties threadProperties = this.threadProperties;
    if (threadProperties == null) {
      Thread thread = this.thread;
      if (thread == null) {
        assert !inEventLoop();
        submit(NOOP_TASK).syncUninterruptibly();
        thread = this.thread;
        assert thread != null;
      } 
      threadProperties = new DefaultThreadProperties(thread);
      if (!PROPERTIES_UPDATER.compareAndSet(this, null, threadProperties))
        threadProperties = this.threadProperties; 
    } 
    return threadProperties;
  }
  
  protected boolean wakesUpForTask(Runnable task) {
    return true;
  }
  
  protected static void reject() {
    throw new RejectedExecutionException("event executor terminated");
  }
  
  protected final void reject(Runnable task) {
    this.rejectedExecutionHandler.rejected(task, this);
  }
  
  private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1L);
  
  private void startThread() {
    if (this.state == 1 && 
      STATE_UPDATER.compareAndSet(this, 1, 2)) {
      boolean success = false;
      try {
        doStartThread();
        success = true;
      } finally {
        if (!success)
          STATE_UPDATER.compareAndSet(this, 2, 1); 
      } 
    } 
  }
  
  private boolean ensureThreadStarted(int oldState) {
    if (oldState == 1)
      try {
        doStartThread();
      } catch (Throwable cause) {
        STATE_UPDATER.set(this, 5);
        this.terminationFuture.tryFailure(cause);
        if (!(cause instanceof Exception))
          PlatformDependent.throwException(cause); 
        return true;
      }  
    return false;
  }
  
  private void doStartThread() {
    assert this.thread == null;
    this.executor.execute(new Runnable() {
          public void run() {
            SingleThreadEventExecutor.this.thread = Thread.currentThread();
            if (SingleThreadEventExecutor.this.interrupted)
              SingleThreadEventExecutor.this.thread.interrupt(); 
            boolean success = false;
            SingleThreadEventExecutor.this.updateLastExecutionTime();
            try {
              SingleThreadEventExecutor.this.run();
              success = true;
            } catch (Throwable t) {
              SingleThreadEventExecutor.logger.warn("Unexpected exception from an event executor: ", t);
            } finally {
              int oldState;
              do {
                oldState = SingleThreadEventExecutor.this.state;
              } while (oldState < 3 && !SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, oldState, 3));
              if (success && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L && SingleThreadEventExecutor
                .logger.isErrorEnabled())
                SingleThreadEventExecutor.logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class
                    .getSimpleName() + ".confirmShutdown() must be called before run() implementation terminates."); 
              try {
                do {
                
                } while (!SingleThreadEventExecutor.this.confirmShutdown());
                do {
                  oldState = SingleThreadEventExecutor.this.state;
                } while (oldState < 4 && !SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, oldState, 4));
                SingleThreadEventExecutor.this.confirmShutdown();
              } finally {
                try {
                  SingleThreadEventExecutor.this.cleanup();
                } finally {
                  FastThreadLocal.removeAll();
                  SingleThreadEventExecutor.STATE_UPDATER.set(SingleThreadEventExecutor.this, 5);
                  SingleThreadEventExecutor.this.threadLock.countDown();
                  int numUserTasks = SingleThreadEventExecutor.this.drainTasks();
                  if (numUserTasks > 0 && SingleThreadEventExecutor.logger.isWarnEnabled())
                    SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + numUserTasks + ')'); 
                  SingleThreadEventExecutor.this.terminationFuture.setSuccess(null);
                } 
              } 
            } 
          }
        });
  }
  
  final int drainTasks() {
    int numTasks = 0;
    while (true) {
      Runnable runnable = this.taskQueue.poll();
      if (runnable == null)
        break; 
      if (WAKEUP_TASK != runnable)
        numTasks++; 
    } 
    return numTasks;
  }
  
  protected abstract void run();
  
  @Deprecated
  protected static interface NonWakeupRunnable extends AbstractEventExecutor.LazyRunnable {}
  
  private static final class DefaultThreadProperties implements ThreadProperties {
    private final Thread t;
    
    DefaultThreadProperties(Thread t) {
      this.t = t;
    }
    
    public Thread.State state() {
      return this.t.getState();
    }
    
    public int priority() {
      return this.t.getPriority();
    }
    
    public boolean isInterrupted() {
      return this.t.isInterrupted();
    }
    
    public boolean isDaemon() {
      return this.t.isDaemon();
    }
    
    public String name() {
      return this.t.getName();
    }
    
    public long id() {
      return this.t.getId();
    }
    
    public StackTraceElement[] stackTrace() {
      return this.t.getStackTrace();
    }
    
    public boolean isAlive() {
      return this.t.isAlive();
    }
  }
}
