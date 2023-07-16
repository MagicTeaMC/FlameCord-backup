package io.netty.util.concurrent;

public interface EventExecutorChooserFactory {
  EventExecutorChooser newChooser(EventExecutor[] paramArrayOfEventExecutor);
  
  public static interface EventExecutorChooser {
    EventExecutor next();
  }
}
