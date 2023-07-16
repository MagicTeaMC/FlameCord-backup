package io.netty.util.internal;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class ThreadExecutorMap {
  private static final FastThreadLocal<EventExecutor> mappings = new FastThreadLocal();
  
  public static EventExecutor currentExecutor() {
    return (EventExecutor)mappings.get();
  }
  
  private static void setCurrentEventExecutor(EventExecutor executor) {
    mappings.set(executor);
  }
  
  public static Executor apply(final Executor executor, final EventExecutor eventExecutor) {
    ObjectUtil.checkNotNull(executor, "executor");
    ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
    return new Executor() {
        public void execute(Runnable command) {
          executor.execute(ThreadExecutorMap.apply(command, eventExecutor));
        }
      };
  }
  
  public static Runnable apply(final Runnable command, final EventExecutor eventExecutor) {
    ObjectUtil.checkNotNull(command, "command");
    ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
    return new Runnable() {
        public void run() {
          ThreadExecutorMap.setCurrentEventExecutor(eventExecutor);
          try {
            command.run();
          } finally {
            ThreadExecutorMap.setCurrentEventExecutor(null);
          } 
        }
      };
  }
  
  public static ThreadFactory apply(final ThreadFactory threadFactory, final EventExecutor eventExecutor) {
    ObjectUtil.checkNotNull(threadFactory, "threadFactory");
    ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
    return new ThreadFactory() {
        public Thread newThread(Runnable r) {
          return threadFactory.newThread(ThreadExecutorMap.apply(r, eventExecutor));
        }
      };
  }
}
