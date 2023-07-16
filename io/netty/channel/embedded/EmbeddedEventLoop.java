package io.netty.channel.embedded;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EmbeddedEventLoop extends AbstractScheduledEventExecutor implements EventLoop {
  private long startTime = initialNanoTime();
  
  private long frozenTimestamp;
  
  private boolean timeFrozen;
  
  private final Queue<Runnable> tasks = new ArrayDeque<Runnable>(2);
  
  public EventLoopGroup parent() {
    return (EventLoopGroup)super.parent();
  }
  
  public EventLoop next() {
    return (EventLoop)super.next();
  }
  
  public void execute(Runnable command) {
    this.tasks.add(ObjectUtil.checkNotNull(command, "command"));
  }
  
  void runTasks() {
    while (true) {
      Runnable task = this.tasks.poll();
      if (task == null)
        break; 
      task.run();
    } 
  }
  
  boolean hasPendingNormalTasks() {
    return !this.tasks.isEmpty();
  }
  
  long runScheduledTasks() {
    long time = getCurrentTimeNanos();
    while (true) {
      Runnable task = pollScheduledTask(time);
      if (task == null)
        return nextScheduledTaskNano(); 
      task.run();
    } 
  }
  
  long nextScheduledTask() {
    return nextScheduledTaskNano();
  }
  
  protected long getCurrentTimeNanos() {
    if (this.timeFrozen)
      return this.frozenTimestamp; 
    return System.nanoTime() - this.startTime;
  }
  
  void advanceTimeBy(long nanos) {
    if (this.timeFrozen) {
      this.frozenTimestamp += nanos;
    } else {
      this.startTime -= nanos;
    } 
  }
  
  void freezeTime() {
    if (!this.timeFrozen) {
      this.frozenTimestamp = getCurrentTimeNanos();
      this.timeFrozen = true;
    } 
  }
  
  void unfreezeTime() {
    if (this.timeFrozen) {
      this.startTime = System.nanoTime() - this.frozenTimestamp;
      this.timeFrozen = false;
    } 
  }
  
  protected void cancelScheduledTasks() {
    super.cancelScheduledTasks();
  }
  
  public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }
  
  public Future<?> terminationFuture() {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public void shutdown() {
    throw new UnsupportedOperationException();
  }
  
  public boolean isShuttingDown() {
    return false;
  }
  
  public boolean isShutdown() {
    return false;
  }
  
  public boolean isTerminated() {
    return false;
  }
  
  public boolean awaitTermination(long timeout, TimeUnit unit) {
    return false;
  }
  
  public ChannelFuture register(Channel channel) {
    return register((ChannelPromise)new DefaultChannelPromise(channel, (EventExecutor)this));
  }
  
  public ChannelFuture register(ChannelPromise promise) {
    ObjectUtil.checkNotNull(promise, "promise");
    promise.channel().unsafe().register(this, promise);
    return (ChannelFuture)promise;
  }
  
  @Deprecated
  public ChannelFuture register(Channel channel, ChannelPromise promise) {
    channel.unsafe().register(this, promise);
    return (ChannelFuture)promise;
  }
  
  public boolean inEventLoop() {
    return true;
  }
  
  public boolean inEventLoop(Thread thread) {
    return true;
  }
}
