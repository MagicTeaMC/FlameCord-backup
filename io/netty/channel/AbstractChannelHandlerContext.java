package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakHint;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AbstractChannelHandlerContext implements ChannelHandlerContext, ResourceLeakHint {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannelHandlerContext.class);
  
  volatile AbstractChannelHandlerContext next;
  
  volatile AbstractChannelHandlerContext prev;
  
  private static final AtomicIntegerFieldUpdater<AbstractChannelHandlerContext> HANDLER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractChannelHandlerContext.class, "handlerState");
  
  private static final int ADD_PENDING = 1;
  
  private static final int ADD_COMPLETE = 2;
  
  private static final int REMOVE_COMPLETE = 3;
  
  private static final int INIT = 0;
  
  private final DefaultChannelPipeline pipeline;
  
  private final String name;
  
  private final boolean ordered;
  
  private final int executionMask;
  
  final EventExecutor executor;
  
  private ChannelFuture succeededFuture;
  
  private Tasks invokeTasks;
  
  private volatile int handlerState = 0;
  
  AbstractChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor, String name, Class<? extends ChannelHandler> handlerClass) {
    this.name = (String)ObjectUtil.checkNotNull(name, "name");
    this.pipeline = pipeline;
    this.executor = executor;
    this.executionMask = ChannelHandlerMask.mask(handlerClass);
    this.ordered = (executor == null || executor instanceof io.netty.util.concurrent.OrderedEventExecutor);
  }
  
  public Channel channel() {
    return this.pipeline.channel();
  }
  
  public ChannelPipeline pipeline() {
    return this.pipeline;
  }
  
  public ByteBufAllocator alloc() {
    return channel().config().getAllocator();
  }
  
  public EventExecutor executor() {
    if (this.executor == null)
      return (EventExecutor)channel().eventLoop(); 
    return this.executor;
  }
  
  public String name() {
    return this.name;
  }
  
  public ChannelHandlerContext fireChannelRegistered() {
    invokeChannelRegistered(findContextInbound(2));
    return this;
  }
  
  static void invokeChannelRegistered(final AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeChannelRegistered();
    } else {
      executor.execute(new Runnable() {
            public void run() {
              next.invokeChannelRegistered();
            }
          });
    } 
  }
  
  private void invokeChannelRegistered() {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.channelRegistered(this);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).channelRegistered(this);
        } else {
          ((ChannelInboundHandler)handler).channelRegistered(this);
        } 
      } catch (Throwable t) {
        invokeExceptionCaught(t);
      } 
    } else {
      fireChannelRegistered();
    } 
  }
  
  public ChannelHandlerContext fireChannelUnregistered() {
    invokeChannelUnregistered(findContextInbound(4));
    return this;
  }
  
  static void invokeChannelUnregistered(final AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeChannelUnregistered();
    } else {
      executor.execute(new Runnable() {
            public void run() {
              next.invokeChannelUnregistered();
            }
          });
    } 
  }
  
  private void invokeChannelUnregistered() {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.channelUnregistered(this);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).channelUnregistered(this);
        } else {
          ((ChannelInboundHandler)handler).channelUnregistered(this);
        } 
      } catch (Throwable t) {
        invokeExceptionCaught(t);
      } 
    } else {
      fireChannelUnregistered();
    } 
  }
  
  public ChannelHandlerContext fireChannelActive() {
    invokeChannelActive(findContextInbound(8));
    return this;
  }
  
  static void invokeChannelActive(final AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeChannelActive();
    } else {
      executor.execute(new Runnable() {
            public void run() {
              next.invokeChannelActive();
            }
          });
    } 
  }
  
  private void invokeChannelActive() {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.channelActive(this);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).channelActive(this);
        } else {
          ((ChannelInboundHandler)handler).channelActive(this);
        } 
      } catch (Throwable t) {
        invokeExceptionCaught(t);
      } 
    } else {
      fireChannelActive();
    } 
  }
  
  public ChannelHandlerContext fireChannelInactive() {
    invokeChannelInactive(findContextInbound(16));
    return this;
  }
  
  static void invokeChannelInactive(final AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeChannelInactive();
    } else {
      executor.execute(new Runnable() {
            public void run() {
              next.invokeChannelInactive();
            }
          });
    } 
  }
  
  private void invokeChannelInactive() {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.channelInactive(this);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).channelInactive(this);
        } else {
          ((ChannelInboundHandler)handler).channelInactive(this);
        } 
      } catch (Throwable t) {
        invokeExceptionCaught(t);
      } 
    } else {
      fireChannelInactive();
    } 
  }
  
  public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
    invokeExceptionCaught(findContextInbound(1), cause);
    return this;
  }
  
  static void invokeExceptionCaught(final AbstractChannelHandlerContext next, final Throwable cause) {
    ObjectUtil.checkNotNull(cause, "cause");
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeExceptionCaught(cause);
    } else {
      try {
        executor.execute(new Runnable() {
              public void run() {
                next.invokeExceptionCaught(cause);
              }
            });
      } catch (Throwable t) {
        if (logger.isWarnEnabled()) {
          logger.warn("Failed to submit an exceptionCaught() event.", t);
          logger.warn("The exceptionCaught() event that was failed to submit was:", cause);
        } 
      } 
    } 
  }
  
  private void invokeExceptionCaught(Throwable cause) {
    if (invokeHandler()) {
      try {
        handler().exceptionCaught(this, cause);
      } catch (Throwable error) {
        if (logger.isDebugEnabled()) {
          logger.debug("An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:", 
              
              ThrowableUtil.stackTraceToString(error), cause);
        } else if (logger.isWarnEnabled()) {
          logger.warn("An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:", error, cause);
        } 
      } 
    } else {
      fireExceptionCaught(cause);
    } 
  }
  
  public ChannelHandlerContext fireUserEventTriggered(Object event) {
    invokeUserEventTriggered(findContextInbound(128), event);
    return this;
  }
  
  static void invokeUserEventTriggered(final AbstractChannelHandlerContext next, final Object event) {
    ObjectUtil.checkNotNull(event, "event");
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeUserEventTriggered(event);
    } else {
      executor.execute(new Runnable() {
            public void run() {
              next.invokeUserEventTriggered(event);
            }
          });
    } 
  }
  
  private void invokeUserEventTriggered(Object event) {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.userEventTriggered(this, event);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).userEventTriggered(this, event);
        } else {
          ((ChannelInboundHandler)handler).userEventTriggered(this, event);
        } 
      } catch (Throwable t) {
        invokeExceptionCaught(t);
      } 
    } else {
      fireUserEventTriggered(event);
    } 
  }
  
  public ChannelHandlerContext fireChannelRead(Object msg) {
    invokeChannelRead(findContextInbound(32), msg);
    return this;
  }
  
  static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
    final Object m = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeChannelRead(m);
    } else {
      executor.execute(new Runnable() {
            public void run() {
              next.invokeChannelRead(m);
            }
          });
    } 
  }
  
  private void invokeChannelRead(Object msg) {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.channelRead(this, msg);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).channelRead(this, msg);
        } else {
          ((ChannelInboundHandler)handler).channelRead(this, msg);
        } 
      } catch (Throwable t) {
        invokeExceptionCaught(t);
      } 
    } else {
      fireChannelRead(msg);
    } 
  }
  
  public ChannelHandlerContext fireChannelReadComplete() {
    invokeChannelReadComplete(findContextInbound(64));
    return this;
  }
  
  static void invokeChannelReadComplete(AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeChannelReadComplete();
    } else {
      Tasks tasks = next.invokeTasks;
      if (tasks == null)
        next.invokeTasks = tasks = new Tasks(next); 
      executor.execute(tasks.invokeChannelReadCompleteTask);
    } 
  }
  
  private void invokeChannelReadComplete() {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.channelReadComplete(this);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).channelReadComplete(this);
        } else {
          ((ChannelInboundHandler)handler).channelReadComplete(this);
        } 
      } catch (Throwable t) {
        invokeExceptionCaught(t);
      } 
    } else {
      fireChannelReadComplete();
    } 
  }
  
  public ChannelHandlerContext fireChannelWritabilityChanged() {
    invokeChannelWritabilityChanged(findContextInbound(256));
    return this;
  }
  
  static void invokeChannelWritabilityChanged(AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeChannelWritabilityChanged();
    } else {
      Tasks tasks = next.invokeTasks;
      if (tasks == null)
        next.invokeTasks = tasks = new Tasks(next); 
      executor.execute(tasks.invokeChannelWritableStateChangedTask);
    } 
  }
  
  private void invokeChannelWritabilityChanged() {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.channelWritabilityChanged(this);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).channelWritabilityChanged(this);
        } else {
          ((ChannelInboundHandler)handler).channelWritabilityChanged(this);
        } 
      } catch (Throwable t) {
        invokeExceptionCaught(t);
      } 
    } else {
      fireChannelWritabilityChanged();
    } 
  }
  
  public ChannelFuture bind(SocketAddress localAddress) {
    return bind(localAddress, newPromise());
  }
  
  public ChannelFuture connect(SocketAddress remoteAddress) {
    return connect(remoteAddress, newPromise());
  }
  
  public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
    return connect(remoteAddress, localAddress, newPromise());
  }
  
  public ChannelFuture disconnect() {
    return disconnect(newPromise());
  }
  
  public ChannelFuture close() {
    return close(newPromise());
  }
  
  public ChannelFuture deregister() {
    return deregister(newPromise());
  }
  
  public ChannelFuture bind(final SocketAddress localAddress, final ChannelPromise promise) {
    ObjectUtil.checkNotNull(localAddress, "localAddress");
    if (isNotValidPromise(promise, false))
      return promise; 
    final AbstractChannelHandlerContext next = findContextOutbound(512);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeBind(localAddress, promise);
    } else {
      safeExecute(executor, new Runnable() {
            public void run() {
              next.invokeBind(localAddress, promise);
            }
          }promise, null, false);
    } 
    return promise;
  }
  
  private void invokeBind(SocketAddress localAddress, ChannelPromise promise) {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.bind(this, localAddress, promise);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).bind(this, localAddress, promise);
        } else {
          ((ChannelOutboundHandler)handler).bind(this, localAddress, promise);
        } 
      } catch (Throwable t) {
        notifyOutboundHandlerException(t, promise);
      } 
    } else {
      bind(localAddress, promise);
    } 
  }
  
  public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
    return connect(remoteAddress, null, promise);
  }
  
  public ChannelFuture connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
    ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
    if (isNotValidPromise(promise, false))
      return promise; 
    final AbstractChannelHandlerContext next = findContextOutbound(1024);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeConnect(remoteAddress, localAddress, promise);
    } else {
      safeExecute(executor, new Runnable() {
            public void run() {
              next.invokeConnect(remoteAddress, localAddress, promise);
            }
          }promise, null, false);
    } 
    return promise;
  }
  
  private void invokeConnect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.connect(this, remoteAddress, localAddress, promise);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).connect(this, remoteAddress, localAddress, promise);
        } else {
          ((ChannelOutboundHandler)handler).connect(this, remoteAddress, localAddress, promise);
        } 
      } catch (Throwable t) {
        notifyOutboundHandlerException(t, promise);
      } 
    } else {
      connect(remoteAddress, localAddress, promise);
    } 
  }
  
  public ChannelFuture disconnect(final ChannelPromise promise) {
    if (!channel().metadata().hasDisconnect())
      return close(promise); 
    if (isNotValidPromise(promise, false))
      return promise; 
    final AbstractChannelHandlerContext next = findContextOutbound(2048);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeDisconnect(promise);
    } else {
      safeExecute(executor, new Runnable() {
            public void run() {
              next.invokeDisconnect(promise);
            }
          },  promise, null, false);
    } 
    return promise;
  }
  
  private void invokeDisconnect(ChannelPromise promise) {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.disconnect(this, promise);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).disconnect(this, promise);
        } else {
          ((ChannelOutboundHandler)handler).disconnect(this, promise);
        } 
      } catch (Throwable t) {
        notifyOutboundHandlerException(t, promise);
      } 
    } else {
      disconnect(promise);
    } 
  }
  
  public ChannelFuture close(final ChannelPromise promise) {
    if (isNotValidPromise(promise, false))
      return promise; 
    final AbstractChannelHandlerContext next = findContextOutbound(4096);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeClose(promise);
    } else {
      safeExecute(executor, new Runnable() {
            public void run() {
              next.invokeClose(promise);
            }
          },  promise, null, false);
    } 
    return promise;
  }
  
  private void invokeClose(ChannelPromise promise) {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.close(this, promise);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).close(this, promise);
        } else {
          ((ChannelOutboundHandler)handler).close(this, promise);
        } 
      } catch (Throwable t) {
        notifyOutboundHandlerException(t, promise);
      } 
    } else {
      close(promise);
    } 
  }
  
  public ChannelFuture deregister(final ChannelPromise promise) {
    if (isNotValidPromise(promise, false))
      return promise; 
    final AbstractChannelHandlerContext next = findContextOutbound(8192);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeDeregister(promise);
    } else {
      safeExecute(executor, new Runnable() {
            public void run() {
              next.invokeDeregister(promise);
            }
          },  promise, null, false);
    } 
    return promise;
  }
  
  private void invokeDeregister(ChannelPromise promise) {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.deregister(this, promise);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).deregister(this, promise);
        } else {
          ((ChannelOutboundHandler)handler).deregister(this, promise);
        } 
      } catch (Throwable t) {
        notifyOutboundHandlerException(t, promise);
      } 
    } else {
      deregister(promise);
    } 
  }
  
  public ChannelHandlerContext read() {
    AbstractChannelHandlerContext next = findContextOutbound(16384);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeRead();
    } else {
      Tasks tasks = next.invokeTasks;
      if (tasks == null)
        next.invokeTasks = tasks = new Tasks(next); 
      executor.execute(tasks.invokeReadTask);
    } 
    return this;
  }
  
  private void invokeRead() {
    if (invokeHandler()) {
      try {
        ChannelHandler handler = handler();
        DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
        if (handler == headContext) {
          headContext.read(this);
        } else if (handler instanceof ChannelDuplexHandler) {
          ((ChannelDuplexHandler)handler).read(this);
        } else {
          ((ChannelOutboundHandler)handler).read(this);
        } 
      } catch (Throwable t) {
        invokeExceptionCaught(t);
      } 
    } else {
      read();
    } 
  }
  
  public ChannelFuture write(Object msg) {
    return write(msg, newPromise());
  }
  
  public ChannelFuture write(Object msg, ChannelPromise promise) {
    write(msg, false, promise);
    return promise;
  }
  
  void invokeWrite(Object msg, ChannelPromise promise) {
    if (invokeHandler()) {
      invokeWrite0(msg, promise);
    } else {
      write(msg, promise);
    } 
  }
  
  private void invokeWrite0(Object msg, ChannelPromise promise) {
    try {
      ChannelHandler handler = handler();
      DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
      if (handler == headContext) {
        headContext.write(this, msg, promise);
      } else if (handler instanceof ChannelDuplexHandler) {
        ((ChannelDuplexHandler)handler).write(this, msg, promise);
      } else {
        ((ChannelOutboundHandler)handler).write(this, msg, promise);
      } 
    } catch (Throwable t) {
      notifyOutboundHandlerException(t, promise);
    } 
  }
  
  public ChannelHandlerContext flush() {
    AbstractChannelHandlerContext next = findContextOutbound(65536);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      next.invokeFlush();
    } else {
      Tasks tasks = next.invokeTasks;
      if (tasks == null)
        next.invokeTasks = tasks = new Tasks(next); 
      safeExecute(executor, tasks.invokeFlushTask, channel().voidPromise(), null, false);
    } 
    return this;
  }
  
  private void invokeFlush() {
    if (invokeHandler()) {
      invokeFlush0();
    } else {
      flush();
    } 
  }
  
  private void invokeFlush0() {
    try {
      ChannelHandler handler = handler();
      DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
      if (handler == headContext) {
        headContext.flush(this);
      } else if (handler instanceof ChannelDuplexHandler) {
        ((ChannelDuplexHandler)handler).flush(this);
      } else {
        ((ChannelOutboundHandler)handler).flush(this);
      } 
    } catch (Throwable t) {
      invokeExceptionCaught(t);
    } 
  }
  
  public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
    write(msg, true, promise);
    return promise;
  }
  
  void invokeWriteAndFlush(Object msg, ChannelPromise promise) {
    if (invokeHandler()) {
      invokeWrite0(msg, promise);
      invokeFlush0();
    } else {
      writeAndFlush(msg, promise);
    } 
  }
  
  private void write(Object msg, boolean flush, ChannelPromise promise) {
    ObjectUtil.checkNotNull(msg, "msg");
    try {
      if (isNotValidPromise(promise, true)) {
        ReferenceCountUtil.release(msg);
        return;
      } 
    } catch (RuntimeException e) {
      ReferenceCountUtil.release(msg);
      throw e;
    } 
    AbstractChannelHandlerContext next = findContextOutbound(flush ? 98304 : 32768);
    Object m = this.pipeline.touch(msg, next);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
      if (flush) {
        next.invokeWriteAndFlush(m, promise);
      } else {
        next.invokeWrite(m, promise);
      } 
    } else {
      WriteTask task = WriteTask.newInstance(next, m, promise, flush);
      if (!safeExecute(executor, task, promise, m, !flush))
        task.cancel(); 
    } 
  }
  
  public ChannelFuture writeAndFlush(Object msg) {
    return writeAndFlush(msg, newPromise());
  }
  
  private static void notifyOutboundHandlerException(Throwable cause, ChannelPromise promise) {
    PromiseNotificationUtil.tryFailure(promise, cause, (promise instanceof VoidChannelPromise) ? null : logger);
  }
  
  public ChannelPromise newPromise() {
    return new DefaultChannelPromise(channel(), executor());
  }
  
  public ChannelProgressivePromise newProgressivePromise() {
    return new DefaultChannelProgressivePromise(channel(), executor());
  }
  
  public ChannelFuture newSucceededFuture() {
    ChannelFuture succeededFuture = this.succeededFuture;
    if (succeededFuture == null)
      this.succeededFuture = succeededFuture = new SucceededChannelFuture(channel(), executor()); 
    return succeededFuture;
  }
  
  public ChannelFuture newFailedFuture(Throwable cause) {
    return new FailedChannelFuture(channel(), executor(), cause);
  }
  
  private boolean isNotValidPromise(ChannelPromise promise, boolean allowVoidPromise) {
    ObjectUtil.checkNotNull(promise, "promise");
    if (promise.isDone()) {
      if (promise.isCancelled())
        return true; 
      throw new IllegalArgumentException("promise already done: " + promise);
    } 
    if (promise.channel() != channel())
      throw new IllegalArgumentException(String.format("promise.channel does not match: %s (expected: %s)", new Object[] { promise
              .channel(), channel() })); 
    if (promise.getClass() == DefaultChannelPromise.class)
      return false; 
    if (!allowVoidPromise && promise instanceof VoidChannelPromise)
      throw new IllegalArgumentException(
          StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation"); 
    if (promise instanceof AbstractChannel.CloseFuture)
      throw new IllegalArgumentException(
          StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline"); 
    return false;
  }
  
  private AbstractChannelHandlerContext findContextInbound(int mask) {
    AbstractChannelHandlerContext ctx = this;
    EventExecutor currentExecutor = executor();
    while (true) {
      ctx = ctx.next;
      if (!skipContext(ctx, currentExecutor, mask, 510))
        return ctx; 
    } 
  }
  
  private AbstractChannelHandlerContext findContextOutbound(int mask) {
    AbstractChannelHandlerContext ctx = this;
    EventExecutor currentExecutor = executor();
    while (true) {
      ctx = ctx.prev;
      if (!skipContext(ctx, currentExecutor, mask, 130560))
        return ctx; 
    } 
  }
  
  private static boolean skipContext(AbstractChannelHandlerContext ctx, EventExecutor currentExecutor, int mask, int onlyMask) {
    return ((ctx.executionMask & (onlyMask | mask)) == 0 || (ctx
      
      .executor() == currentExecutor && (ctx.executionMask & mask) == 0));
  }
  
  public ChannelPromise voidPromise() {
    return channel().voidPromise();
  }
  
  final void setRemoved() {
    this.handlerState = 3;
  }
  
  final boolean setAddComplete() {
    while (true) {
      int oldState = this.handlerState;
      if (oldState == 3)
        return false; 
      if (HANDLER_STATE_UPDATER.compareAndSet(this, oldState, 2))
        return true; 
    } 
  }
  
  final void setAddPending() {
    boolean updated = HANDLER_STATE_UPDATER.compareAndSet(this, 0, 1);
    assert updated;
  }
  
  final void callHandlerAdded() throws Exception {
    if (setAddComplete())
      handler().handlerAdded(this); 
  }
  
  final void callHandlerRemoved() throws Exception {
    try {
      if (this.handlerState == 2)
        handler().handlerRemoved(this); 
    } finally {
      setRemoved();
    } 
  }
  
  private boolean invokeHandler() {
    int handlerState = this.handlerState;
    return (handlerState == 2 || (!this.ordered && handlerState == 1));
  }
  
  public boolean isRemoved() {
    return (this.handlerState == 3);
  }
  
  public <T> Attribute<T> attr(AttributeKey<T> key) {
    return channel().attr(key);
  }
  
  public <T> boolean hasAttr(AttributeKey<T> key) {
    return channel().hasAttr(key);
  }
  
  private static boolean safeExecute(EventExecutor executor, Runnable runnable, ChannelPromise promise, Object msg, boolean lazy) {
    try {
      if (lazy && executor instanceof AbstractEventExecutor) {
        ((AbstractEventExecutor)executor).lazyExecute(runnable);
      } else {
        executor.execute(runnable);
      } 
      return true;
    } catch (Throwable cause) {
      try {
        if (msg != null)
          ReferenceCountUtil.release(msg); 
      } finally {
        promise.setFailure(cause);
      } 
      return false;
    } 
  }
  
  public String toHintString() {
    return '\'' + this.name + "' will handle the message from this point.";
  }
  
  public String toString() {
    return StringUtil.simpleClassName(ChannelHandlerContext.class) + '(' + this.name + ", " + channel() + ')';
  }
  
  static final class WriteTask implements Runnable {
    private static final ObjectPool<WriteTask> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<WriteTask>() {
          public AbstractChannelHandlerContext.WriteTask newObject(ObjectPool.Handle<AbstractChannelHandlerContext.WriteTask> handle) {
            return new AbstractChannelHandlerContext.WriteTask(handle);
          }
        });
    
    static WriteTask newInstance(AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise, boolean flush) {
      WriteTask task = (WriteTask)RECYCLER.get();
      init(task, ctx, msg, promise, flush);
      return task;
    }
    
    private static final boolean ESTIMATE_TASK_SIZE_ON_SUBMIT = SystemPropertyUtil.getBoolean("io.netty.transport.estimateSizeOnSubmit", true);
    
    private static final int WRITE_TASK_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.writeTaskSizeOverhead", 32);
    
    private final ObjectPool.Handle<WriteTask> handle;
    
    private AbstractChannelHandlerContext ctx;
    
    private Object msg;
    
    private ChannelPromise promise;
    
    private int size;
    
    private WriteTask(ObjectPool.Handle<? extends WriteTask> handle) {
      this.handle = (ObjectPool.Handle)handle;
    }
    
    protected static void init(WriteTask task, AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise, boolean flush) {
      task.ctx = ctx;
      task.msg = msg;
      task.promise = promise;
      if (ESTIMATE_TASK_SIZE_ON_SUBMIT) {
        task.size = ctx.pipeline.estimatorHandle().size(msg) + WRITE_TASK_OVERHEAD;
        ctx.pipeline.incrementPendingOutboundBytes(task.size);
      } else {
        task.size = 0;
      } 
      if (flush)
        task.size |= Integer.MIN_VALUE; 
    }
    
    public void run() {
      try {
        decrementPendingOutboundBytes();
        if (this.size >= 0) {
          this.ctx.invokeWrite(this.msg, this.promise);
        } else {
          this.ctx.invokeWriteAndFlush(this.msg, this.promise);
        } 
      } finally {
        recycle();
      } 
    }
    
    void cancel() {
      try {
        decrementPendingOutboundBytes();
      } finally {
        recycle();
      } 
    }
    
    private void decrementPendingOutboundBytes() {
      if (ESTIMATE_TASK_SIZE_ON_SUBMIT)
        this.ctx.pipeline.decrementPendingOutboundBytes((this.size & Integer.MAX_VALUE)); 
    }
    
    private void recycle() {
      this.ctx = null;
      this.msg = null;
      this.promise = null;
      this.handle.recycle(this);
    }
  }
  
  private static final class Tasks {
    private final AbstractChannelHandlerContext next;
    
    private final Runnable invokeChannelReadCompleteTask = new Runnable() {
        public void run() {
          AbstractChannelHandlerContext.Tasks.this.next.invokeChannelReadComplete();
        }
      };
    
    private final Runnable invokeReadTask = new Runnable() {
        public void run() {
          AbstractChannelHandlerContext.Tasks.this.next.invokeRead();
        }
      };
    
    private final Runnable invokeChannelWritableStateChangedTask = new Runnable() {
        public void run() {
          AbstractChannelHandlerContext.Tasks.this.next.invokeChannelWritabilityChanged();
        }
      };
    
    private final Runnable invokeFlushTask = new Runnable() {
        public void run() {
          AbstractChannelHandlerContext.Tasks.this.next.invokeFlush();
        }
      };
    
    Tasks(AbstractChannelHandlerContext next) {
      this.next = next;
    }
  }
}
