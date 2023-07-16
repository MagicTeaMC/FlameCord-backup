package io.netty.channel;

import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultEventLoop extends SingleThreadEventLoop {
  public DefaultEventLoop() {
    this((EventLoopGroup)null);
  }
  
  public DefaultEventLoop(ThreadFactory threadFactory) {
    this((EventLoopGroup)null, threadFactory);
  }
  
  public DefaultEventLoop(Executor executor) {
    this((EventLoopGroup)null, executor);
  }
  
  public DefaultEventLoop(EventLoopGroup parent) {
    this(parent, (ThreadFactory)new DefaultThreadFactory(DefaultEventLoop.class));
  }
  
  public DefaultEventLoop(EventLoopGroup parent, ThreadFactory threadFactory) {
    super(parent, threadFactory, true);
  }
  
  public DefaultEventLoop(EventLoopGroup parent, Executor executor) {
    super(parent, executor, true);
  }
  
  protected void run() {
    do {
      Runnable task = takeTask();
      if (task == null)
        continue; 
      runTask(task);
      updateLastExecutionTime();
    } while (!confirmShutdown());
  }
}
