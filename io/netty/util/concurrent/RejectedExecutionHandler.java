package io.netty.util.concurrent;

public interface RejectedExecutionHandler {
  void rejected(Runnable paramRunnable, SingleThreadEventExecutor paramSingleThreadEventExecutor);
}
