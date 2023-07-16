package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.unix.DomainDatagramChannel;
import io.netty.channel.unix.DomainDatagramChannelConfig;
import io.netty.channel.unix.DomainDatagramPacket;
import io.netty.channel.unix.DomainDatagramSocketAddress;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public final class EpollDomainDatagramChannel extends AbstractEpollChannel implements DomainDatagramChannel {
  private static final ChannelMetadata METADATA = new ChannelMetadata(true);
  
  private static final String EXPECTED_TYPES = " (expected: " + 
    
    StringUtil.simpleClassName(DomainDatagramPacket.class) + ", " + 
    StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + 
    StringUtil.simpleClassName(ByteBuf.class) + ", " + 
    StringUtil.simpleClassName(DomainSocketAddress.class) + ">, " + 
    StringUtil.simpleClassName(ByteBuf.class) + ')';
  
  private volatile boolean connected;
  
  private volatile DomainSocketAddress local;
  
  private volatile DomainSocketAddress remote;
  
  private final EpollDomainDatagramChannelConfig config;
  
  public EpollDomainDatagramChannel() {
    this(LinuxSocket.newSocketDomainDgram(), false);
  }
  
  public EpollDomainDatagramChannel(int fd) {
    this(new LinuxSocket(fd), true);
  }
  
  private EpollDomainDatagramChannel(LinuxSocket socket, boolean active) {
    super((Channel)null, socket, active);
    this.config = new EpollDomainDatagramChannelConfig(this);
  }
  
  public EpollDomainDatagramChannelConfig config() {
    return this.config;
  }
  
  protected void doBind(SocketAddress localAddress) throws Exception {
    super.doBind(localAddress);
    this.local = (DomainSocketAddress)localAddress;
    this.active = true;
  }
  
  protected void doClose() throws Exception {
    super.doClose();
    this.connected = this.active = false;
    this.local = null;
    this.remote = null;
  }
  
  protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
    if (super.doConnect(remoteAddress, localAddress)) {
      if (localAddress != null)
        this.local = (DomainSocketAddress)localAddress; 
      this.remote = (DomainSocketAddress)remoteAddress;
      this.connected = true;
      return true;
    } 
    return false;
  }
  
  protected void doDisconnect() throws Exception {
    doClose();
  }
  
  protected void doWrite(ChannelOutboundBuffer in) throws Exception {
    int maxMessagesPerWrite = maxMessagesPerWrite();
    while (maxMessagesPerWrite > 0) {
      Object msg = in.current();
      if (msg == null)
        break; 
      try {
        boolean done = false;
        for (int i = config().getWriteSpinCount(); i > 0; i--) {
          if (doWriteMessage(msg)) {
            done = true;
            break;
          } 
        } 
        if (done) {
          in.remove();
          maxMessagesPerWrite--;
          continue;
        } 
        break;
      } catch (IOException e) {
        maxMessagesPerWrite--;
        in.remove(e);
      } 
    } 
    if (in.isEmpty()) {
      clearFlag(Native.EPOLLOUT);
    } else {
      setFlag(Native.EPOLLOUT);
    } 
  }
  
  private boolean doWriteMessage(Object msg) throws Exception {
    ByteBuf data;
    DomainSocketAddress remoteAddress;
    long writtenBytes;
    if (msg instanceof AddressedEnvelope) {
      AddressedEnvelope<ByteBuf, DomainSocketAddress> envelope = (AddressedEnvelope<ByteBuf, DomainSocketAddress>)msg;
      data = (ByteBuf)envelope.content();
      remoteAddress = (DomainSocketAddress)envelope.recipient();
    } else {
      data = (ByteBuf)msg;
      remoteAddress = null;
    } 
    int dataLen = data.readableBytes();
    if (dataLen == 0)
      return true; 
    if (data.hasMemoryAddress()) {
      long memoryAddress = data.memoryAddress();
      if (remoteAddress == null) {
        writtenBytes = this.socket.sendAddress(memoryAddress, data.readerIndex(), data.writerIndex());
      } else {
        writtenBytes = this.socket.sendToAddressDomainSocket(memoryAddress, data.readerIndex(), data.writerIndex(), remoteAddress
            .path().getBytes(CharsetUtil.UTF_8));
      } 
    } else if (data.nioBufferCount() > 1) {
      IovArray array = ((EpollEventLoop)eventLoop()).cleanIovArray();
      array.add(data, data.readerIndex(), data.readableBytes());
      int cnt = array.count();
      assert cnt != 0;
      if (remoteAddress == null) {
        writtenBytes = this.socket.writevAddresses(array.memoryAddress(0), cnt);
      } else {
        writtenBytes = this.socket.sendToAddressesDomainSocket(array.memoryAddress(0), cnt, remoteAddress
            .path().getBytes(CharsetUtil.UTF_8));
      } 
    } else {
      ByteBuffer nioData = data.internalNioBuffer(data.readerIndex(), data.readableBytes());
      if (remoteAddress == null) {
        writtenBytes = this.socket.send(nioData, nioData.position(), nioData.limit());
      } else {
        writtenBytes = this.socket.sendToDomainSocket(nioData, nioData.position(), nioData.limit(), remoteAddress
            .path().getBytes(CharsetUtil.UTF_8));
      } 
    } 
    return (writtenBytes > 0L);
  }
  
  protected Object filterOutboundMessage(Object msg) {
    if (msg instanceof DomainDatagramPacket) {
      DomainDatagramPacket packet = (DomainDatagramPacket)msg;
      ByteBuf content = (ByteBuf)packet.content();
      return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DomainDatagramPacket(
          newDirectBuffer(packet, content), (DomainSocketAddress)packet.recipient()) : msg;
    } 
    if (msg instanceof ByteBuf) {
      ByteBuf buf = (ByteBuf)msg;
      return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? newDirectBuffer(buf) : buf;
    } 
    if (msg instanceof AddressedEnvelope) {
      AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope<Object, SocketAddress>)msg;
      if (e.content() instanceof ByteBuf && (e
        .recipient() == null || e.recipient() instanceof DomainSocketAddress)) {
        ByteBuf content = (ByteBuf)e.content();
        return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DefaultAddressedEnvelope(
            
            newDirectBuffer(e, content), e.recipient()) : e;
      } 
    } 
    throw new UnsupportedOperationException("unsupported message type: " + 
        StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
  }
  
  public boolean isActive() {
    return (this.socket.isOpen() && ((this.config.getActiveOnOpen() && isRegistered()) || this.active));
  }
  
  public boolean isConnected() {
    return this.connected;
  }
  
  public DomainSocketAddress localAddress() {
    return (DomainSocketAddress)super.localAddress();
  }
  
  protected DomainSocketAddress localAddress0() {
    return this.local;
  }
  
  public ChannelMetadata metadata() {
    return METADATA;
  }
  
  protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
    return new EpollDomainDatagramChannelUnsafe();
  }
  
  public PeerCredentials peerCredentials() throws IOException {
    return this.socket.getPeerCredentials();
  }
  
  public DomainSocketAddress remoteAddress() {
    return (DomainSocketAddress)super.remoteAddress();
  }
  
  protected DomainSocketAddress remoteAddress0() {
    return this.remote;
  }
  
  final class EpollDomainDatagramChannelUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
    void epollInReady() {
      assert EpollDomainDatagramChannel.this.eventLoop().inEventLoop();
      DomainDatagramChannelConfig config = EpollDomainDatagramChannel.this.config();
      if (EpollDomainDatagramChannel.this.shouldBreakEpollInReady((ChannelConfig)config)) {
        clearEpollIn0();
        return;
      } 
      EpollRecvByteAllocatorHandle allocHandle = recvBufAllocHandle();
      allocHandle.edgeTriggered(EpollDomainDatagramChannel.this.isFlagSet(Native.EPOLLET));
      ChannelPipeline pipeline = EpollDomainDatagramChannel.this.pipeline();
      ByteBufAllocator allocator = config.getAllocator();
      allocHandle.reset((ChannelConfig)config);
      epollInBefore();
      Throwable exception = null;
      try {
        ByteBuf byteBuf = null;
        try {
          boolean connected = EpollDomainDatagramChannel.this.isConnected();
          do {
            DomainDatagramPacket packet;
            byteBuf = allocHandle.allocate(allocator);
            allocHandle.attemptedBytesRead(byteBuf.writableBytes());
            if (connected) {
              allocHandle.lastBytesRead(EpollDomainDatagramChannel.this.doReadBytes(byteBuf));
              if (allocHandle.lastBytesRead() <= 0) {
                byteBuf.release();
                break;
              } 
              packet = new DomainDatagramPacket(byteBuf, (DomainSocketAddress)localAddress(), (DomainSocketAddress)remoteAddress());
            } else {
              DomainDatagramSocketAddress remoteAddress;
              DomainSocketAddress domainSocketAddress;
              if (byteBuf.hasMemoryAddress()) {
                remoteAddress = EpollDomainDatagramChannel.this.socket.recvFromAddressDomainSocket(byteBuf.memoryAddress(), byteBuf
                    .writerIndex(), byteBuf.capacity());
              } else {
                ByteBuffer nioData = byteBuf.internalNioBuffer(byteBuf
                    .writerIndex(), byteBuf.writableBytes());
                remoteAddress = EpollDomainDatagramChannel.this.socket.recvFromDomainSocket(nioData, nioData.position(), nioData.limit());
              } 
              if (remoteAddress == null) {
                allocHandle.lastBytesRead(-1);
                byteBuf.release();
                break;
              } 
              DomainDatagramSocketAddress domainDatagramSocketAddress1 = remoteAddress.localAddress();
              if (domainDatagramSocketAddress1 == null)
                domainSocketAddress = (DomainSocketAddress)localAddress(); 
              allocHandle.lastBytesRead(remoteAddress.receivedAmount());
              byteBuf.writerIndex(byteBuf.writerIndex() + allocHandle.lastBytesRead());
              packet = new DomainDatagramPacket(byteBuf, domainSocketAddress, (DomainSocketAddress)remoteAddress);
            } 
            allocHandle.incMessagesRead(1);
            this.readPending = false;
            pipeline.fireChannelRead(packet);
            byteBuf = null;
          } while (allocHandle.continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER));
        } catch (Throwable t) {
          if (byteBuf != null)
            byteBuf.release(); 
          exception = t;
        } 
        allocHandle.readComplete();
        pipeline.fireChannelReadComplete();
        if (exception != null)
          pipeline.fireExceptionCaught(exception); 
      } finally {
        epollInFinally((ChannelConfig)config);
      } 
    }
  }
}