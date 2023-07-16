package io.netty.channel.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Deque;
import java.util.concurrent.Callable;

public class SimpleChannelPool implements ChannelPool {
  private static final AttributeKey<SimpleChannelPool> POOL_KEY = AttributeKey.newInstance("io.netty.channel.pool.SimpleChannelPool");
  
  private final Deque<Channel> deque = PlatformDependent.newConcurrentDeque();
  
  private final ChannelPoolHandler handler;
  
  private final ChannelHealthChecker healthCheck;
  
  private final Bootstrap bootstrap;
  
  private final boolean releaseHealthCheck;
  
  private final boolean lastRecentUsed;
  
  public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler) {
    this(bootstrap, handler, ChannelHealthChecker.ACTIVE);
  }
  
  public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck) {
    this(bootstrap, handler, healthCheck, true);
  }
  
  public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, boolean releaseHealthCheck) {
    this(bootstrap, handler, healthCheck, releaseHealthCheck, true);
  }
  
  public SimpleChannelPool(Bootstrap bootstrap, final ChannelPoolHandler handler, ChannelHealthChecker healthCheck, boolean releaseHealthCheck, boolean lastRecentUsed) {
    this.handler = (ChannelPoolHandler)ObjectUtil.checkNotNull(handler, "handler");
    this.healthCheck = (ChannelHealthChecker)ObjectUtil.checkNotNull(healthCheck, "healthCheck");
    this.releaseHealthCheck = releaseHealthCheck;
    this.bootstrap = ((Bootstrap)ObjectUtil.checkNotNull(bootstrap, "bootstrap")).clone();
    this.bootstrap.handler((ChannelHandler)new ChannelInitializer<Channel>() {
          protected void initChannel(Channel ch) throws Exception {
            assert ch.eventLoop().inEventLoop();
            handler.channelCreated(ch);
          }
        });
    this.lastRecentUsed = lastRecentUsed;
  }
  
  protected Bootstrap bootstrap() {
    return this.bootstrap;
  }
  
  protected ChannelPoolHandler handler() {
    return this.handler;
  }
  
  protected ChannelHealthChecker healthChecker() {
    return this.healthCheck;
  }
  
  protected boolean releaseHealthCheck() {
    return this.releaseHealthCheck;
  }
  
  public final Future<Channel> acquire() {
    return acquire(this.bootstrap.config().group().next().newPromise());
  }
  
  public Future<Channel> acquire(Promise<Channel> promise) {
    return acquireHealthyFromPoolOrNew((Promise<Channel>)ObjectUtil.checkNotNull(promise, "promise"));
  }
  
  private Future<Channel> acquireHealthyFromPoolOrNew(final Promise<Channel> promise) {
    try {
      final Channel ch = pollChannel();
      if (ch == null) {
        Bootstrap bs = this.bootstrap.clone();
        bs.attr(POOL_KEY, this);
        ChannelFuture f = connectChannel(bs);
        if (f.isDone()) {
          notifyConnect(f, promise);
        } else {
          f.addListener((GenericFutureListener)new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                  SimpleChannelPool.this.notifyConnect(future, promise);
                }
              });
        } 
      } else {
        EventLoop loop = ch.eventLoop();
        if (loop.inEventLoop()) {
          doHealthCheck(ch, promise);
        } else {
          loop.execute(new Runnable() {
                public void run() {
                  SimpleChannelPool.this.doHealthCheck(ch, promise);
                }
              });
        } 
      } 
    } catch (Throwable cause) {
      promise.tryFailure(cause);
    } 
    return (Future<Channel>)promise;
  }
  
  private void notifyConnect(ChannelFuture future, Promise<Channel> promise) {
    Channel channel = null;
    try {
      if (future.isSuccess()) {
        channel = future.channel();
        this.handler.channelAcquired(channel);
        if (!promise.trySuccess(channel))
          release(channel); 
      } else {
        promise.tryFailure(future.cause());
      } 
    } catch (Throwable cause) {
      closeAndFail(channel, cause, promise);
    } 
  }
  
  private void doHealthCheck(final Channel channel, final Promise<Channel> promise) {
    try {
      assert channel.eventLoop().inEventLoop();
      Future<Boolean> f = this.healthCheck.isHealthy(channel);
      if (f.isDone()) {
        notifyHealthCheck(f, channel, promise);
      } else {
        f.addListener((GenericFutureListener)new FutureListener<Boolean>() {
              public void operationComplete(Future<Boolean> future) {
                SimpleChannelPool.this.notifyHealthCheck(future, channel, promise);
              }
            });
      } 
    } catch (Throwable cause) {
      closeAndFail(channel, cause, promise);
    } 
  }
  
  private void notifyHealthCheck(Future<Boolean> future, Channel channel, Promise<Channel> promise) {
    try {
      assert channel.eventLoop().inEventLoop();
      if (future.isSuccess() && ((Boolean)future.getNow()).booleanValue()) {
        channel.attr(POOL_KEY).set(this);
        this.handler.channelAcquired(channel);
        promise.setSuccess(channel);
      } else {
        closeChannel(channel);
        acquireHealthyFromPoolOrNew(promise);
      } 
    } catch (Throwable cause) {
      closeAndFail(channel, cause, promise);
    } 
  }
  
  protected ChannelFuture connectChannel(Bootstrap bs) {
    return bs.connect();
  }
  
  public final Future<Void> release(Channel channel) {
    return release(channel, channel.eventLoop().newPromise());
  }
  
  public Future<Void> release(final Channel channel, final Promise<Void> promise) {
    try {
      ObjectUtil.checkNotNull(channel, "channel");
      ObjectUtil.checkNotNull(promise, "promise");
      EventLoop loop = channel.eventLoop();
      if (loop.inEventLoop()) {
        doReleaseChannel(channel, promise);
      } else {
        loop.execute(new Runnable() {
              public void run() {
                SimpleChannelPool.this.doReleaseChannel(channel, promise);
              }
            });
      } 
    } catch (Throwable cause) {
      closeAndFail(channel, cause, promise);
    } 
    return (Future<Void>)promise;
  }
  
  private void doReleaseChannel(Channel channel, Promise<Void> promise) {
    try {
      assert channel.eventLoop().inEventLoop();
      if (channel.attr(POOL_KEY).getAndSet(null) != this) {
        closeAndFail(channel, new IllegalArgumentException("Channel " + channel + " was not acquired from this ChannelPool"), promise);
      } else if (this.releaseHealthCheck) {
        doHealthCheckOnRelease(channel, promise);
      } else {
        releaseAndOffer(channel, promise);
      } 
    } catch (Throwable cause) {
      closeAndFail(channel, cause, promise);
    } 
  }
  
  private void doHealthCheckOnRelease(final Channel channel, final Promise<Void> promise) throws Exception {
    final Future<Boolean> f = this.healthCheck.isHealthy(channel);
    if (f.isDone()) {
      releaseAndOfferIfHealthy(channel, promise, f);
    } else {
      f.addListener((GenericFutureListener)new FutureListener<Boolean>() {
            public void operationComplete(Future<Boolean> future) throws Exception {
              SimpleChannelPool.this.releaseAndOfferIfHealthy(channel, promise, f);
            }
          });
    } 
  }
  
  private void releaseAndOfferIfHealthy(Channel channel, Promise<Void> promise, Future<Boolean> future) {
    try {
      if (((Boolean)future.getNow()).booleanValue()) {
        releaseAndOffer(channel, promise);
      } else {
        this.handler.channelReleased(channel);
        promise.setSuccess(null);
      } 
    } catch (Throwable cause) {
      closeAndFail(channel, cause, promise);
    } 
  }
  
  private void releaseAndOffer(Channel channel, Promise<Void> promise) throws Exception {
    if (offerChannel(channel)) {
      this.handler.channelReleased(channel);
      promise.setSuccess(null);
    } else {
      closeAndFail(channel, new ChannelPoolFullException(), promise);
    } 
  }
  
  private void closeChannel(Channel channel) throws Exception {
    channel.attr(POOL_KEY).getAndSet(null);
    channel.close();
  }
  
  private void closeAndFail(Channel channel, Throwable cause, Promise<?> promise) {
    if (channel != null)
      try {
        closeChannel(channel);
      } catch (Throwable t) {
        promise.tryFailure(t);
      }  
    promise.tryFailure(cause);
  }
  
  protected Channel pollChannel() {
    return this.lastRecentUsed ? this.deque.pollLast() : this.deque.pollFirst();
  }
  
  protected boolean offerChannel(Channel channel) {
    return this.deque.offer(channel);
  }
  
  public void close() {
    while (true) {
      Channel channel = pollChannel();
      if (channel == null)
        break; 
      channel.close().awaitUninterruptibly();
    } 
  }
  
  public Future<Void> closeAsync() {
    return GlobalEventExecutor.INSTANCE.submit(new Callable<Void>() {
          public Void call() throws Exception {
            SimpleChannelPool.this.close();
            return null;
          }
        });
  }
  
  private static final class ChannelPoolFullException extends IllegalStateException {
    private ChannelPoolFullException() {
      super("ChannelPool full");
    }
    
    public Throwable fillInStackTrace() {
      return this;
    }
  }
}
