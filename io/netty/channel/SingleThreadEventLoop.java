package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {
  protected static final int DEFAULT_MAX_PENDING_TASKS = Math.max(16, 
      SystemPropertyUtil.getInt("io.netty.eventLoop.maxPendingTasks", 2147483647));
  
  private final Queue<Runnable> tailTasks;
  
  protected SingleThreadEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
    this(parent, threadFactory, addTaskWakesUp, DEFAULT_MAX_PENDING_TASKS, RejectedExecutionHandlers.reject());
  }
  
  protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp) {
    this(parent, executor, addTaskWakesUp, DEFAULT_MAX_PENDING_TASKS, RejectedExecutionHandlers.reject());
  }
  
  protected SingleThreadEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedExecutionHandler) {
    super(parent, threadFactory, addTaskWakesUp, maxPendingTasks, rejectedExecutionHandler);
    this.tailTasks = newTaskQueue(maxPendingTasks);
  }
  
  protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedExecutionHandler) {
    super(parent, executor, addTaskWakesUp, maxPendingTasks, rejectedExecutionHandler);
    this.tailTasks = newTaskQueue(maxPendingTasks);
  }
  
  protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp, Queue<Runnable> taskQueue, Queue<Runnable> tailTaskQueue, RejectedExecutionHandler rejectedExecutionHandler) {
    super(parent, executor, addTaskWakesUp, taskQueue, rejectedExecutionHandler);
    this.tailTasks = (Queue<Runnable>)ObjectUtil.checkNotNull(tailTaskQueue, "tailTaskQueue");
  }
  
  public EventLoopGroup parent() {
    return (EventLoopGroup)super.parent();
  }
  
  public EventLoop next() {
    return (EventLoop)super.next();
  }
  
  public ChannelFuture register(Channel channel) {
    return register(new DefaultChannelPromise(channel, (EventExecutor)this));
  }
  
  public ChannelFuture register(ChannelPromise promise) {
    ObjectUtil.checkNotNull(promise, "promise");
    promise.channel().unsafe().register(this, promise);
    return promise;
  }
  
  @Deprecated
  public ChannelFuture register(Channel channel, ChannelPromise promise) {
    ObjectUtil.checkNotNull(promise, "promise");
    ObjectUtil.checkNotNull(channel, "channel");
    channel.unsafe().register(this, promise);
    return promise;
  }
  
  public final void executeAfterEventLoopIteration(Runnable task) {
    ObjectUtil.checkNotNull(task, "task");
    if (isShutdown())
      reject(); 
    if (!this.tailTasks.offer(task))
      reject(task); 
    if (wakesUpForTask(task))
      wakeup(inEventLoop()); 
  }
  
  final boolean removeAfterEventLoopIterationTask(Runnable task) {
    return this.tailTasks.remove(ObjectUtil.checkNotNull(task, "task"));
  }
  
  protected void afterRunningAllTasks() {
    runAllTasksFrom(this.tailTasks);
  }
  
  protected boolean hasTasks() {
    return (super.hasTasks() || !this.tailTasks.isEmpty());
  }
  
  public int pendingTasks() {
    return super.pendingTasks() + this.tailTasks.size();
  }
  
  public int registeredChannels() {
    return -1;
  }
  
  public Iterator<Channel> registeredChannelsIterator() {
    throw new UnsupportedOperationException("registeredChannelsIterator");
  }
  
  protected static final class ChannelsReadOnlyIterator<T extends Channel> implements Iterator<Channel> {
    private final Iterator<T> channelIterator;
    
    public ChannelsReadOnlyIterator(Iterable<T> channelIterable) {
      this
        .channelIterator = ((Iterable<T>)ObjectUtil.checkNotNull(channelIterable, "channelIterable")).iterator();
    }
    
    public boolean hasNext() {
      return this.channelIterator.hasNext();
    }
    
    public Channel next() {
      return (Channel)this.channelIterator.next();
    }
    
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }
    
    public static <T> Iterator<T> empty() {
      return (Iterator)EMPTY;
    }
    
    private static final Iterator<Object> EMPTY = new Iterator() {
        public boolean hasNext() {
          return false;
        }
        
        public Object next() {
          throw new NoSuchElementException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException("remove");
        }
      };
  }
}
