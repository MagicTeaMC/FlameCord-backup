package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.SegmentedDatagramPacket;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.UnresolvedAddressException;

public final class EpollDatagramChannel extends AbstractEpollChannel implements DatagramChannel {
  private static final ChannelMetadata METADATA = new ChannelMetadata(true);
  
  private static final String EXPECTED_TYPES = " (expected: " + 
    StringUtil.simpleClassName(DatagramPacket.class) + ", " + 
    StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + 
    StringUtil.simpleClassName(ByteBuf.class) + ", " + 
    StringUtil.simpleClassName(InetSocketAddress.class) + ">, " + 
    StringUtil.simpleClassName(ByteBuf.class) + ')';
  
  private final EpollDatagramChannelConfig config;
  
  private volatile boolean connected;
  
  public static boolean isSegmentedDatagramPacketSupported() {
    return (Epoll.isAvailable() && Native.IS_SUPPORTING_SENDMMSG && Native.IS_SUPPORTING_UDP_SEGMENT);
  }
  
  public EpollDatagramChannel() {
    this((InternetProtocolFamily)null);
  }
  
  public EpollDatagramChannel(InternetProtocolFamily family) {
    this(LinuxSocket.newSocketDgram(family), false);
  }
  
  public EpollDatagramChannel(int fd) {
    this(new LinuxSocket(fd), true);
  }
  
  private EpollDatagramChannel(LinuxSocket fd, boolean active) {
    super((Channel)null, fd, active);
    this.config = new EpollDatagramChannelConfig(this);
  }
  
  public InetSocketAddress remoteAddress() {
    return (InetSocketAddress)super.remoteAddress();
  }
  
  public InetSocketAddress localAddress() {
    return (InetSocketAddress)super.localAddress();
  }
  
  public ChannelMetadata metadata() {
    return METADATA;
  }
  
  public boolean isActive() {
    return (this.socket.isOpen() && ((this.config.getActiveOnOpen() && isRegistered()) || this.active));
  }
  
  public boolean isConnected() {
    return this.connected;
  }
  
  public ChannelFuture joinGroup(InetAddress multicastAddress) {
    return joinGroup(multicastAddress, newPromise());
  }
  
  public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
    try {
      NetworkInterface iface = config().getNetworkInterface();
      if (iface == null)
        iface = NetworkInterface.getByInetAddress(localAddress().getAddress()); 
      return joinGroup(multicastAddress, iface, (InetAddress)null, promise);
    } catch (IOException e) {
      promise.setFailure(e);
      return (ChannelFuture)promise;
    } 
  }
  
  public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
    return joinGroup(multicastAddress, networkInterface, newPromise());
  }
  
  public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
    return joinGroup(multicastAddress.getAddress(), networkInterface, (InetAddress)null, promise);
  }
  
  public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
    return joinGroup(multicastAddress, networkInterface, source, newPromise());
  }
  
  public ChannelFuture joinGroup(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source, final ChannelPromise promise) {
    ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
    ObjectUtil.checkNotNull(networkInterface, "networkInterface");
    if (eventLoop().inEventLoop()) {
      joinGroup0(multicastAddress, networkInterface, source, promise);
    } else {
      eventLoop().execute(new Runnable() {
            public void run() {
              EpollDatagramChannel.this.joinGroup0(multicastAddress, networkInterface, source, promise);
            }
          });
    } 
    return (ChannelFuture)promise;
  }
  
  private void joinGroup0(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
    assert eventLoop().inEventLoop();
    try {
      this.socket.joinGroup(multicastAddress, networkInterface, source);
      promise.setSuccess();
    } catch (IOException e) {
      promise.setFailure(e);
    } 
  }
  
  public ChannelFuture leaveGroup(InetAddress multicastAddress) {
    return leaveGroup(multicastAddress, newPromise());
  }
  
  public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
    try {
      return leaveGroup(multicastAddress, 
          NetworkInterface.getByInetAddress(localAddress().getAddress()), (InetAddress)null, promise);
    } catch (IOException e) {
      promise.setFailure(e);
      return (ChannelFuture)promise;
    } 
  }
  
  public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
    return leaveGroup(multicastAddress, networkInterface, newPromise());
  }
  
  public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
    return leaveGroup(multicastAddress.getAddress(), networkInterface, (InetAddress)null, promise);
  }
  
  public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
    return leaveGroup(multicastAddress, networkInterface, source, newPromise());
  }
  
  public ChannelFuture leaveGroup(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source, final ChannelPromise promise) {
    ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
    ObjectUtil.checkNotNull(networkInterface, "networkInterface");
    if (eventLoop().inEventLoop()) {
      leaveGroup0(multicastAddress, networkInterface, source, promise);
    } else {
      eventLoop().execute(new Runnable() {
            public void run() {
              EpollDatagramChannel.this.leaveGroup0(multicastAddress, networkInterface, source, promise);
            }
          });
    } 
    return (ChannelFuture)promise;
  }
  
  private void leaveGroup0(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
    assert eventLoop().inEventLoop();
    try {
      this.socket.leaveGroup(multicastAddress, networkInterface, source);
      promise.setSuccess();
    } catch (IOException e) {
      promise.setFailure(e);
    } 
  }
  
  public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
    return block(multicastAddress, networkInterface, sourceToBlock, newPromise());
  }
  
  public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
    ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
    ObjectUtil.checkNotNull(sourceToBlock, "sourceToBlock");
    ObjectUtil.checkNotNull(networkInterface, "networkInterface");
    promise.setFailure(new UnsupportedOperationException("Multicast block not supported"));
    return (ChannelFuture)promise;
  }
  
  public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
    return block(multicastAddress, sourceToBlock, newPromise());
  }
  
  public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
    try {
      return block(multicastAddress, 
          
          NetworkInterface.getByInetAddress(localAddress().getAddress()), sourceToBlock, promise);
    } catch (Throwable e) {
      promise.setFailure(e);
      return (ChannelFuture)promise;
    } 
  }
  
  protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
    return new EpollDatagramChannelUnsafe();
  }
  
  protected void doBind(SocketAddress localAddress) throws Exception {
    if (localAddress instanceof InetSocketAddress) {
      InetSocketAddress socketAddress = (InetSocketAddress)localAddress;
      if (socketAddress.getAddress().isAnyLocalAddress() && socketAddress
        .getAddress() instanceof java.net.Inet4Address && 
        this.socket.family() == InternetProtocolFamily.IPv6)
        localAddress = new InetSocketAddress(LinuxSocket.INET6_ANY, socketAddress.getPort()); 
    } 
    super.doBind(localAddress);
    this.active = true;
  }
  
  protected void doWrite(ChannelOutboundBuffer in) throws Exception {
    int maxMessagesPerWrite = maxMessagesPerWrite();
    while (maxMessagesPerWrite > 0) {
      Object msg = in.current();
      if (msg == null)
        break; 
      try {
        if ((Native.IS_SUPPORTING_SENDMMSG && in.size() > 1) || in
          
          .current() instanceof SegmentedDatagramPacket) {
          NativeDatagramPacketArray array = cleanDatagramPacketArray();
          array.add(in, isConnected(), maxMessagesPerWrite);
          int cnt = array.count();
          if (cnt >= 1) {
            int offset = 0;
            NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
            int send = this.socket.sendmmsg(packets, offset, cnt);
            if (send == 0)
              break; 
            for (int j = 0; j < send; j++)
              in.remove(); 
            maxMessagesPerWrite -= send;
            continue;
          } 
        } 
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
    InetSocketAddress remoteAddress;
    if (msg instanceof AddressedEnvelope) {
      AddressedEnvelope<ByteBuf, InetSocketAddress> envelope = (AddressedEnvelope<ByteBuf, InetSocketAddress>)msg;
      data = (ByteBuf)envelope.content();
      remoteAddress = (InetSocketAddress)envelope.recipient();
    } else {
      data = (ByteBuf)msg;
      remoteAddress = null;
    } 
    int dataLen = data.readableBytes();
    if (dataLen == 0)
      return true; 
    return (doWriteOrSendBytes(data, remoteAddress, false) > 0L);
  }
  
  private static void checkUnresolved(AddressedEnvelope<?, ?> envelope) {
    if (envelope.recipient() instanceof InetSocketAddress && ((InetSocketAddress)envelope
      .recipient()).isUnresolved())
      throw new UnresolvedAddressException(); 
  }
  
  protected Object filterOutboundMessage(Object msg) {
    if (msg instanceof SegmentedDatagramPacket) {
      if (!Native.IS_SUPPORTING_UDP_SEGMENT)
        throw new UnsupportedOperationException("unsupported message type: " + 
            StringUtil.simpleClassName(msg) + EXPECTED_TYPES); 
      SegmentedDatagramPacket packet = (SegmentedDatagramPacket)msg;
      checkUnresolved((AddressedEnvelope<?, ?>)packet);
      ByteBuf content = (ByteBuf)packet.content();
      return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? packet
        .replace(newDirectBuffer(packet, content)) : msg;
    } 
    if (msg instanceof DatagramPacket) {
      DatagramPacket packet = (DatagramPacket)msg;
      checkUnresolved((AddressedEnvelope<?, ?>)packet);
      ByteBuf content = (ByteBuf)packet.content();
      return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DatagramPacket(
          newDirectBuffer(packet, content), (InetSocketAddress)packet.recipient()) : msg;
    } 
    if (msg instanceof ByteBuf) {
      ByteBuf buf = (ByteBuf)msg;
      return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? newDirectBuffer(buf) : buf;
    } 
    if (msg instanceof AddressedEnvelope) {
      AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope<Object, SocketAddress>)msg;
      checkUnresolved(e);
      if (e.content() instanceof ByteBuf && (e
        .recipient() == null || e.recipient() instanceof InetSocketAddress)) {
        ByteBuf content = (ByteBuf)e.content();
        return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DefaultAddressedEnvelope(
            
            newDirectBuffer(e, content), e.recipient()) : e;
      } 
    } 
    throw new UnsupportedOperationException("unsupported message type: " + 
        StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
  }
  
  public EpollDatagramChannelConfig config() {
    return this.config;
  }
  
  protected void doDisconnect() throws Exception {
    this.socket.disconnect();
    this.connected = this.active = false;
    resetCachedAddresses();
  }
  
  protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
    if (super.doConnect(remoteAddress, localAddress)) {
      this.connected = true;
      return true;
    } 
    return false;
  }
  
  protected void doClose() throws Exception {
    super.doClose();
    this.connected = false;
  }
  
  final class EpollDatagramChannelUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
    void epollInReady() {
      assert EpollDatagramChannel.this.eventLoop().inEventLoop();
      EpollDatagramChannelConfig config = EpollDatagramChannel.this.config();
      if (EpollDatagramChannel.this.shouldBreakEpollInReady((ChannelConfig)config)) {
        clearEpollIn0();
        return;
      } 
      EpollRecvByteAllocatorHandle allocHandle = recvBufAllocHandle();
      allocHandle.edgeTriggered(EpollDatagramChannel.this.isFlagSet(Native.EPOLLET));
      ChannelPipeline pipeline = EpollDatagramChannel.this.pipeline();
      ByteBufAllocator allocator = config.getAllocator();
      allocHandle.reset((ChannelConfig)config);
      epollInBefore();
      Throwable exception = null;
      try {
        try {
          boolean connected = EpollDatagramChannel.this.isConnected();
          while (true) {
            boolean read;
            int datagramSize = EpollDatagramChannel.this.config().getMaxDatagramPayloadSize();
            ByteBuf byteBuf = allocHandle.allocate(allocator);
            int numDatagram = Native.IS_SUPPORTING_RECVMMSG ? ((datagramSize == 0) ? 1 : (byteBuf.writableBytes() / datagramSize)) : 0;
            try {
              if (numDatagram <= 1) {
                if (!connected || config.isUdpGro()) {
                  read = EpollDatagramChannel.this.recvmsg(allocHandle, EpollDatagramChannel.this.cleanDatagramPacketArray(), byteBuf);
                } else {
                  read = EpollDatagramChannel.this.connectedRead(allocHandle, byteBuf, datagramSize);
                } 
              } else {
                read = EpollDatagramChannel.this.scatteringRead(allocHandle, EpollDatagramChannel.this.cleanDatagramPacketArray(), byteBuf, datagramSize, numDatagram);
              } 
            } catch (io.netty.channel.unix.Errors.NativeIoException e) {
              if (connected)
                throw EpollDatagramChannel.this.translateForConnected(e); 
              throw e;
            } 
            if (read) {
              this.readPending = false;
              if (!allocHandle.continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER))
                break; 
              continue;
            } 
            break;
          } 
        } catch (Throwable t) {
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
  
  private boolean connectedRead(EpollRecvByteAllocatorHandle allocHandle, ByteBuf byteBuf, int maxDatagramPacketSize) throws Exception {
    try {
      int localReadAmount, writable = (maxDatagramPacketSize != 0) ? Math.min(byteBuf.writableBytes(), maxDatagramPacketSize) : byteBuf.writableBytes();
      allocHandle.attemptedBytesRead(writable);
      int writerIndex = byteBuf.writerIndex();
      if (byteBuf.hasMemoryAddress()) {
        localReadAmount = this.socket.recvAddress(byteBuf.memoryAddress(), writerIndex, writerIndex + writable);
      } else {
        ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, writable);
        localReadAmount = this.socket.recv(buf, buf.position(), buf.limit());
      } 
      if (localReadAmount <= 0) {
        allocHandle.lastBytesRead(localReadAmount);
        return false;
      } 
      byteBuf.writerIndex(writerIndex + localReadAmount);
      allocHandle.lastBytesRead((maxDatagramPacketSize <= 0) ? localReadAmount : writable);
      DatagramPacket packet = new DatagramPacket(byteBuf, localAddress(), remoteAddress());
      allocHandle.incMessagesRead(1);
      pipeline().fireChannelRead(packet);
      byteBuf = null;
      return true;
    } finally {
      if (byteBuf != null)
        byteBuf.release(); 
    } 
  }
  
  private IOException translateForConnected(Errors.NativeIoException e) {
    if (e.expectedErr() == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
      PortUnreachableException error = new PortUnreachableException(e.getMessage());
      error.initCause((Throwable)e);
      return error;
    } 
    return (IOException)e;
  }
  
  private static void addDatagramPacketToOut(DatagramPacket packet, RecyclableArrayList out) {
    if (packet instanceof SegmentedDatagramPacket) {
      SegmentedDatagramPacket segmentedDatagramPacket = (SegmentedDatagramPacket)packet;
      ByteBuf content = (ByteBuf)segmentedDatagramPacket.content();
      InetSocketAddress recipient = (InetSocketAddress)segmentedDatagramPacket.recipient();
      InetSocketAddress sender = (InetSocketAddress)segmentedDatagramPacket.sender();
      int segmentSize = segmentedDatagramPacket.segmentSize();
      do {
        out.add(new DatagramPacket(content.readRetainedSlice(Math.min(content.readableBytes(), segmentSize)), recipient, sender));
      } while (content.isReadable());
      segmentedDatagramPacket.release();
    } else {
      out.add(packet);
    } 
  }
  
  private static void releaseAndRecycle(ByteBuf byteBuf, RecyclableArrayList packetList) {
    if (byteBuf != null)
      byteBuf.release(); 
    if (packetList != null) {
      for (int i = 0; i < packetList.size(); i++)
        ReferenceCountUtil.release(packetList.get(i)); 
      packetList.recycle();
    } 
  }
  
  private static void processPacket(ChannelPipeline pipeline, EpollRecvByteAllocatorHandle handle, int bytesRead, DatagramPacket packet) {
    handle.lastBytesRead(Math.max(1, bytesRead));
    handle.incMessagesRead(1);
    pipeline.fireChannelRead(packet);
  }
  
  private static void processPacketList(ChannelPipeline pipeline, EpollRecvByteAllocatorHandle handle, int bytesRead, RecyclableArrayList packetList) {
    int messagesRead = packetList.size();
    handle.lastBytesRead(Math.max(1, bytesRead));
    handle.incMessagesRead(messagesRead);
    for (int i = 0; i < messagesRead; i++)
      pipeline.fireChannelRead(packetList.set(i, Unpooled.EMPTY_BUFFER)); 
  }
  
  private boolean recvmsg(EpollRecvByteAllocatorHandle allocHandle, NativeDatagramPacketArray array, ByteBuf byteBuf) throws IOException {
    RecyclableArrayList datagramPackets = null;
    try {
      int writable = byteBuf.writableBytes();
      boolean added = array.addWritable(byteBuf, byteBuf.writerIndex(), writable);
      assert added;
      allocHandle.attemptedBytesRead(writable);
      NativeDatagramPacketArray.NativeDatagramPacket msg = array.packets()[0];
      int bytesReceived = this.socket.recvmsg(msg);
      if (!msg.hasSender()) {
        allocHandle.lastBytesRead(-1);
        return false;
      } 
      byteBuf.writerIndex(bytesReceived);
      InetSocketAddress local = localAddress();
      DatagramPacket packet = msg.newDatagramPacket(byteBuf, local);
      if (!(packet instanceof SegmentedDatagramPacket)) {
        processPacket(pipeline(), allocHandle, bytesReceived, packet);
      } else {
        datagramPackets = RecyclableArrayList.newInstance();
        addDatagramPacketToOut(packet, datagramPackets);
        processPacketList(pipeline(), allocHandle, bytesReceived, datagramPackets);
        datagramPackets.recycle();
        datagramPackets = null;
      } 
      return true;
    } finally {
      releaseAndRecycle(byteBuf, datagramPackets);
    } 
  }
  
  private boolean scatteringRead(EpollRecvByteAllocatorHandle allocHandle, NativeDatagramPacketArray array, ByteBuf byteBuf, int datagramSize, int numDatagram) throws IOException {
    RecyclableArrayList datagramPackets = null;
    try {
      int offset = byteBuf.writerIndex();
      for (int i = 0; i < numDatagram && 
        array.addWritable(byteBuf, offset, datagramSize); ) {
        i++;
        offset += datagramSize;
      } 
      allocHandle.attemptedBytesRead(offset - byteBuf.writerIndex());
      NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
      int received = this.socket.recvmmsg(packets, 0, array.count());
      if (received == 0) {
        allocHandle.lastBytesRead(-1);
        return false;
      } 
      InetSocketAddress local = localAddress();
      int bytesReceived = received * datagramSize;
      byteBuf.writerIndex(byteBuf.writerIndex() + bytesReceived);
      if (received == 1) {
        DatagramPacket packet = packets[0].newDatagramPacket(byteBuf, local);
        if (!(packet instanceof SegmentedDatagramPacket)) {
          processPacket(pipeline(), allocHandle, datagramSize, packet);
          return true;
        } 
      } 
      datagramPackets = RecyclableArrayList.newInstance();
      int j;
      for (j = 0; j < received; j++) {
        DatagramPacket packet = packets[j].newDatagramPacket(byteBuf, local);
        byteBuf.skipBytes(datagramSize);
        addDatagramPacketToOut(packet, datagramPackets);
      } 
      byteBuf.release();
      byteBuf = null;
      processPacketList(pipeline(), allocHandle, bytesReceived, datagramPackets);
      datagramPackets.recycle();
      datagramPackets = null;
      j = 1;
      return j;
    } finally {
      releaseAndRecycle(byteBuf, datagramPackets);
    } 
  }
  
  private NativeDatagramPacketArray cleanDatagramPacketArray() {
    return ((EpollEventLoop)eventLoop()).cleanDatagramPacketArray();
  }
}
