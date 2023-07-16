package io.netty.channel.local;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractServerChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannelRecvByteBufAllocator;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;

public class LocalServerChannel extends AbstractServerChannel {
  private final ChannelConfig config = (ChannelConfig)new DefaultChannelConfig((Channel)this, (RecvByteBufAllocator)new ServerChannelRecvByteBufAllocator()) {
    
    };
  
  private final Queue<Object> inboundBuffer = new ArrayDeque();
  
  private final Runnable shutdownHook = new Runnable() {
      public void run() {
        LocalServerChannel.this.unsafe().close(LocalServerChannel.this.unsafe().voidPromise());
      }
    };
  
  private volatile int state;
  
  private volatile LocalAddress localAddress;
  
  private volatile boolean acceptInProgress;
  
  public LocalServerChannel() {
    config().setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator(this.config.getAllocator()));
  }
  
  public ChannelConfig config() {
    return this.config;
  }
  
  public LocalAddress localAddress() {
    return (LocalAddress)super.localAddress();
  }
  
  public LocalAddress remoteAddress() {
    return (LocalAddress)super.remoteAddress();
  }
  
  public boolean isOpen() {
    return (this.state < 2);
  }
  
  public boolean isActive() {
    return (this.state == 1);
  }
  
  protected boolean isCompatible(EventLoop loop) {
    return loop instanceof io.netty.channel.SingleThreadEventLoop;
  }
  
  protected SocketAddress localAddress0() {
    return this.localAddress;
  }
  
  protected void doRegister() throws Exception {
    ((SingleThreadEventExecutor)eventLoop()).addShutdownHook(this.shutdownHook);
  }
  
  protected void doBind(SocketAddress localAddress) throws Exception {
    this.localAddress = LocalChannelRegistry.register((Channel)this, this.localAddress, localAddress);
    this.state = 1;
  }
  
  protected void doClose() throws Exception {
    if (this.state <= 1) {
      if (this.localAddress != null) {
        LocalChannelRegistry.unregister(this.localAddress);
        this.localAddress = null;
      } 
      this.state = 2;
    } 
  }
  
  protected void doDeregister() throws Exception {
    ((SingleThreadEventExecutor)eventLoop()).removeShutdownHook(this.shutdownHook);
  }
  
  protected void doBeginRead() throws Exception {
    if (this.acceptInProgress)
      return; 
    Queue<Object> inboundBuffer = this.inboundBuffer;
    if (inboundBuffer.isEmpty()) {
      this.acceptInProgress = true;
      return;
    } 
    readInbound();
  }
  
  LocalChannel serve(LocalChannel peer) {
    final LocalChannel child = newLocalChannel(peer);
    if (eventLoop().inEventLoop()) {
      serve0(child);
    } else {
      eventLoop().execute(new Runnable() {
            public void run() {
              LocalServerChannel.this.serve0(child);
            }
          });
    } 
    return child;
  }
  
  private void readInbound() {
    RecvByteBufAllocator.Handle handle = unsafe().recvBufAllocHandle();
    handle.reset(config());
    ChannelPipeline pipeline = pipeline();
    do {
      Object m = this.inboundBuffer.poll();
      if (m == null)
        break; 
      pipeline.fireChannelRead(m);
    } while (handle.continueReading());
    handle.readComplete();
    pipeline.fireChannelReadComplete();
  }
  
  protected LocalChannel newLocalChannel(LocalChannel peer) {
    return new LocalChannel(this, peer);
  }
  
  private void serve0(LocalChannel child) {
    this.inboundBuffer.add(child);
    if (this.acceptInProgress) {
      this.acceptInProgress = false;
      readInbound();
    } 
  }
}
