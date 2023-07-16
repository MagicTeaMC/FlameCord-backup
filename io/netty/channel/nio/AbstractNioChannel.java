package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNioChannel extends AbstractChannel {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioChannel.class);
  
  private final SelectableChannel ch;
  
  protected final int readInterestOp;
  
  volatile SelectionKey selectionKey;
  
  boolean readPending;
  
  private final Runnable clearReadPendingRunnable = new Runnable() {
      public void run() {
        AbstractNioChannel.this.clearReadPending0();
      }
    };
  
  private ChannelPromise connectPromise;
  
  private Future<?> connectTimeoutFuture;
  
  private SocketAddress requestedRemoteAddress;
  
  protected AbstractNioChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
    super(parent);
    this.ch = ch;
    this.readInterestOp = readInterestOp;
    try {
      ch.configureBlocking(false);
    } catch (IOException e) {
      try {
        ch.close();
      } catch (IOException e2) {
        logger.warn("Failed to close a partially initialized socket.", e2);
      } 
      throw new ChannelException("Failed to enter non-blocking mode.", e);
    } 
  }
  
  public boolean isOpen() {
    return this.ch.isOpen();
  }
  
  public NioUnsafe unsafe() {
    return (NioUnsafe)super.unsafe();
  }
  
  protected SelectableChannel javaChannel() {
    return this.ch;
  }
  
  public NioEventLoop eventLoop() {
    return (NioEventLoop)super.eventLoop();
  }
  
  protected SelectionKey selectionKey() {
    assert this.selectionKey != null;
    return this.selectionKey;
  }
  
  @Deprecated
  protected boolean isReadPending() {
    return this.readPending;
  }
  
  @Deprecated
  protected void setReadPending(final boolean readPending) {
    if (isRegistered()) {
      NioEventLoop nioEventLoop = eventLoop();
      if (nioEventLoop.inEventLoop()) {
        setReadPending0(readPending);
      } else {
        nioEventLoop.execute(new Runnable() {
              public void run() {
                AbstractNioChannel.this.setReadPending0(readPending);
              }
            });
      } 
    } else {
      this.readPending = readPending;
    } 
  }
  
  protected final void clearReadPending() {
    if (isRegistered()) {
      NioEventLoop nioEventLoop = eventLoop();
      if (nioEventLoop.inEventLoop()) {
        clearReadPending0();
      } else {
        nioEventLoop.execute(this.clearReadPendingRunnable);
      } 
    } else {
      this.readPending = false;
    } 
  }
  
  private void setReadPending0(boolean readPending) {
    this.readPending = readPending;
    if (!readPending)
      ((AbstractNioUnsafe)unsafe()).removeReadOp(); 
  }
  
  private void clearReadPending0() {
    this.readPending = false;
    ((AbstractNioUnsafe)unsafe()).removeReadOp();
  }
  
  public static interface NioUnsafe extends Channel.Unsafe {
    SelectableChannel ch();
    
    void finishConnect();
    
    void read();
    
    void forceFlush();
  }
  
  protected abstract class AbstractNioUnsafe extends AbstractChannel.AbstractUnsafe implements NioUnsafe {
    protected AbstractNioUnsafe() {
      super(AbstractNioChannel.this);
    }
    
    protected final void removeReadOp() {
      SelectionKey key = AbstractNioChannel.this.selectionKey();
      if (!key.isValid())
        return; 
      int interestOps = key.interestOps();
      if ((interestOps & AbstractNioChannel.this.readInterestOp) != 0)
        key.interestOps(interestOps & (AbstractNioChannel.this.readInterestOp ^ 0xFFFFFFFF)); 
    }
    
    public final SelectableChannel ch() {
      return AbstractNioChannel.this.javaChannel();
    }
    
    public final void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      if (!promise.setUncancellable() || !ensureOpen(promise))
        return; 
      try {
        if (AbstractNioChannel.this.connectPromise != null)
          throw new ConnectionPendingException(); 
        boolean wasActive = AbstractNioChannel.this.isActive();
        if (AbstractNioChannel.this.doConnect(remoteAddress, localAddress)) {
          fulfillConnectPromise(promise, wasActive);
        } else {
          AbstractNioChannel.this.connectPromise = promise;
          AbstractNioChannel.this.requestedRemoteAddress = remoteAddress;
          int connectTimeoutMillis = AbstractNioChannel.this.config().getConnectTimeoutMillis();
          if (connectTimeoutMillis > 0)
            AbstractNioChannel.this.connectTimeoutFuture = (Future<?>)AbstractNioChannel.this.eventLoop().schedule(new Runnable() {
                  public void run() {
                    ChannelPromise connectPromise = AbstractNioChannel.this.connectPromise;
                    if (connectPromise != null && !connectPromise.isDone() && connectPromise
                      .tryFailure((Throwable)new ConnectTimeoutException("connection timed out: " + remoteAddress)))
                      AbstractNioChannel.AbstractNioUnsafe.this.close(AbstractNioChannel.AbstractNioUnsafe.this.voidPromise()); 
                  }
                },  connectTimeoutMillis, TimeUnit.MILLISECONDS); 
          promise.addListener((GenericFutureListener)new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                  if (future.isCancelled()) {
                    if (AbstractNioChannel.this.connectTimeoutFuture != null)
                      AbstractNioChannel.this.connectTimeoutFuture.cancel(false); 
                    AbstractNioChannel.this.connectPromise = null;
                    AbstractNioChannel.AbstractNioUnsafe.this.close(AbstractNioChannel.AbstractNioUnsafe.this.voidPromise());
                  } 
                }
              });
        } 
      } catch (Throwable t) {
        promise.tryFailure(annotateConnectException(t, remoteAddress));
        closeIfClosed();
      } 
    }
    
    private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
      if (promise == null)
        return; 
      boolean active = AbstractNioChannel.this.isActive();
      boolean promiseSet = promise.trySuccess();
      if (!wasActive && active)
        AbstractNioChannel.this.pipeline().fireChannelActive(); 
      if (!promiseSet)
        close(voidPromise()); 
    }
    
    private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
      if (promise == null)
        return; 
      promise.tryFailure(cause);
      closeIfClosed();
    }
    
    public final void finishConnect() {
      assert AbstractNioChannel.this.eventLoop().inEventLoop();
      try {
        boolean wasActive = AbstractNioChannel.this.isActive();
        AbstractNioChannel.this.doFinishConnect();
        fulfillConnectPromise(AbstractNioChannel.this.connectPromise, wasActive);
      } catch (Throwable t) {
        fulfillConnectPromise(AbstractNioChannel.this.connectPromise, annotateConnectException(t, AbstractNioChannel.this.requestedRemoteAddress));
      } finally {
        if (AbstractNioChannel.this.connectTimeoutFuture != null)
          AbstractNioChannel.this.connectTimeoutFuture.cancel(false); 
        AbstractNioChannel.this.connectPromise = null;
      } 
    }
    
    protected final void flush0() {
      if (!isFlushPending())
        super.flush0(); 
    }
    
    public final void forceFlush() {
      super.flush0();
    }
    
    private boolean isFlushPending() {
      SelectionKey selectionKey = AbstractNioChannel.this.selectionKey();
      return (selectionKey.isValid() && (selectionKey.interestOps() & 0x4) != 0);
    }
  }
  
  protected boolean isCompatible(EventLoop loop) {
    return loop instanceof NioEventLoop;
  }
  
  protected void doRegister() throws Exception {
    boolean selected = false;
    while (true) {
      try {
        this.selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this);
        return;
      } catch (CancelledKeyException e) {
        if (!selected) {
          eventLoop().selectNow();
          selected = true;
          continue;
        } 
        break;
      } 
    } 
    throw e;
  }
  
  protected void doDeregister() throws Exception {
    eventLoop().cancel(selectionKey());
  }
  
  protected void doBeginRead() throws Exception {
    SelectionKey selectionKey = this.selectionKey;
    if (!selectionKey.isValid())
      return; 
    this.readPending = true;
    int interestOps = selectionKey.interestOps();
    if ((interestOps & this.readInterestOp) == 0)
      selectionKey.interestOps(interestOps | this.readInterestOp); 
  }
  
  protected final ByteBuf newDirectBuffer(ByteBuf buf) {
    int readableBytes = buf.readableBytes();
    if (readableBytes == 0) {
      ReferenceCountUtil.safeRelease(buf);
      return Unpooled.EMPTY_BUFFER;
    } 
    ByteBufAllocator alloc = alloc();
    if (alloc.isDirectBufferPooled()) {
      ByteBuf byteBuf = alloc.directBuffer(readableBytes);
      byteBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
      ReferenceCountUtil.safeRelease(buf);
      return byteBuf;
    } 
    ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
    if (directBuf != null) {
      directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
      ReferenceCountUtil.safeRelease(buf);
      return directBuf;
    } 
    return buf;
  }
  
  protected final ByteBuf newDirectBuffer(ReferenceCounted holder, ByteBuf buf) {
    int readableBytes = buf.readableBytes();
    if (readableBytes == 0) {
      ReferenceCountUtil.safeRelease(holder);
      return Unpooled.EMPTY_BUFFER;
    } 
    ByteBufAllocator alloc = alloc();
    if (alloc.isDirectBufferPooled()) {
      ByteBuf byteBuf = alloc.directBuffer(readableBytes);
      byteBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
      ReferenceCountUtil.safeRelease(holder);
      return byteBuf;
    } 
    ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
    if (directBuf != null) {
      directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
      ReferenceCountUtil.safeRelease(holder);
      return directBuf;
    } 
    if (holder != buf) {
      buf.retain();
      ReferenceCountUtil.safeRelease(holder);
    } 
    return buf;
  }
  
  protected void doClose() throws Exception {
    ChannelPromise promise = this.connectPromise;
    if (promise != null) {
      promise.tryFailure(new ClosedChannelException());
      this.connectPromise = null;
    } 
    Future<?> future = this.connectTimeoutFuture;
    if (future != null) {
      future.cancel(false);
      this.connectTimeoutFuture = null;
    } 
  }
  
  protected abstract boolean doConnect(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2) throws Exception;
  
  protected abstract void doFinishConnect() throws Exception;
}
