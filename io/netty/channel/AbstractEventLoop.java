package io.netty.channel;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;

public abstract class AbstractEventLoop extends AbstractEventExecutor implements EventLoop {
  protected AbstractEventLoop() {}
  
  protected AbstractEventLoop(EventLoopGroup parent) {
    super(parent);
  }
  
  public EventLoopGroup parent() {
    return (EventLoopGroup)super.parent();
  }
  
  public EventLoop next() {
    return (EventLoop)super.next();
  }
}
