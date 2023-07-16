package io.netty.channel.oio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import java.net.SocketAddress;

@Deprecated
public abstract class AbstractOioChannel extends AbstractChannel {
  protected static final int SO_TIMEOUT = 1000;
  
  boolean readPending;
  
  boolean readWhenInactive;
  
  final Runnable readTask = new Runnable() {
      public void run() {
        AbstractOioChannel.this.doRead();
      }
    };
  
  private final Runnable clearReadPendingRunnable = new Runnable() {
      public void run() {
        AbstractOioChannel.this.readPending = false;
      }
    };
  
  protected AbstractOioChannel(Channel parent) {
    super(parent);
  }
  
  protected AbstractChannel.AbstractUnsafe newUnsafe() {
    return new DefaultOioUnsafe();
  }
  
  private final class DefaultOioUnsafe extends AbstractChannel.AbstractUnsafe {
    private DefaultOioUnsafe() {
      super(AbstractOioChannel.this);
    }
    
    public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      if (!promise.setUncancellable() || !ensureOpen(promise))
        return; 
      try {
        boolean wasActive = AbstractOioChannel.this.isActive();
        AbstractOioChannel.this.doConnect(remoteAddress, localAddress);
        boolean active = AbstractOioChannel.this.isActive();
        safeSetSuccess(promise);
        if (!wasActive && active)
          AbstractOioChannel.this.pipeline().fireChannelActive(); 
      } catch (Throwable t) {
        safeSetFailure(promise, annotateConnectException(t, remoteAddress));
        closeIfClosed();
      } 
    }
  }
  
  protected boolean isCompatible(EventLoop loop) {
    return loop instanceof io.netty.channel.ThreadPerChannelEventLoop;
  }
  
  protected abstract void doConnect(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2) throws Exception;
  
  protected void doBeginRead() throws Exception {
    if (this.readPending)
      return; 
    if (!isActive()) {
      this.readWhenInactive = true;
      return;
    } 
    this.readPending = true;
    eventLoop().execute(this.readTask);
  }
  
  protected abstract void doRead();
  
  @Deprecated
  protected boolean isReadPending() {
    return this.readPending;
  }
  
  @Deprecated
  protected void setReadPending(final boolean readPending) {
    if (isRegistered()) {
      EventLoop eventLoop = eventLoop();
      if (eventLoop.inEventLoop()) {
        this.readPending = readPending;
      } else {
        eventLoop.execute(new Runnable() {
              public void run() {
                AbstractOioChannel.this.readPending = readPending;
              }
            });
      } 
    } else {
      this.readPending = readPending;
    } 
  }
  
  protected final void clearReadPending() {
    if (isRegistered()) {
      EventLoop eventLoop = eventLoop();
      if (eventLoop.inEventLoop()) {
        this.readPending = false;
      } else {
        eventLoop.execute(this.clearReadPendingRunnable);
      } 
    } else {
      this.readPending = false;
    } 
  }
}
