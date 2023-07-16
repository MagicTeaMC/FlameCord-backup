package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractCoalescingBufferQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPromise;
import io.netty.channel.unix.UnixChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;

public class SslHandler extends ByteToMessageDecoder implements ChannelOutboundHandler {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslHandler.class);
  
  private static final Pattern IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
  
  private static final Pattern IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
  
  private static final int STATE_SENT_FIRST_MESSAGE = 1;
  
  private static final int STATE_FLUSHED_BEFORE_HANDSHAKE = 2;
  
  private static final int STATE_READ_DURING_HANDSHAKE = 4;
  
  private static final int STATE_HANDSHAKE_STARTED = 8;
  
  private static final int STATE_NEEDS_FLUSH = 16;
  
  private static final int STATE_OUTBOUND_CLOSED = 32;
  
  private static final int STATE_CLOSE_NOTIFY = 64;
  
  private static final int STATE_PROCESS_TASK = 128;
  
  private static final int STATE_FIRE_CHANNEL_READ = 256;
  
  private static final int STATE_UNWRAP_REENTRY = 512;
  
  private static final int MAX_PLAINTEXT_LENGTH = 16384;
  
  private volatile ChannelHandlerContext ctx;
  
  private final SSLEngine engine;
  
  private final SslEngineType engineType;
  
  private final Executor delegatedTaskExecutor;
  
  private final boolean jdkCompatibilityMode;
  
  private enum SslEngineType {
    TCNATIVE(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR) {
      SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int len, ByteBuf out) throws SSLException {
        SSLEngineResult result;
        int nioBufferCount = in.nioBufferCount();
        int writerIndex = out.writerIndex();
        if (nioBufferCount > 1) {
          ReferenceCountedOpenSslEngine opensslEngine = (ReferenceCountedOpenSslEngine)handler.engine;
          try {
            handler.singleBuffer[0] = SslHandler.toByteBuffer(out, writerIndex, out.writableBytes());
            result = opensslEngine.unwrap(in.nioBuffers(in.readerIndex(), len), handler.singleBuffer);
          } finally {
            handler.singleBuffer[0] = null;
          } 
        } else {
          result = handler.engine.unwrap(SslHandler.toByteBuffer(in, in.readerIndex(), len), SslHandler
              .toByteBuffer(out, writerIndex, out.writableBytes()));
        } 
        out.writerIndex(writerIndex + result.bytesProduced());
        return result;
      }
      
      ByteBuf allocateWrapBuffer(SslHandler handler, ByteBufAllocator allocator, int pendingBytes, int numComponents) {
        return allocator.directBuffer(((ReferenceCountedOpenSslEngine)handler.engine)
            .calculateMaxLengthForWrap(pendingBytes, numComponents));
      }
      
      int calculatePendingData(SslHandler handler, int guess) {
        int sslPending = ((ReferenceCountedOpenSslEngine)handler.engine).sslPending();
        return (sslPending > 0) ? sslPending : guess;
      }
      
      boolean jdkCompatibilityMode(SSLEngine engine) {
        return ((ReferenceCountedOpenSslEngine)engine).jdkCompatibilityMode;
      }
    },
    CONSCRYPT(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR) {
      SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int len, ByteBuf out) throws SSLException {
        SSLEngineResult result;
        int nioBufferCount = in.nioBufferCount();
        int writerIndex = out.writerIndex();
        if (nioBufferCount > 1) {
          try {
            handler.singleBuffer[0] = SslHandler.toByteBuffer(out, writerIndex, out.writableBytes());
            result = ((ConscryptAlpnSslEngine)handler.engine).unwrap(in
                .nioBuffers(in.readerIndex(), len), handler
                .singleBuffer);
          } finally {
            handler.singleBuffer[0] = null;
          } 
        } else {
          result = handler.engine.unwrap(SslHandler.toByteBuffer(in, in.readerIndex(), len), SslHandler
              .toByteBuffer(out, writerIndex, out.writableBytes()));
        } 
        out.writerIndex(writerIndex + result.bytesProduced());
        return result;
      }
      
      ByteBuf allocateWrapBuffer(SslHandler handler, ByteBufAllocator allocator, int pendingBytes, int numComponents) {
        return allocator.directBuffer((
            (ConscryptAlpnSslEngine)handler.engine).calculateOutNetBufSize(pendingBytes, numComponents));
      }
      
      int calculatePendingData(SslHandler handler, int guess) {
        return guess;
      }
      
      boolean jdkCompatibilityMode(SSLEngine engine) {
        return true;
      }
    },
    JDK(false, ByteToMessageDecoder.MERGE_CUMULATOR) {
      SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int len, ByteBuf out) throws SSLException {
        int writerIndex = out.writerIndex();
        ByteBuffer inNioBuffer = SslHandler.toByteBuffer(in, in.readerIndex(), len);
        int position = inNioBuffer.position();
        SSLEngineResult result = handler.engine.unwrap(inNioBuffer, SslHandler
            .toByteBuffer(out, writerIndex, out.writableBytes()));
        out.writerIndex(writerIndex + result.bytesProduced());
        if (result.bytesConsumed() == 0) {
          int consumed = inNioBuffer.position() - position;
          if (consumed != result.bytesConsumed())
            return new SSLEngineResult(result
                .getStatus(), result.getHandshakeStatus(), consumed, result.bytesProduced()); 
        } 
        return result;
      }
      
      ByteBuf allocateWrapBuffer(SslHandler handler, ByteBufAllocator allocator, int pendingBytes, int numComponents) {
        return allocator.heapBuffer(handler.engine.getSession().getPacketBufferSize());
      }
      
      int calculatePendingData(SslHandler handler, int guess) {
        return guess;
      }
      
      boolean jdkCompatibilityMode(SSLEngine engine) {
        return true;
      }
    };
    
    final boolean wantsDirectBuffer;
    
    final ByteToMessageDecoder.Cumulator cumulator;
    
    static SslEngineType forEngine(SSLEngine engine) {
      return (engine instanceof ReferenceCountedOpenSslEngine) ? TCNATIVE : ((engine instanceof ConscryptAlpnSslEngine) ? CONSCRYPT : JDK);
    }
    
    SslEngineType(boolean wantsDirectBuffer, ByteToMessageDecoder.Cumulator cumulator) {
      this.wantsDirectBuffer = wantsDirectBuffer;
      this.cumulator = cumulator;
    }
    
    abstract SSLEngineResult unwrap(SslHandler param1SslHandler, ByteBuf param1ByteBuf1, int param1Int, ByteBuf param1ByteBuf2) throws SSLException;
    
    abstract int calculatePendingData(SslHandler param1SslHandler, int param1Int);
    
    abstract boolean jdkCompatibilityMode(SSLEngine param1SSLEngine);
    
    abstract ByteBuf allocateWrapBuffer(SslHandler param1SslHandler, ByteBufAllocator param1ByteBufAllocator, int param1Int1, int param1Int2);
  }
  
  private final ByteBuffer[] singleBuffer = new ByteBuffer[1];
  
  private final boolean startTls;
  
  private final SslTasksRunner sslTaskRunnerForUnwrap = new SslTasksRunner(true);
  
  private final SslTasksRunner sslTaskRunner = new SslTasksRunner(false);
  
  private SslHandlerCoalescingBufferQueue pendingUnencryptedWrites;
  
  private Promise<Channel> handshakePromise = (Promise<Channel>)new LazyChannelPromise();
  
  private final LazyChannelPromise sslClosePromise = new LazyChannelPromise();
  
  private int packetLength;
  
  private short state;
  
  private volatile long handshakeTimeoutMillis = 10000L;
  
  private volatile long closeNotifyFlushTimeoutMillis = 3000L;
  
  private volatile long closeNotifyReadTimeoutMillis;
  
  volatile int wrapDataSize = 16384;
  
  public SslHandler(SSLEngine engine) {
    this(engine, false);
  }
  
  public SslHandler(SSLEngine engine, boolean startTls) {
    this(engine, startTls, (Executor)ImmediateExecutor.INSTANCE);
  }
  
  public SslHandler(SSLEngine engine, Executor delegatedTaskExecutor) {
    this(engine, false, delegatedTaskExecutor);
  }
  
  public SslHandler(SSLEngine engine, boolean startTls, Executor delegatedTaskExecutor) {
    this.engine = (SSLEngine)ObjectUtil.checkNotNull(engine, "engine");
    this.delegatedTaskExecutor = (Executor)ObjectUtil.checkNotNull(delegatedTaskExecutor, "delegatedTaskExecutor");
    this.engineType = SslEngineType.forEngine(engine);
    this.startTls = startTls;
    this.jdkCompatibilityMode = this.engineType.jdkCompatibilityMode(engine);
    setCumulator(this.engineType.cumulator);
  }
  
  public long getHandshakeTimeoutMillis() {
    return this.handshakeTimeoutMillis;
  }
  
  public void setHandshakeTimeout(long handshakeTimeout, TimeUnit unit) {
    ObjectUtil.checkNotNull(unit, "unit");
    setHandshakeTimeoutMillis(unit.toMillis(handshakeTimeout));
  }
  
  public void setHandshakeTimeoutMillis(long handshakeTimeoutMillis) {
    this.handshakeTimeoutMillis = ObjectUtil.checkPositiveOrZero(handshakeTimeoutMillis, "handshakeTimeoutMillis");
  }
  
  public final void setWrapDataSize(int wrapDataSize) {
    this.wrapDataSize = wrapDataSize;
  }
  
  @Deprecated
  public long getCloseNotifyTimeoutMillis() {
    return getCloseNotifyFlushTimeoutMillis();
  }
  
  @Deprecated
  public void setCloseNotifyTimeout(long closeNotifyTimeout, TimeUnit unit) {
    setCloseNotifyFlushTimeout(closeNotifyTimeout, unit);
  }
  
  @Deprecated
  public void setCloseNotifyTimeoutMillis(long closeNotifyFlushTimeoutMillis) {
    setCloseNotifyFlushTimeoutMillis(closeNotifyFlushTimeoutMillis);
  }
  
  public final long getCloseNotifyFlushTimeoutMillis() {
    return this.closeNotifyFlushTimeoutMillis;
  }
  
  public final void setCloseNotifyFlushTimeout(long closeNotifyFlushTimeout, TimeUnit unit) {
    setCloseNotifyFlushTimeoutMillis(unit.toMillis(closeNotifyFlushTimeout));
  }
  
  public final void setCloseNotifyFlushTimeoutMillis(long closeNotifyFlushTimeoutMillis) {
    this.closeNotifyFlushTimeoutMillis = ObjectUtil.checkPositiveOrZero(closeNotifyFlushTimeoutMillis, "closeNotifyFlushTimeoutMillis");
  }
  
  public final long getCloseNotifyReadTimeoutMillis() {
    return this.closeNotifyReadTimeoutMillis;
  }
  
  public final void setCloseNotifyReadTimeout(long closeNotifyReadTimeout, TimeUnit unit) {
    setCloseNotifyReadTimeoutMillis(unit.toMillis(closeNotifyReadTimeout));
  }
  
  public final void setCloseNotifyReadTimeoutMillis(long closeNotifyReadTimeoutMillis) {
    this.closeNotifyReadTimeoutMillis = ObjectUtil.checkPositiveOrZero(closeNotifyReadTimeoutMillis, "closeNotifyReadTimeoutMillis");
  }
  
  public SSLEngine engine() {
    return this.engine;
  }
  
  public String applicationProtocol() {
    SSLEngine engine = engine();
    if (!(engine instanceof ApplicationProtocolAccessor))
      return null; 
    return ((ApplicationProtocolAccessor)engine).getNegotiatedApplicationProtocol();
  }
  
  public Future<Channel> handshakeFuture() {
    return (Future<Channel>)this.handshakePromise;
  }
  
  @Deprecated
  public ChannelFuture close() {
    return closeOutbound();
  }
  
  @Deprecated
  public ChannelFuture close(ChannelPromise promise) {
    return closeOutbound(promise);
  }
  
  public ChannelFuture closeOutbound() {
    return closeOutbound(this.ctx.newPromise());
  }
  
  public ChannelFuture closeOutbound(final ChannelPromise promise) {
    ChannelHandlerContext ctx = this.ctx;
    if (ctx.executor().inEventLoop()) {
      closeOutbound0(promise);
    } else {
      ctx.executor().execute(new Runnable() {
            public void run() {
              SslHandler.this.closeOutbound0(promise);
            }
          });
    } 
    return (ChannelFuture)promise;
  }
  
  private void closeOutbound0(ChannelPromise promise) {
    setState(32);
    this.engine.closeOutbound();
    try {
      flush(this.ctx, promise);
    } catch (Exception e) {
      if (!promise.tryFailure(e))
        logger.warn("{} flush() raised a masked exception.", this.ctx.channel(), e); 
    } 
  }
  
  public Future<Channel> sslCloseFuture() {
    return (Future<Channel>)this.sslClosePromise;
  }
  
  public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
    try {
      if (this.pendingUnencryptedWrites != null && !this.pendingUnencryptedWrites.isEmpty())
        this.pendingUnencryptedWrites.releaseAndFailAll((ChannelOutboundInvoker)ctx, (Throwable)new ChannelException("Pending write on removal of SslHandler")); 
      this.pendingUnencryptedWrites = null;
      SSLException cause = null;
      if (!this.handshakePromise.isDone()) {
        cause = new SSLHandshakeException("SslHandler removed before handshake completed");
        if (this.handshakePromise.tryFailure(cause))
          ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(cause)); 
      } 
      if (!this.sslClosePromise.isDone()) {
        if (cause == null)
          cause = new SSLException("SslHandler removed before SSLEngine was closed"); 
        notifyClosePromise(cause);
      } 
    } finally {
      ReferenceCountUtil.release(this.engine);
    } 
  }
  
  public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
    ctx.bind(localAddress, promise);
  }
  
  public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
    ctx.connect(remoteAddress, localAddress, promise);
  }
  
  public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
    ctx.deregister(promise);
  }
  
  public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
    closeOutboundAndChannel(ctx, promise, true);
  }
  
  public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
    closeOutboundAndChannel(ctx, promise, false);
  }
  
  public void read(ChannelHandlerContext ctx) throws Exception {
    if (!this.handshakePromise.isDone())
      setState(4); 
    ctx.read();
  }
  
  private static IllegalStateException newPendingWritesNullException() {
    return new IllegalStateException("pendingUnencryptedWrites is null, handlerRemoved0 called?");
  }
  
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    if (!(msg instanceof ByteBuf)) {
      UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException(msg, new Class[] { ByteBuf.class });
      ReferenceCountUtil.safeRelease(msg);
      promise.setFailure((Throwable)exception);
    } else if (this.pendingUnencryptedWrites == null) {
      ReferenceCountUtil.safeRelease(msg);
      promise.setFailure(newPendingWritesNullException());
    } else {
      this.pendingUnencryptedWrites.add((ByteBuf)msg, promise);
    } 
  }
  
  public void flush(ChannelHandlerContext ctx) throws Exception {
    if (this.startTls && !isStateSet(1)) {
      setState(1);
      this.pendingUnencryptedWrites.writeAndRemoveAll(ctx);
      forceFlush(ctx);
      startHandshakeProcessing(true);
      return;
    } 
    if (isStateSet(128))
      return; 
    try {
      wrapAndFlush(ctx);
    } catch (Throwable cause) {
      setHandshakeFailure(ctx, cause);
      PlatformDependent.throwException(cause);
    } 
  }
  
  private void wrapAndFlush(ChannelHandlerContext ctx) throws SSLException {
    if (this.pendingUnencryptedWrites.isEmpty())
      this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, ctx.newPromise()); 
    if (!this.handshakePromise.isDone())
      setState(2); 
    try {
      wrap(ctx, false);
    } finally {
      forceFlush(ctx);
    } 
  }
  
  private void wrap(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
    ByteBuf out = null;
    ByteBufAllocator alloc = ctx.alloc();
    try {
      int wrapDataSize = this.wrapDataSize;
      while (!ctx.isRemoved()) {
        ChannelPromise promise = ctx.newPromise();
        ByteBuf buf = (wrapDataSize > 0) ? this.pendingUnencryptedWrites.remove(alloc, wrapDataSize, promise) : this.pendingUnencryptedWrites.removeFirst(promise);
        if (buf == null)
          break; 
        if (out == null)
          out = allocateOutNetBuf(ctx, buf.readableBytes(), buf.nioBufferCount()); 
        SSLEngineResult result = wrap(alloc, this.engine, buf, out);
        if (buf.isReadable()) {
          this.pendingUnencryptedWrites.addFirst(buf, promise);
          promise = null;
        } else {
          buf.release();
        } 
        if (out.isReadable()) {
          ByteBuf b = out;
          out = null;
          if (promise != null) {
            ctx.write(b, promise);
          } else {
            ctx.write(b);
          } 
        } else if (promise != null) {
          ctx.write(Unpooled.EMPTY_BUFFER, promise);
        } 
        if (result.getStatus() == SSLEngineResult.Status.CLOSED) {
          Throwable exception = this.handshakePromise.cause();
          if (exception == null) {
            exception = this.sslClosePromise.cause();
            if (exception == null)
              exception = new SslClosedEngineException("SSLEngine closed already"); 
          } 
          this.pendingUnencryptedWrites.releaseAndFailAll((ChannelOutboundInvoker)ctx, exception);
          return;
        } 
        switch (result.getHandshakeStatus()) {
          case NEED_TASK:
            if (!runDelegatedTasks(inUnwrap))
              break; 
            continue;
          case FINISHED:
          case NOT_HANDSHAKING:
            setHandshakeSuccess();
            continue;
          case NEED_WRAP:
            if (result.bytesProduced() > 0 && this.pendingUnencryptedWrites.isEmpty())
              this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER); 
            continue;
          case NEED_UNWRAP:
            readIfNeeded(ctx);
            return;
        } 
        throw new IllegalStateException("Unknown handshake status: " + result
            .getHandshakeStatus());
      } 
    } finally {
      if (out != null)
        out.release(); 
      if (inUnwrap)
        setState(16); 
    } 
  }
  
  private boolean wrapNonAppData(final ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
    ByteBuf out = null;
    ByteBufAllocator alloc = ctx.alloc();
    try {
      while (!ctx.isRemoved()) {
        boolean bool;
        if (out == null)
          out = allocateOutNetBuf(ctx, 2048, 1); 
        SSLEngineResult result = wrap(alloc, this.engine, Unpooled.EMPTY_BUFFER, out);
        if (result.bytesProduced() > 0) {
          ctx.write(out).addListener((GenericFutureListener)new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                  Throwable cause = future.cause();
                  if (cause != null)
                    SslHandler.this.setHandshakeFailureTransportFailure(ctx, cause); 
                }
              });
          if (inUnwrap)
            setState(16); 
          out = null;
        } 
        SSLEngineResult.HandshakeStatus status = result.getHandshakeStatus();
        switch (status) {
          case FINISHED:
            if (setHandshakeSuccess() && inUnwrap && !this.pendingUnencryptedWrites.isEmpty())
              wrap(ctx, true); 
            bool = false;
            return bool;
          case NEED_TASK:
            if (!runDelegatedTasks(inUnwrap))
              break; 
            break;
          case NEED_UNWRAP:
            if (inUnwrap || unwrapNonAppData(ctx) <= 0) {
              bool = false;
              return bool;
            } 
            break;
          case NEED_WRAP:
            break;
          case NOT_HANDSHAKING:
            if (setHandshakeSuccess() && inUnwrap && !this.pendingUnencryptedWrites.isEmpty())
              wrap(ctx, true); 
            if (!inUnwrap)
              unwrapNonAppData(ctx); 
            bool = true;
            return bool;
          default:
            throw new IllegalStateException("Unknown handshake status: " + result.getHandshakeStatus());
        } 
        if (result.bytesProduced() == 0 && status != SSLEngineResult.HandshakeStatus.NEED_TASK)
          break; 
        if (result.bytesConsumed() == 0 && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING)
          break; 
      } 
    } finally {
      if (out != null)
        out.release(); 
    } 
    return false;
  }
  
  private SSLEngineResult wrap(ByteBufAllocator alloc, SSLEngine engine, ByteBuf in, ByteBuf out) throws SSLException {
    ByteBuf newDirectIn = null;
    try {
      ByteBuffer[] in0;
      SSLEngineResult result;
      int readerIndex = in.readerIndex();
      int readableBytes = in.readableBytes();
      if (in.isDirect() || !this.engineType.wantsDirectBuffer) {
        if (!(in instanceof CompositeByteBuf) && in.nioBufferCount() == 1) {
          in0 = this.singleBuffer;
          in0[0] = in.internalNioBuffer(readerIndex, readableBytes);
        } else {
          in0 = in.nioBuffers();
        } 
      } else {
        newDirectIn = alloc.directBuffer(readableBytes);
        newDirectIn.writeBytes(in, readerIndex, readableBytes);
        in0 = this.singleBuffer;
        in0[0] = newDirectIn.internalNioBuffer(newDirectIn.readerIndex(), readableBytes);
      } 
      while (true) {
        ByteBuffer out0 = out.nioBuffer(out.writerIndex(), out.writableBytes());
        result = engine.wrap(in0, out0);
        in.skipBytes(result.bytesConsumed());
        out.writerIndex(out.writerIndex() + result.bytesProduced());
        if (result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
          out.ensureWritable(engine.getSession().getPacketBufferSize());
          continue;
        } 
        break;
      } 
      return result;
    } finally {
      this.singleBuffer[0] = null;
      if (newDirectIn != null)
        newDirectIn.release(); 
    } 
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    boolean handshakeFailed = (this.handshakePromise.cause() != null);
    ClosedChannelException exception = new ClosedChannelException();
    if (isStateSet(8) && !this.handshakePromise.isDone())
      ThrowableUtil.addSuppressed(exception, StacklessSSLHandshakeException.newInstance("Connection closed while SSL/TLS handshake was in progress", SslHandler.class, "channelInactive")); 
    setHandshakeFailure(ctx, exception, !isStateSet(32), isStateSet(8), false);
    notifyClosePromise(exception);
    try {
      super.channelInactive(ctx);
    } catch (DecoderException e) {
      if (!handshakeFailed || !(e.getCause() instanceof SSLException))
        throw e; 
    } 
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (ignoreException(cause)) {
      if (logger.isDebugEnabled())
        logger.debug("{} Swallowing a harmless 'connection reset by peer / broken pipe' error that occurred while writing close_notify in response to the peer's close_notify", ctx
            
            .channel(), cause); 
      if (ctx.channel().isActive())
        ctx.close(); 
    } else {
      ctx.fireExceptionCaught(cause);
    } 
  }
  
  private boolean ignoreException(Throwable t) {
    if (!(t instanceof SSLException) && t instanceof java.io.IOException && this.sslClosePromise.isDone()) {
      String message = t.getMessage();
      if (message != null && IGNORABLE_ERROR_MESSAGE.matcher(message).matches())
        return true; 
      StackTraceElement[] elements = t.getStackTrace();
      for (StackTraceElement element : elements) {
        String classname = element.getClassName();
        String methodname = element.getMethodName();
        if (!classname.startsWith("io.netty."))
          if ("read".equals(methodname)) {
            if (IGNORABLE_CLASS_IN_STACK.matcher(classname).matches())
              return true; 
            try {
              Class<?> clazz = PlatformDependent.getClassLoader(getClass()).loadClass(classname);
              if (SocketChannel.class.isAssignableFrom(clazz) || DatagramChannel.class
                .isAssignableFrom(clazz))
                return true; 
              if (PlatformDependent.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel"
                .equals(clazz.getSuperclass().getName()))
                return true; 
            } catch (Throwable cause) {
              if (logger.isDebugEnabled())
                logger.debug("Unexpected exception while loading class {} classname {}", new Object[] { getClass(), classname, cause }); 
            } 
          }  
      } 
    } 
    return false;
  }
  
  public static boolean isEncrypted(ByteBuf buffer) {
    if (buffer.readableBytes() < 5)
      throw new IllegalArgumentException("buffer must have at least 5 readable bytes"); 
    return (SslUtils.getEncryptedPacketLength(buffer, buffer.readerIndex()) != -2);
  }
  
  private void decodeJdkCompatible(ChannelHandlerContext ctx, ByteBuf in) throws NotSslRecordException {
    int packetLength = this.packetLength;
    if (packetLength > 0) {
      if (in.readableBytes() < packetLength)
        return; 
    } else {
      int readableBytes = in.readableBytes();
      if (readableBytes < 5)
        return; 
      packetLength = SslUtils.getEncryptedPacketLength(in, in.readerIndex());
      if (packetLength == -2) {
        NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
        in.skipBytes(in.readableBytes());
        setHandshakeFailure(ctx, e);
        throw e;
      } 
      assert packetLength > 0;
      if (packetLength > readableBytes) {
        this.packetLength = packetLength;
        return;
      } 
    } 
    this.packetLength = 0;
    try {
      int bytesConsumed = unwrap(ctx, in, packetLength);
      assert bytesConsumed == packetLength || this.engine.isInboundDone() : "we feed the SSLEngine a packets worth of data: " + packetLength + " but it only consumed: " + bytesConsumed;
    } catch (Throwable cause) {
      handleUnwrapThrowable(ctx, cause);
    } 
  }
  
  private void decodeNonJdkCompatible(ChannelHandlerContext ctx, ByteBuf in) {
    try {
      unwrap(ctx, in, in.readableBytes());
    } catch (Throwable cause) {
      handleUnwrapThrowable(ctx, cause);
    } 
  }
  
  private void handleUnwrapThrowable(ChannelHandlerContext ctx, Throwable cause) {
    try {
      if (this.handshakePromise.tryFailure(cause))
        ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(cause)); 
      if (this.pendingUnencryptedWrites != null)
        wrapAndFlush(ctx); 
    } catch (SSLException ex) {
      logger.debug("SSLException during trying to call SSLEngine.wrap(...) because of an previous SSLException, ignoring...", ex);
    } finally {
      setHandshakeFailure(ctx, cause, true, false, true);
    } 
    PlatformDependent.throwException(cause);
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws SSLException {
    if (isStateSet(128))
      return; 
    if (this.jdkCompatibilityMode) {
      decodeJdkCompatible(ctx, in);
    } else {
      decodeNonJdkCompatible(ctx, in);
    } 
  }
  
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    channelReadComplete0(ctx);
  }
  
  private void channelReadComplete0(ChannelHandlerContext ctx) {
    discardSomeReadBytes();
    flushIfNeeded(ctx);
    readIfNeeded(ctx);
    clearState(256);
    ctx.fireChannelReadComplete();
  }
  
  private void readIfNeeded(ChannelHandlerContext ctx) {
    if (!ctx.channel().config().isAutoRead() && (
      !isStateSet(256) || !this.handshakePromise.isDone()))
      ctx.read(); 
  }
  
  private void flushIfNeeded(ChannelHandlerContext ctx) {
    if (isStateSet(16))
      forceFlush(ctx); 
  }
  
  private int unwrapNonAppData(ChannelHandlerContext ctx) throws SSLException {
    return unwrap(ctx, Unpooled.EMPTY_BUFFER, 0);
  }
  
  private int unwrap(ChannelHandlerContext ctx, ByteBuf packet, int length) throws SSLException {
    // Byte code:
    //   0: iload_3
    //   1: istore #4
    //   3: iconst_0
    //   4: istore #5
    //   6: iconst_0
    //   7: istore #6
    //   9: iconst_0
    //   10: istore #7
    //   12: aload_0
    //   13: aload_1
    //   14: iload_3
    //   15: invokespecial allocate : (Lio/netty/channel/ChannelHandlerContext;I)Lio/netty/buffer/ByteBuf;
    //   18: astore #8
    //   20: aload_0
    //   21: getfield engineType : Lio/netty/handler/ssl/SslHandler$SslEngineType;
    //   24: aload_0
    //   25: aload_2
    //   26: iload_3
    //   27: aload #8
    //   29: invokevirtual unwrap : (Lio/netty/handler/ssl/SslHandler;Lio/netty/buffer/ByteBuf;ILio/netty/buffer/ByteBuf;)Ljavax/net/ssl/SSLEngineResult;
    //   32: astore #9
    //   34: aload #9
    //   36: invokevirtual getStatus : ()Ljavax/net/ssl/SSLEngineResult$Status;
    //   39: astore #10
    //   41: aload #9
    //   43: invokevirtual getHandshakeStatus : ()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
    //   46: astore #11
    //   48: aload #9
    //   50: invokevirtual bytesProduced : ()I
    //   53: istore #12
    //   55: aload #9
    //   57: invokevirtual bytesConsumed : ()I
    //   60: istore #13
    //   62: aload_2
    //   63: iload #13
    //   65: invokevirtual skipBytes : (I)Lio/netty/buffer/ByteBuf;
    //   68: pop
    //   69: iload_3
    //   70: iload #13
    //   72: isub
    //   73: istore_3
    //   74: aload #11
    //   76: getstatic javax/net/ssl/SSLEngineResult$HandshakeStatus.FINISHED : Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
    //   79: if_acmpeq -> 90
    //   82: aload #11
    //   84: getstatic javax/net/ssl/SSLEngineResult$HandshakeStatus.NOT_HANDSHAKING : Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
    //   87: if_acmpne -> 133
    //   90: iload #5
    //   92: aload #8
    //   94: invokevirtual isReadable : ()Z
    //   97: ifeq -> 110
    //   100: aload_0
    //   101: invokespecial setHandshakeSuccessUnwrapMarkReentry : ()Z
    //   104: ifeq -> 117
    //   107: goto -> 125
    //   110: aload_0
    //   111: invokespecial setHandshakeSuccess : ()Z
    //   114: ifne -> 125
    //   117: aload #11
    //   119: getstatic javax/net/ssl/SSLEngineResult$HandshakeStatus.FINISHED : Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
    //   122: if_acmpne -> 129
    //   125: iconst_1
    //   126: goto -> 130
    //   129: iconst_0
    //   130: ior
    //   131: istore #5
    //   133: aload #8
    //   135: invokevirtual isReadable : ()Z
    //   138: ifeq -> 183
    //   141: aload_0
    //   142: sipush #256
    //   145: invokespecial setState : (I)V
    //   148: aload_0
    //   149: sipush #512
    //   152: invokespecial isStateSet : (I)Z
    //   155: ifeq -> 171
    //   158: iconst_1
    //   159: istore #7
    //   161: aload_0
    //   162: aload_1
    //   163: aload #8
    //   165: invokespecial executeChannelRead : (Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;)V
    //   168: goto -> 180
    //   171: aload_1
    //   172: aload #8
    //   174: invokeinterface fireChannelRead : (Ljava/lang/Object;)Lio/netty/channel/ChannelHandlerContext;
    //   179: pop
    //   180: aconst_null
    //   181: astore #8
    //   183: aload #10
    //   185: getstatic javax/net/ssl/SSLEngineResult$Status.CLOSED : Ljavax/net/ssl/SSLEngineResult$Status;
    //   188: if_acmpne -> 197
    //   191: iconst_1
    //   192: istore #6
    //   194: goto -> 265
    //   197: aload #10
    //   199: getstatic javax/net/ssl/SSLEngineResult$Status.BUFFER_OVERFLOW : Ljavax/net/ssl/SSLEngineResult$Status;
    //   202: if_acmpne -> 265
    //   205: aload #8
    //   207: ifnull -> 216
    //   210: aload #8
    //   212: invokevirtual release : ()Z
    //   215: pop
    //   216: aload_0
    //   217: getfield engine : Ljavax/net/ssl/SSLEngine;
    //   220: invokevirtual getSession : ()Ljavax/net/ssl/SSLSession;
    //   223: invokeinterface getApplicationBufferSize : ()I
    //   228: istore #14
    //   230: aload_0
    //   231: aload_1
    //   232: aload_0
    //   233: getfield engineType : Lio/netty/handler/ssl/SslHandler$SslEngineType;
    //   236: aload_0
    //   237: iload #14
    //   239: iload #12
    //   241: if_icmpge -> 249
    //   244: iload #14
    //   246: goto -> 254
    //   249: iload #14
    //   251: iload #12
    //   253: isub
    //   254: invokevirtual calculatePendingData : (Lio/netty/handler/ssl/SslHandler;I)I
    //   257: invokespecial allocate : (Lio/netty/channel/ChannelHandlerContext;I)Lio/netty/buffer/ByteBuf;
    //   260: astore #8
    //   262: goto -> 385
    //   265: aload #11
    //   267: getstatic javax/net/ssl/SSLEngineResult$HandshakeStatus.NEED_TASK : Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
    //   270: if_acmpne -> 294
    //   273: aload_0
    //   274: iconst_1
    //   275: invokespecial runDelegatedTasks : (Z)Z
    //   278: istore #14
    //   280: iload #14
    //   282: ifne -> 291
    //   285: iconst_0
    //   286: istore #5
    //   288: goto -> 394
    //   291: goto -> 318
    //   294: aload #11
    //   296: getstatic javax/net/ssl/SSLEngineResult$HandshakeStatus.NEED_WRAP : Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
    //   299: if_acmpne -> 318
    //   302: aload_0
    //   303: aload_1
    //   304: iconst_1
    //   305: invokespecial wrapNonAppData : (Lio/netty/channel/ChannelHandlerContext;Z)Z
    //   308: ifeq -> 318
    //   311: iload_3
    //   312: ifne -> 318
    //   315: goto -> 394
    //   318: aload #10
    //   320: getstatic javax/net/ssl/SSLEngineResult$Status.BUFFER_UNDERFLOW : Ljavax/net/ssl/SSLEngineResult$Status;
    //   323: if_acmpeq -> 356
    //   326: aload #11
    //   328: getstatic javax/net/ssl/SSLEngineResult$HandshakeStatus.NEED_TASK : Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
    //   331: if_acmpeq -> 372
    //   334: iload #13
    //   336: ifne -> 344
    //   339: iload #12
    //   341: ifeq -> 356
    //   344: iload_3
    //   345: ifne -> 372
    //   348: aload #11
    //   350: getstatic javax/net/ssl/SSLEngineResult$HandshakeStatus.NOT_HANDSHAKING : Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
    //   353: if_acmpne -> 372
    //   356: aload #11
    //   358: getstatic javax/net/ssl/SSLEngineResult$HandshakeStatus.NEED_UNWRAP : Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
    //   361: if_acmpne -> 394
    //   364: aload_0
    //   365: aload_1
    //   366: invokespecial readIfNeeded : (Lio/netty/channel/ChannelHandlerContext;)V
    //   369: goto -> 394
    //   372: aload #8
    //   374: ifnonnull -> 385
    //   377: aload_0
    //   378: aload_1
    //   379: iload_3
    //   380: invokespecial allocate : (Lio/netty/channel/ChannelHandlerContext;I)Lio/netty/buffer/ByteBuf;
    //   383: astore #8
    //   385: aload_1
    //   386: invokeinterface isRemoved : ()Z
    //   391: ifeq -> 20
    //   394: aload_0
    //   395: iconst_2
    //   396: invokespecial isStateSet : (I)Z
    //   399: ifeq -> 422
    //   402: aload_0
    //   403: getfield handshakePromise : Lio/netty/util/concurrent/Promise;
    //   406: invokeinterface isDone : ()Z
    //   411: ifeq -> 422
    //   414: aload_0
    //   415: iconst_2
    //   416: invokespecial clearState : (I)V
    //   419: iconst_1
    //   420: istore #5
    //   422: iload #5
    //   424: ifeq -> 433
    //   427: aload_0
    //   428: aload_1
    //   429: iconst_1
    //   430: invokespecial wrap : (Lio/netty/channel/ChannelHandlerContext;Z)V
    //   433: aload #8
    //   435: ifnull -> 444
    //   438: aload #8
    //   440: invokevirtual release : ()Z
    //   443: pop
    //   444: iload #6
    //   446: ifeq -> 509
    //   449: iload #7
    //   451: ifeq -> 462
    //   454: aload_0
    //   455: aload_1
    //   456: invokespecial executeNotifyClosePromise : (Lio/netty/channel/ChannelHandlerContext;)V
    //   459: goto -> 509
    //   462: aload_0
    //   463: aconst_null
    //   464: invokespecial notifyClosePromise : (Ljava/lang/Throwable;)V
    //   467: goto -> 509
    //   470: astore #15
    //   472: aload #8
    //   474: ifnull -> 483
    //   477: aload #8
    //   479: invokevirtual release : ()Z
    //   482: pop
    //   483: iload #6
    //   485: ifeq -> 506
    //   488: iload #7
    //   490: ifeq -> 501
    //   493: aload_0
    //   494: aload_1
    //   495: invokespecial executeNotifyClosePromise : (Lio/netty/channel/ChannelHandlerContext;)V
    //   498: goto -> 506
    //   501: aload_0
    //   502: aconst_null
    //   503: invokespecial notifyClosePromise : (Ljava/lang/Throwable;)V
    //   506: aload #15
    //   508: athrow
    //   509: iload #4
    //   511: iload_3
    //   512: isub
    //   513: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1344	-> 0
    //   #1345	-> 3
    //   #1346	-> 6
    //   #1347	-> 9
    //   #1348	-> 12
    //   #1353	-> 20
    //   #1354	-> 34
    //   #1355	-> 41
    //   #1356	-> 48
    //   #1357	-> 55
    //   #1362	-> 62
    //   #1363	-> 69
    //   #1368	-> 74
    //   #1369	-> 90
    //   #1370	-> 101
    //   #1377	-> 133
    //   #1378	-> 141
    //   #1379	-> 148
    //   #1380	-> 158
    //   #1381	-> 161
    //   #1383	-> 171
    //   #1385	-> 180
    //   #1388	-> 183
    //   #1389	-> 191
    //   #1390	-> 197
    //   #1391	-> 205
    //   #1392	-> 210
    //   #1394	-> 216
    //   #1399	-> 230
    //   #1401	-> 262
    //   #1404	-> 265
    //   #1405	-> 273
    //   #1406	-> 280
    //   #1412	-> 285
    //   #1413	-> 288
    //   #1415	-> 291
    //   #1419	-> 302
    //   #1420	-> 315
    //   #1424	-> 318
    //   #1428	-> 356
    //   #1431	-> 364
    //   #1435	-> 372
    //   #1436	-> 377
    //   #1438	-> 385
    //   #1440	-> 394
    //   #1445	-> 414
    //   #1446	-> 419
    //   #1449	-> 422
    //   #1450	-> 427
    //   #1453	-> 433
    //   #1454	-> 438
    //   #1457	-> 444
    //   #1458	-> 449
    //   #1459	-> 454
    //   #1461	-> 462
    //   #1453	-> 470
    //   #1454	-> 477
    //   #1457	-> 483
    //   #1458	-> 488
    //   #1459	-> 493
    //   #1461	-> 501
    //   #1464	-> 506
    //   #1465	-> 509
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   230	35	14	applicationBufferSize	I
    //   280	11	14	pending	Z
    //   34	351	9	result	Ljavax/net/ssl/SSLEngineResult;
    //   41	344	10	status	Ljavax/net/ssl/SSLEngineResult$Status;
    //   48	337	11	handshakeStatus	Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
    //   55	330	12	produced	I
    //   62	323	13	consumed	I
    //   0	514	0	this	Lio/netty/handler/ssl/SslHandler;
    //   0	514	1	ctx	Lio/netty/channel/ChannelHandlerContext;
    //   0	514	2	packet	Lio/netty/buffer/ByteBuf;
    //   0	514	3	length	I
    //   3	511	4	originalLength	I
    //   6	508	5	wrapLater	Z
    //   9	505	6	notifyClosure	Z
    //   12	502	7	executedRead	Z
    //   20	494	8	decodeOut	Lio/netty/buffer/ByteBuf;
    // Exception table:
    //   from	to	target	type
    //   20	433	470	finally
    //   470	472	470	finally
  }
  
  private boolean setHandshakeSuccessUnwrapMarkReentry() {
    boolean setReentryState = !isStateSet(512);
    if (setReentryState)
      setState(512); 
    try {
      return setHandshakeSuccess();
    } finally {
      if (setReentryState)
        clearState(512); 
    } 
  }
  
  private void executeNotifyClosePromise(ChannelHandlerContext ctx) {
    try {
      ctx.executor().execute(new Runnable() {
            public void run() {
              SslHandler.this.notifyClosePromise((Throwable)null);
            }
          });
    } catch (RejectedExecutionException e) {
      notifyClosePromise(e);
    } 
  }
  
  private void executeChannelRead(final ChannelHandlerContext ctx, final ByteBuf decodedOut) {
    try {
      ctx.executor().execute(new Runnable() {
            public void run() {
              ctx.fireChannelRead(decodedOut);
            }
          });
    } catch (RejectedExecutionException e) {
      decodedOut.release();
      throw e;
    } 
  }
  
  private static ByteBuffer toByteBuffer(ByteBuf out, int index, int len) {
    return (out.nioBufferCount() == 1) ? out.internalNioBuffer(index, len) : out
      .nioBuffer(index, len);
  }
  
  private static boolean inEventLoop(Executor executor) {
    return (executor instanceof EventExecutor && ((EventExecutor)executor).inEventLoop());
  }
  
  private boolean runDelegatedTasks(boolean inUnwrap) {
    if (this.delegatedTaskExecutor == ImmediateExecutor.INSTANCE || inEventLoop(this.delegatedTaskExecutor))
      while (true) {
        Runnable task = this.engine.getDelegatedTask();
        if (task == null)
          return true; 
        setState(128);
        if (task instanceof AsyncRunnable) {
          boolean pending = false;
          try {
            AsyncRunnable asyncTask = (AsyncRunnable)task;
            AsyncTaskCompletionHandler completionHandler = new AsyncTaskCompletionHandler(inUnwrap);
            asyncTask.run(completionHandler);
            pending = completionHandler.resumeLater();
            if (pending)
              return false; 
          } finally {
            if (!pending)
              clearState(128); 
          } 
          continue;
        } 
        try {
          task.run();
        } finally {
          clearState(128);
        } 
      }  
    executeDelegatedTask(inUnwrap);
    return false;
  }
  
  private SslTasksRunner getTaskRunner(boolean inUnwrap) {
    return inUnwrap ? this.sslTaskRunnerForUnwrap : this.sslTaskRunner;
  }
  
  private void executeDelegatedTask(boolean inUnwrap) {
    executeDelegatedTask(getTaskRunner(inUnwrap));
  }
  
  private void executeDelegatedTask(SslTasksRunner task) {
    setState(128);
    try {
      this.delegatedTaskExecutor.execute(task);
    } catch (RejectedExecutionException e) {
      clearState(128);
      throw e;
    } 
  }
  
  private final class AsyncTaskCompletionHandler implements Runnable {
    private final boolean inUnwrap;
    
    boolean didRun;
    
    boolean resumeLater;
    
    AsyncTaskCompletionHandler(boolean inUnwrap) {
      this.inUnwrap = inUnwrap;
    }
    
    public void run() {
      this.didRun = true;
      if (this.resumeLater)
        SslHandler.this.getTaskRunner(this.inUnwrap).runComplete(); 
    }
    
    boolean resumeLater() {
      if (!this.didRun) {
        this.resumeLater = true;
        return true;
      } 
      return false;
    }
  }
  
  private final class SslTasksRunner implements Runnable {
    private final boolean inUnwrap;
    
    private final Runnable runCompleteTask = new Runnable() {
        public void run() {
          SslHandler.SslTasksRunner.this.runComplete();
        }
      };
    
    SslTasksRunner(boolean inUnwrap) {
      this.inUnwrap = inUnwrap;
    }
    
    private void taskError(Throwable e) {
      if (this.inUnwrap) {
        try {
          SslHandler.this.handleUnwrapThrowable(SslHandler.this.ctx, e);
        } catch (Throwable cause) {
          safeExceptionCaught(cause);
        } 
      } else {
        SslHandler.this.setHandshakeFailure(SslHandler.this.ctx, e);
        SslHandler.this.forceFlush(SslHandler.this.ctx);
      } 
    }
    
    private void safeExceptionCaught(Throwable cause) {
      try {
        SslHandler.this.exceptionCaught(SslHandler.this.ctx, wrapIfNeeded(cause));
      } catch (Throwable error) {
        SslHandler.this.ctx.fireExceptionCaught(error);
      } 
    }
    
    private Throwable wrapIfNeeded(Throwable cause) {
      if (!this.inUnwrap)
        return cause; 
      return (cause instanceof DecoderException) ? cause : (Throwable)new DecoderException(cause);
    }
    
    private void tryDecodeAgain() {
      try {
        SslHandler.this.channelRead(SslHandler.this.ctx, Unpooled.EMPTY_BUFFER);
      } catch (Throwable cause) {
        safeExceptionCaught(cause);
      } finally {
        SslHandler.this.channelReadComplete0(SslHandler.this.ctx);
      } 
    }
    
    private void resumeOnEventExecutor() {
      assert SslHandler.this.ctx.executor().inEventLoop();
      SslHandler.this.clearState(128);
      try {
        SSLEngineResult.HandshakeStatus status = SslHandler.this.engine.getHandshakeStatus();
        switch (status) {
          case NEED_TASK:
            SslHandler.this.executeDelegatedTask(this);
            return;
          case FINISHED:
          case NOT_HANDSHAKING:
            SslHandler.this.setHandshakeSuccess();
            try {
              SslHandler.this.wrap(SslHandler.this.ctx, this.inUnwrap);
            } catch (Throwable e) {
              taskError(e);
              return;
            } 
            if (this.inUnwrap)
              SslHandler.this.unwrapNonAppData(SslHandler.this.ctx); 
            SslHandler.this.forceFlush(SslHandler.this.ctx);
            tryDecodeAgain();
            return;
          case NEED_UNWRAP:
            try {
              SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
            } catch (SSLException e) {
              SslHandler.this.handleUnwrapThrowable(SslHandler.this.ctx, e);
              return;
            } 
            tryDecodeAgain();
            return;
          case NEED_WRAP:
            try {
              if (!SslHandler.this.wrapNonAppData(SslHandler.this.ctx, false) && this.inUnwrap)
                SslHandler.this.unwrapNonAppData(SslHandler.this.ctx); 
              SslHandler.this.forceFlush(SslHandler.this.ctx);
            } catch (Throwable e) {
              taskError(e);
              return;
            } 
            tryDecodeAgain();
            return;
        } 
        throw new AssertionError();
      } catch (Throwable cause) {
        safeExceptionCaught(cause);
      } 
    }
    
    void runComplete() {
      EventExecutor executor = SslHandler.this.ctx.executor();
      executor.execute(new Runnable() {
            public void run() {
              SslHandler.SslTasksRunner.this.resumeOnEventExecutor();
            }
          });
    }
    
    public void run() {
      try {
        Runnable task = SslHandler.this.engine.getDelegatedTask();
        if (task == null)
          return; 
        if (task instanceof AsyncRunnable) {
          AsyncRunnable asyncTask = (AsyncRunnable)task;
          asyncTask.run(this.runCompleteTask);
        } else {
          task.run();
          runComplete();
        } 
      } catch (Throwable cause) {
        handleException(cause);
      } 
    }
    
    private void handleException(final Throwable cause) {
      EventExecutor executor = SslHandler.this.ctx.executor();
      if (executor.inEventLoop()) {
        SslHandler.this.clearState(128);
        safeExceptionCaught(cause);
      } else {
        try {
          executor.execute(new Runnable() {
                public void run() {
                  SslHandler.this.clearState(128);
                  SslHandler.SslTasksRunner.this.safeExceptionCaught(cause);
                }
              });
        } catch (RejectedExecutionException ignore) {
          SslHandler.this.clearState(128);
          SslHandler.this.ctx.fireExceptionCaught(cause);
        } 
      } 
    }
  }
  
  private boolean setHandshakeSuccess() {
    boolean notified;
    if (notified = (!this.handshakePromise.isDone() && this.handshakePromise.trySuccess(this.ctx.channel()))) {
      if (logger.isDebugEnabled()) {
        SSLSession session = this.engine.getSession();
        logger.debug("{} HANDSHAKEN: protocol:{} cipher suite:{}", new Object[] { this.ctx
              
              .channel(), session
              .getProtocol(), session
              .getCipherSuite() });
      } 
      this.ctx.fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
    } 
    if (isStateSet(4)) {
      clearState(4);
      if (!this.ctx.channel().config().isAutoRead())
        this.ctx.read(); 
    } 
    return notified;
  }
  
  private void setHandshakeFailure(ChannelHandlerContext ctx, Throwable cause) {
    setHandshakeFailure(ctx, cause, true, true, false);
  }
  
  private void setHandshakeFailure(ChannelHandlerContext ctx, Throwable cause, boolean closeInbound, boolean notify, boolean alwaysFlushAndClose) {
    try {
      setState(32);
      this.engine.closeOutbound();
      if (closeInbound)
        try {
          this.engine.closeInbound();
        } catch (SSLException e) {
          if (logger.isDebugEnabled()) {
            String msg = e.getMessage();
            if (msg == null || (!msg.contains("possible truncation attack") && 
              !msg.contains("closing inbound before receiving peer's close_notify")))
              logger.debug("{} SSLEngine.closeInbound() raised an exception.", ctx.channel(), e); 
          } 
        }  
      if (this.handshakePromise.tryFailure(cause) || alwaysFlushAndClose)
        SslUtils.handleHandshakeFailure(ctx, cause, notify); 
    } finally {
      releaseAndFailAll(ctx, cause);
    } 
  }
  
  private void setHandshakeFailureTransportFailure(ChannelHandlerContext ctx, Throwable cause) {
    try {
      SSLException transportFailure = new SSLException("failure when writing TLS control frames", cause);
      releaseAndFailAll(ctx, transportFailure);
      if (this.handshakePromise.tryFailure(transportFailure))
        ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(transportFailure)); 
    } finally {
      ctx.close();
    } 
  }
  
  private void releaseAndFailAll(ChannelHandlerContext ctx, Throwable cause) {
    if (this.pendingUnencryptedWrites != null)
      this.pendingUnencryptedWrites.releaseAndFailAll((ChannelOutboundInvoker)ctx, cause); 
  }
  
  private void notifyClosePromise(Throwable cause) {
    if (cause == null) {
      if (this.sslClosePromise.trySuccess(this.ctx.channel()))
        this.ctx.fireUserEventTriggered(SslCloseCompletionEvent.SUCCESS); 
    } else if (this.sslClosePromise.tryFailure(cause)) {
      this.ctx.fireUserEventTriggered(new SslCloseCompletionEvent(cause));
    } 
  }
  
  private void closeOutboundAndChannel(ChannelHandlerContext ctx, final ChannelPromise promise, boolean disconnect) throws Exception {
    setState(32);
    this.engine.closeOutbound();
    if (!ctx.channel().isActive()) {
      if (disconnect) {
        ctx.disconnect(promise);
      } else {
        ctx.close(promise);
      } 
      return;
    } 
    ChannelPromise closeNotifyPromise = ctx.newPromise();
    try {
      flush(ctx, closeNotifyPromise);
    } finally {
      if (!isStateSet(64)) {
        setState(64);
        safeClose(ctx, (ChannelFuture)closeNotifyPromise, (ChannelPromise)PromiseNotifier.cascade(false, (Future)ctx.newPromise(), (Promise)promise));
      } else {
        this.sslClosePromise.addListener((GenericFutureListener)new FutureListener<Channel>() {
              public void operationComplete(Future<Channel> future) {
                promise.setSuccess();
              }
            });
      } 
    } 
  }
  
  private void flush(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
    if (this.pendingUnencryptedWrites != null) {
      this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, promise);
    } else {
      promise.setFailure(newPendingWritesNullException());
    } 
    flush(ctx);
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    this.ctx = ctx;
    Channel channel = ctx.channel();
    this.pendingUnencryptedWrites = new SslHandlerCoalescingBufferQueue(channel, 16);
    setOpensslEngineSocketFd(channel);
    boolean fastOpen = Boolean.TRUE.equals(channel.config().getOption(ChannelOption.TCP_FASTOPEN_CONNECT));
    boolean active = channel.isActive();
    if (active || fastOpen) {
      startHandshakeProcessing(active);
      ChannelOutboundBuffer outboundBuffer;
      if (fastOpen && ((outboundBuffer = channel.unsafe().outboundBuffer()) == null || outboundBuffer
        .totalPendingWriteBytes() > 0L))
        setState(16); 
    } 
  }
  
  private void startHandshakeProcessing(boolean flushAtEnd) {
    if (!isStateSet(8)) {
      setState(8);
      if (this.engine.getUseClientMode())
        handshake(flushAtEnd); 
      applyHandshakeTimeout();
    } else if (isStateSet(16)) {
      forceFlush(this.ctx);
    } 
  }
  
  public Future<Channel> renegotiate() {
    ChannelHandlerContext ctx = this.ctx;
    if (ctx == null)
      throw new IllegalStateException(); 
    return renegotiate(ctx.executor().newPromise());
  }
  
  public Future<Channel> renegotiate(final Promise<Channel> promise) {
    ObjectUtil.checkNotNull(promise, "promise");
    ChannelHandlerContext ctx = this.ctx;
    if (ctx == null)
      throw new IllegalStateException(); 
    EventExecutor executor = ctx.executor();
    if (!executor.inEventLoop()) {
      executor.execute(new Runnable() {
            public void run() {
              SslHandler.this.renegotiateOnEventLoop(promise);
            }
          });
      return (Future<Channel>)promise;
    } 
    renegotiateOnEventLoop(promise);
    return (Future<Channel>)promise;
  }
  
  private void renegotiateOnEventLoop(Promise<Channel> newHandshakePromise) {
    Promise<Channel> oldHandshakePromise = this.handshakePromise;
    if (!oldHandshakePromise.isDone()) {
      PromiseNotifier.cascade((Future)oldHandshakePromise, newHandshakePromise);
    } else {
      this.handshakePromise = newHandshakePromise;
      handshake(true);
      applyHandshakeTimeout();
    } 
  }
  
  private void handshake(boolean flushAtEnd) {
    if (this.engine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING)
      return; 
    if (this.handshakePromise.isDone())
      return; 
    ChannelHandlerContext ctx = this.ctx;
    try {
      this.engine.beginHandshake();
      wrapNonAppData(ctx, false);
    } catch (Throwable e) {
      setHandshakeFailure(ctx, e);
    } finally {
      if (flushAtEnd)
        forceFlush(ctx); 
    } 
  }
  
  private void applyHandshakeTimeout() {
    final Promise<Channel> localHandshakePromise = this.handshakePromise;
    final long handshakeTimeoutMillis = this.handshakeTimeoutMillis;
    if (handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone())
      return; 
    final ScheduledFuture timeoutFuture = this.ctx.executor().schedule(new Runnable() {
          public void run() {
            if (localHandshakePromise.isDone())
              return; 
            SSLException exception = new SslHandshakeTimeoutException("handshake timed out after " + handshakeTimeoutMillis + "ms");
            try {
              if (localHandshakePromise.tryFailure(exception))
                SslUtils.handleHandshakeFailure(SslHandler.this.ctx, exception, true); 
            } finally {
              SslHandler.this.releaseAndFailAll(SslHandler.this.ctx, exception);
            } 
          }
        }handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
    localHandshakePromise.addListener((GenericFutureListener)new FutureListener<Channel>() {
          public void operationComplete(Future<Channel> f) throws Exception {
            timeoutFuture.cancel(false);
          }
        });
  }
  
  private void forceFlush(ChannelHandlerContext ctx) {
    clearState(16);
    ctx.flush();
  }
  
  private void setOpensslEngineSocketFd(Channel c) {
    if (c instanceof UnixChannel && this.engine instanceof ReferenceCountedOpenSslEngine)
      ((ReferenceCountedOpenSslEngine)this.engine).bioSetFd(((UnixChannel)c).fd().intValue()); 
  }
  
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    setOpensslEngineSocketFd(ctx.channel());
    if (!this.startTls)
      startHandshakeProcessing(true); 
    ctx.fireChannelActive();
  }
  
  private void safeClose(final ChannelHandlerContext ctx, final ChannelFuture flushFuture, final ChannelPromise promise) {
    final Future<?> timeoutFuture;
    if (!ctx.channel().isActive()) {
      ctx.close(promise);
      return;
    } 
    if (!flushFuture.isDone()) {
      long closeNotifyTimeout = this.closeNotifyFlushTimeoutMillis;
      if (closeNotifyTimeout > 0L) {
        ScheduledFuture scheduledFuture = ctx.executor().schedule(new Runnable() {
              public void run() {
                if (!flushFuture.isDone()) {
                  SslHandler.logger.warn("{} Last write attempt timed out; force-closing the connection.", ctx
                      .channel());
                  SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                } 
              }
            }closeNotifyTimeout, TimeUnit.MILLISECONDS);
      } else {
        timeoutFuture = null;
      } 
    } else {
      timeoutFuture = null;
    } 
    flushFuture.addListener((GenericFutureListener)new ChannelFutureListener() {
          public void operationComplete(ChannelFuture f) {
            if (timeoutFuture != null)
              timeoutFuture.cancel(false); 
            final long closeNotifyReadTimeout = SslHandler.this.closeNotifyReadTimeoutMillis;
            if (closeNotifyReadTimeout <= 0L) {
              SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
            } else {
              final Future<?> closeNotifyReadTimeoutFuture;
              if (!SslHandler.this.sslClosePromise.isDone()) {
                ScheduledFuture scheduledFuture = ctx.executor().schedule(new Runnable() {
                      public void run() {
                        if (!SslHandler.this.sslClosePromise.isDone()) {
                          SslHandler.logger.debug("{} did not receive close_notify in {}ms; force-closing the connection.", ctx
                              
                              .channel(), Long.valueOf(closeNotifyReadTimeout));
                          SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                        } 
                      }
                    }closeNotifyReadTimeout, TimeUnit.MILLISECONDS);
              } else {
                closeNotifyReadTimeoutFuture = null;
              } 
              SslHandler.this.sslClosePromise.addListener((GenericFutureListener)new FutureListener<Channel>() {
                    public void operationComplete(Future<Channel> future) throws Exception {
                      if (closeNotifyReadTimeoutFuture != null)
                        closeNotifyReadTimeoutFuture.cancel(false); 
                      SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                    }
                  });
            } 
          }
        });
  }
  
  private static void addCloseListener(ChannelFuture future, ChannelPromise promise) {
    PromiseNotifier.cascade(false, (Future)future, (Promise)promise);
  }
  
  private ByteBuf allocate(ChannelHandlerContext ctx, int capacity) {
    ByteBufAllocator alloc = ctx.alloc();
    if (this.engineType.wantsDirectBuffer)
      return alloc.directBuffer(capacity); 
    return alloc.buffer(capacity);
  }
  
  private ByteBuf allocateOutNetBuf(ChannelHandlerContext ctx, int pendingBytes, int numComponents) {
    return this.engineType.allocateWrapBuffer(this, ctx.alloc(), pendingBytes, numComponents);
  }
  
  private boolean isStateSet(int bit) {
    return ((this.state & bit) == bit);
  }
  
  private void setState(int bit) {
    this.state = (short)(this.state | bit);
  }
  
  private void clearState(int bit) {
    this.state = (short)(this.state & (bit ^ 0xFFFFFFFF));
  }
  
  private final class SslHandlerCoalescingBufferQueue extends AbstractCoalescingBufferQueue {
    SslHandlerCoalescingBufferQueue(Channel channel, int initSize) {
      super(channel, initSize);
    }
    
    protected ByteBuf compose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
      int wrapDataSize = SslHandler.this.wrapDataSize;
      if (cumulation instanceof CompositeByteBuf) {
        CompositeByteBuf composite = (CompositeByteBuf)cumulation;
        int numComponents = composite.numComponents();
        if (numComponents == 0 || 
          !SslHandler.attemptCopyToCumulation(composite.internalComponent(numComponents - 1), next, wrapDataSize))
          composite.addComponent(true, next); 
        return (ByteBuf)composite;
      } 
      return SslHandler.attemptCopyToCumulation(cumulation, next, wrapDataSize) ? cumulation : 
        copyAndCompose(alloc, cumulation, next);
    }
    
    protected ByteBuf composeFirst(ByteBufAllocator allocator, ByteBuf first) {
      if (first instanceof CompositeByteBuf) {
        CompositeByteBuf composite = (CompositeByteBuf)first;
        if (SslHandler.this.engineType.wantsDirectBuffer) {
          first = allocator.directBuffer(composite.readableBytes());
        } else {
          first = allocator.heapBuffer(composite.readableBytes());
        } 
        try {
          first.writeBytes((ByteBuf)composite);
        } catch (Throwable cause) {
          first.release();
          PlatformDependent.throwException(cause);
        } 
        composite.release();
      } 
      return first;
    }
    
    protected ByteBuf removeEmptyValue() {
      return null;
    }
  }
  
  private static boolean attemptCopyToCumulation(ByteBuf cumulation, ByteBuf next, int wrapDataSize) {
    int inReadableBytes = next.readableBytes();
    int cumulationCapacity = cumulation.capacity();
    if (wrapDataSize - cumulation.readableBytes() >= inReadableBytes && ((cumulation
      
      .isWritable(inReadableBytes) && cumulationCapacity >= wrapDataSize) || (cumulationCapacity < wrapDataSize && 
      
      ByteBufUtil.ensureWritableSuccess(cumulation.ensureWritable(inReadableBytes, false))))) {
      cumulation.writeBytes(next);
      next.release();
      return true;
    } 
    return false;
  }
  
  private final class LazyChannelPromise extends DefaultPromise<Channel> {
    private LazyChannelPromise() {}
    
    protected EventExecutor executor() {
      if (SslHandler.this.ctx == null)
        throw new IllegalStateException(); 
      return SslHandler.this.ctx.executor();
    }
    
    protected void checkDeadLock() {
      if (SslHandler.this.ctx == null)
        return; 
      super.checkDeadLock();
    }
  }
}
