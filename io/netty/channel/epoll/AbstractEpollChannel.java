package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.UnixChannel;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.TimeUnit;

abstract class AbstractEpollChannel extends AbstractChannel implements UnixChannel {
  private static final ChannelMetadata METADATA = new ChannelMetadata(false);
  
  final LinuxSocket socket;
  
  private ChannelPromise connectPromise;
  
  private Future<?> connectTimeoutFuture;
  
  private SocketAddress requestedRemoteAddress;
  
  private volatile SocketAddress local;
  
  private volatile SocketAddress remote;
  
  protected int flags = Native.EPOLLET;
  
  boolean inputClosedSeenErrorOnRead;
  
  boolean epollInReadyRunnablePending;
  
  protected volatile boolean active;
  
  AbstractEpollChannel(LinuxSocket fd) {
    this((Channel)null, fd, false);
  }
  
  AbstractEpollChannel(Channel parent, LinuxSocket fd, boolean active) {
    super(parent);
    this.socket = (LinuxSocket)ObjectUtil.checkNotNull(fd, "fd");
    this.active = active;
    if (active) {
      this.local = fd.localAddress();
      this.remote = fd.remoteAddress();
    } 
  }
  
  AbstractEpollChannel(Channel parent, LinuxSocket fd, SocketAddress remote) {
    super(parent);
    this.socket = (LinuxSocket)ObjectUtil.checkNotNull(fd, "fd");
    this.active = true;
    this.remote = remote;
    this.local = fd.localAddress();
  }
  
  static boolean isSoErrorZero(Socket fd) {
    try {
      return (fd.getSoError() == 0);
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  void setFlag(int flag) throws IOException {
    if (!isFlagSet(flag)) {
      this.flags |= flag;
      modifyEvents();
    } 
  }
  
  void clearFlag(int flag) throws IOException {
    if (isFlagSet(flag)) {
      this.flags &= flag ^ 0xFFFFFFFF;
      modifyEvents();
    } 
  }
  
  boolean isFlagSet(int flag) {
    return ((this.flags & flag) != 0);
  }
  
  public final FileDescriptor fd() {
    return (FileDescriptor)this.socket;
  }
  
  public boolean isActive() {
    return this.active;
  }
  
  public ChannelMetadata metadata() {
    return METADATA;
  }
  
  protected void doClose() throws Exception {
    this.active = false;
    this.inputClosedSeenErrorOnRead = true;
    try {
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
      if (isRegistered()) {
        EventLoop loop = eventLoop();
        if (loop.inEventLoop()) {
          doDeregister();
        } else {
          loop.execute(new Runnable() {
                public void run() {
                  try {
                    AbstractEpollChannel.this.doDeregister();
                  } catch (Throwable cause) {
                    AbstractEpollChannel.this.pipeline().fireExceptionCaught(cause);
                  } 
                }
              });
        } 
      } 
    } finally {
      this.socket.close();
    } 
  }
  
  void resetCachedAddresses() {
    this.local = this.socket.localAddress();
    this.remote = this.socket.remoteAddress();
  }
  
  protected void doDisconnect() throws Exception {
    doClose();
  }
  
  protected boolean isCompatible(EventLoop loop) {
    return loop instanceof EpollEventLoop;
  }
  
  public boolean isOpen() {
    return this.socket.isOpen();
  }
  
  protected void doDeregister() throws Exception {
    ((EpollEventLoop)eventLoop()).remove(this);
  }
  
  protected final void doBeginRead() throws Exception {
    AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)unsafe();
    unsafe.readPending = true;
    setFlag(Native.EPOLLIN);
    if (unsafe.maybeMoreDataToRead)
      unsafe.executeEpollInReadyRunnable((ChannelConfig)config()); 
  }
  
  final boolean shouldBreakEpollInReady(ChannelConfig config) {
    return (this.socket.isInputShutdown() && (this.inputClosedSeenErrorOnRead || !isAllowHalfClosure(config)));
  }
  
  private static boolean isAllowHalfClosure(ChannelConfig config) {
    if (config instanceof EpollDomainSocketChannelConfig)
      return ((EpollDomainSocketChannelConfig)config).isAllowHalfClosure(); 
    return (config instanceof SocketChannelConfig && ((SocketChannelConfig)config)
      .isAllowHalfClosure());
  }
  
  final void clearEpollIn() {
    if (isRegistered()) {
      EventLoop loop = eventLoop();
      final AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)unsafe();
      if (loop.inEventLoop()) {
        unsafe.clearEpollIn0();
      } else {
        loop.execute(new Runnable() {
              public void run() {
                if (!unsafe.readPending && !AbstractEpollChannel.this.config().isAutoRead())
                  unsafe.clearEpollIn0(); 
              }
            });
      } 
    } else {
      this.flags &= Native.EPOLLIN ^ 0xFFFFFFFF;
    } 
  }
  
  private void modifyEvents() throws IOException {
    if (isOpen() && isRegistered())
      ((EpollEventLoop)eventLoop()).modify(this); 
  }
  
  protected void doRegister() throws Exception {
    this.epollInReadyRunnablePending = false;
    ((EpollEventLoop)eventLoop()).add(this);
  }
  
  protected final ByteBuf newDirectBuffer(ByteBuf buf) {
    return newDirectBuffer(buf, buf);
  }
  
  protected final ByteBuf newDirectBuffer(Object holder, ByteBuf buf) {
    int readableBytes = buf.readableBytes();
    if (readableBytes == 0) {
      ReferenceCountUtil.release(holder);
      return Unpooled.EMPTY_BUFFER;
    } 
    ByteBufAllocator alloc = alloc();
    if (alloc.isDirectBufferPooled())
      return newDirectBuffer0(holder, buf, alloc, readableBytes); 
    ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
    if (directBuf == null)
      return newDirectBuffer0(holder, buf, alloc, readableBytes); 
    directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
    ReferenceCountUtil.safeRelease(holder);
    return directBuf;
  }
  
  private static ByteBuf newDirectBuffer0(Object holder, ByteBuf buf, ByteBufAllocator alloc, int capacity) {
    ByteBuf directBuf = alloc.directBuffer(capacity);
    directBuf.writeBytes(buf, buf.readerIndex(), capacity);
    ReferenceCountUtil.safeRelease(holder);
    return directBuf;
  }
  
  protected static void checkResolvable(InetSocketAddress addr) {
    if (addr.isUnresolved())
      throw new UnresolvedAddressException(); 
  }
  
  protected final int doReadBytes(ByteBuf byteBuf) throws Exception {
    int localReadAmount, writerIndex = byteBuf.writerIndex();
    unsafe().recvBufAllocHandle().attemptedBytesRead(byteBuf.writableBytes());
    if (byteBuf.hasMemoryAddress()) {
      localReadAmount = this.socket.recvAddress(byteBuf.memoryAddress(), writerIndex, byteBuf.capacity());
    } else {
      ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, byteBuf.writableBytes());
      localReadAmount = this.socket.recv(buf, buf.position(), buf.limit());
    } 
    if (localReadAmount > 0)
      byteBuf.writerIndex(writerIndex + localReadAmount); 
    return localReadAmount;
  }
  
  protected final int doWriteBytes(ChannelOutboundBuffer in, ByteBuf buf) throws Exception {
    if (buf.hasMemoryAddress()) {
      int localFlushedAmount = this.socket.sendAddress(buf.memoryAddress(), buf.readerIndex(), buf.writerIndex());
      if (localFlushedAmount > 0) {
        in.removeBytes(localFlushedAmount);
        return 1;
      } 
    } else {
      ByteBuffer nioBuf = (buf.nioBufferCount() == 1) ? buf.internalNioBuffer(buf.readerIndex(), buf.readableBytes()) : buf.nioBuffer();
      int localFlushedAmount = this.socket.send(nioBuf, nioBuf.position(), nioBuf.limit());
      if (localFlushedAmount > 0) {
        nioBuf.position(nioBuf.position() + localFlushedAmount);
        in.removeBytes(localFlushedAmount);
        return 1;
      } 
    } 
    return Integer.MAX_VALUE;
  }
  
  final long doWriteOrSendBytes(ByteBuf data, InetSocketAddress remoteAddress, boolean fastOpen) throws IOException {
    assert !fastOpen || remoteAddress != null : "fastOpen requires a remote address";
    if (data.hasMemoryAddress()) {
      long memoryAddress = data.memoryAddress();
      if (remoteAddress == null)
        return this.socket.sendAddress(memoryAddress, data.readerIndex(), data.writerIndex()); 
      return this.socket.sendToAddress(memoryAddress, data.readerIndex(), data.writerIndex(), remoteAddress
          .getAddress(), remoteAddress.getPort(), fastOpen);
    } 
    if (data.nioBufferCount() > 1) {
      IovArray array = ((EpollEventLoop)eventLoop()).cleanIovArray();
      array.add(data, data.readerIndex(), data.readableBytes());
      int cnt = array.count();
      assert cnt != 0;
      if (remoteAddress == null)
        return this.socket.writevAddresses(array.memoryAddress(0), cnt); 
      return this.socket.sendToAddresses(array.memoryAddress(0), cnt, remoteAddress
          .getAddress(), remoteAddress.getPort(), fastOpen);
    } 
    ByteBuffer nioData = data.internalNioBuffer(data.readerIndex(), data.readableBytes());
    if (remoteAddress == null)
      return this.socket.send(nioData, nioData.position(), nioData.limit()); 
    return this.socket.sendTo(nioData, nioData.position(), nioData.limit(), remoteAddress
        .getAddress(), remoteAddress.getPort(), fastOpen);
  }
  
  protected abstract class AbstractEpollUnsafe extends AbstractChannel.AbstractUnsafe {
    boolean readPending;
    
    boolean maybeMoreDataToRead;
    
    private EpollRecvByteAllocatorHandle allocHandle;
    
    private final Runnable epollInReadyRunnable;
    
    protected AbstractEpollUnsafe() {
      super(AbstractEpollChannel.this);
      this.epollInReadyRunnable = new Runnable() {
          public void run() {
            AbstractEpollChannel.this.epollInReadyRunnablePending = false;
            AbstractEpollChannel.AbstractEpollUnsafe.this.epollInReady();
          }
        };
    }
    
    final void epollInBefore() {
      this.maybeMoreDataToRead = false;
    }
    
    final void epollInFinally(ChannelConfig config) {
      this.maybeMoreDataToRead = this.allocHandle.maybeMoreDataToRead();
      if (this.allocHandle.isReceivedRdHup() || (this.readPending && this.maybeMoreDataToRead)) {
        executeEpollInReadyRunnable(config);
      } else if (!this.readPending && !config.isAutoRead()) {
        AbstractEpollChannel.this.clearEpollIn();
      } 
    }
    
    final void executeEpollInReadyRunnable(ChannelConfig config) {
      if (AbstractEpollChannel.this.epollInReadyRunnablePending || !AbstractEpollChannel.this.isActive() || AbstractEpollChannel.this.shouldBreakEpollInReady(config))
        return; 
      AbstractEpollChannel.this.epollInReadyRunnablePending = true;
      AbstractEpollChannel.this.eventLoop().execute(this.epollInReadyRunnable);
    }
    
    final void epollRdHupReady() {
      recvBufAllocHandle().receivedRdHup();
      if (AbstractEpollChannel.this.isActive()) {
        epollInReady();
      } else {
        shutdownInput(true);
      } 
      clearEpollRdHup();
    }
    
    private void clearEpollRdHup() {
      try {
        AbstractEpollChannel.this.clearFlag(Native.EPOLLRDHUP);
      } catch (IOException e) {
        AbstractEpollChannel.this.pipeline().fireExceptionCaught(e);
        close(voidPromise());
      } 
    }
    
    void shutdownInput(boolean rdHup) {
      if (!AbstractEpollChannel.this.socket.isInputShutdown()) {
        if (AbstractEpollChannel.isAllowHalfClosure((ChannelConfig)AbstractEpollChannel.this.config())) {
          try {
            AbstractEpollChannel.this.socket.shutdown(true, false);
          } catch (IOException ignored) {
            fireEventAndClose(ChannelInputShutdownEvent.INSTANCE);
            return;
          } catch (NotYetConnectedException notYetConnectedException) {}
          clearEpollIn0();
          AbstractEpollChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
        } else {
          close(voidPromise());
        } 
      } else if (!rdHup && !AbstractEpollChannel.this.inputClosedSeenErrorOnRead) {
        AbstractEpollChannel.this.inputClosedSeenErrorOnRead = true;
        AbstractEpollChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
      } 
    }
    
    private void fireEventAndClose(Object evt) {
      AbstractEpollChannel.this.pipeline().fireUserEventTriggered(evt);
      close(voidPromise());
    }
    
    public EpollRecvByteAllocatorHandle recvBufAllocHandle() {
      if (this.allocHandle == null)
        this.allocHandle = newEpollHandle((RecvByteBufAllocator.ExtendedHandle)super.recvBufAllocHandle()); 
      return this.allocHandle;
    }
    
    EpollRecvByteAllocatorHandle newEpollHandle(RecvByteBufAllocator.ExtendedHandle handle) {
      return new EpollRecvByteAllocatorHandle(handle);
    }
    
    protected final void flush0() {
      if (!AbstractEpollChannel.this.isFlagSet(Native.EPOLLOUT))
        super.flush0(); 
    }
    
    final void epollOutReady() {
      if (AbstractEpollChannel.this.connectPromise != null) {
        finishConnect();
      } else if (!AbstractEpollChannel.this.socket.isOutputShutdown()) {
        super.flush0();
      } 
    }
    
    protected final void clearEpollIn0() {
      assert AbstractEpollChannel.this.eventLoop().inEventLoop();
      try {
        this.readPending = false;
        AbstractEpollChannel.this.clearFlag(Native.EPOLLIN);
      } catch (IOException e) {
        AbstractEpollChannel.this.pipeline().fireExceptionCaught(e);
        AbstractEpollChannel.this.unsafe().close(AbstractEpollChannel.this.unsafe().voidPromise());
      } 
    }
    
    public void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      if (!promise.setUncancellable() || !ensureOpen(promise))
        return; 
      try {
        if (AbstractEpollChannel.this.connectPromise != null)
          throw new ConnectionPendingException(); 
        boolean wasActive = AbstractEpollChannel.this.isActive();
        if (AbstractEpollChannel.this.doConnect(remoteAddress, localAddress)) {
          fulfillConnectPromise(promise, wasActive);
        } else {
          AbstractEpollChannel.this.connectPromise = promise;
          AbstractEpollChannel.this.requestedRemoteAddress = remoteAddress;
          int connectTimeoutMillis = AbstractEpollChannel.this.config().getConnectTimeoutMillis();
          if (connectTimeoutMillis > 0)
            AbstractEpollChannel.this.connectTimeoutFuture = (Future<?>)AbstractEpollChannel.this.eventLoop().schedule(new Runnable() {
                  public void run() {
                    ChannelPromise connectPromise = AbstractEpollChannel.this.connectPromise;
                    if (connectPromise != null && !connectPromise.isDone() && connectPromise
                      .tryFailure((Throwable)new ConnectTimeoutException("connection timed out: " + remoteAddress)))
                      AbstractEpollChannel.AbstractEpollUnsafe.this.close(AbstractEpollChannel.AbstractEpollUnsafe.this.voidPromise()); 
                  }
                },  connectTimeoutMillis, TimeUnit.MILLISECONDS); 
          promise.addListener((GenericFutureListener)new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                  if (future.isCancelled()) {
                    if (AbstractEpollChannel.this.connectTimeoutFuture != null)
                      AbstractEpollChannel.this.connectTimeoutFuture.cancel(false); 
                    AbstractEpollChannel.this.connectPromise = null;
                    AbstractEpollChannel.AbstractEpollUnsafe.this.close(AbstractEpollChannel.AbstractEpollUnsafe.this.voidPromise());
                  } 
                }
              });
        } 
      } catch (Throwable t) {
        closeIfClosed();
        promise.tryFailure(annotateConnectException(t, remoteAddress));
      } 
    }
    
    private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
      if (promise == null)
        return; 
      AbstractEpollChannel.this.active = true;
      boolean active = AbstractEpollChannel.this.isActive();
      boolean promiseSet = promise.trySuccess();
      if (!wasActive && active)
        AbstractEpollChannel.this.pipeline().fireChannelActive(); 
      if (!promiseSet)
        close(voidPromise()); 
    }
    
    private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
      if (promise == null)
        return; 
      promise.tryFailure(cause);
      closeIfClosed();
    }
    
    private void finishConnect() {
      assert AbstractEpollChannel.this.eventLoop().inEventLoop();
      boolean connectStillInProgress = false;
      try {
        boolean wasActive = AbstractEpollChannel.this.isActive();
        if (!doFinishConnect()) {
          connectStillInProgress = true;
          return;
        } 
        fulfillConnectPromise(AbstractEpollChannel.this.connectPromise, wasActive);
      } catch (Throwable t) {
        fulfillConnectPromise(AbstractEpollChannel.this.connectPromise, annotateConnectException(t, AbstractEpollChannel.this.requestedRemoteAddress));
      } finally {
        if (!connectStillInProgress) {
          if (AbstractEpollChannel.this.connectTimeoutFuture != null)
            AbstractEpollChannel.this.connectTimeoutFuture.cancel(false); 
          AbstractEpollChannel.this.connectPromise = null;
        } 
      } 
    }
    
    private boolean doFinishConnect() throws Exception {
      if (AbstractEpollChannel.this.socket.finishConnect()) {
        AbstractEpollChannel.this.clearFlag(Native.EPOLLOUT);
        if (AbstractEpollChannel.this.requestedRemoteAddress instanceof InetSocketAddress)
          AbstractEpollChannel.this.remote = UnixChannelUtil.computeRemoteAddr((InetSocketAddress)AbstractEpollChannel.this.requestedRemoteAddress, AbstractEpollChannel.this.socket.remoteAddress()); 
        AbstractEpollChannel.this.requestedRemoteAddress = null;
        return true;
      } 
      AbstractEpollChannel.this.setFlag(Native.EPOLLOUT);
      return false;
    }
    
    abstract void epollInReady();
  }
  
  protected void doBind(SocketAddress local) throws Exception {
    if (local instanceof InetSocketAddress)
      checkResolvable((InetSocketAddress)local); 
    this.socket.bind(local);
    this.local = this.socket.localAddress();
  }
  
  protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
    if (localAddress instanceof InetSocketAddress)
      checkResolvable((InetSocketAddress)localAddress); 
    InetSocketAddress remoteSocketAddr = (remoteAddress instanceof InetSocketAddress) ? (InetSocketAddress)remoteAddress : null;
    if (remoteSocketAddr != null)
      checkResolvable(remoteSocketAddr); 
    if (this.remote != null)
      throw new AlreadyConnectedException(); 
    if (localAddress != null)
      this.socket.bind(localAddress); 
    boolean connected = doConnect0(remoteAddress);
    if (connected)
      this
        .remote = (remoteSocketAddr == null) ? remoteAddress : UnixChannelUtil.computeRemoteAddr(remoteSocketAddr, this.socket.remoteAddress()); 
    this.local = this.socket.localAddress();
    return connected;
  }
  
  boolean doConnect0(SocketAddress remote) throws Exception {
    boolean success = false;
    try {
      boolean connected = this.socket.connect(remote);
      if (!connected)
        setFlag(Native.EPOLLOUT); 
      success = true;
      return connected;
    } finally {
      if (!success)
        doClose(); 
    } 
  }
  
  protected SocketAddress localAddress0() {
    return this.local;
  }
  
  protected SocketAddress remoteAddress0() {
    return this.remote;
  }
  
  public abstract EpollChannelConfig config();
  
  protected abstract AbstractEpollUnsafe newUnsafe();
}
