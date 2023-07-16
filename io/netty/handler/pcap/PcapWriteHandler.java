package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public final class PcapWriteHandler extends ChannelDuplexHandler implements Closeable {
  private final InternalLogger logger = InternalLoggerFactory.getInstance(PcapWriteHandler.class);
  
  private PcapWriter pCapWriter;
  
  private final OutputStream outputStream;
  
  private final boolean captureZeroByte;
  
  private final boolean writePcapGlobalHeader;
  
  private final boolean sharedOutputStream;
  
  private int sendSegmentNumber = 1;
  
  private int receiveSegmentNumber = 1;
  
  private ChannelType channelType;
  
  private InetSocketAddress initiatorAddr;
  
  private InetSocketAddress handlerAddr;
  
  private boolean isServerPipeline;
  
  private final AtomicReference<State> state = new AtomicReference<State>(State.INIT);
  
  @Deprecated
  public PcapWriteHandler(OutputStream outputStream) {
    this(outputStream, false, true);
  }
  
  @Deprecated
  public PcapWriteHandler(OutputStream outputStream, boolean captureZeroByte, boolean writePcapGlobalHeader) {
    this.outputStream = (OutputStream)ObjectUtil.checkNotNull(outputStream, "OutputStream");
    this.captureZeroByte = captureZeroByte;
    this.writePcapGlobalHeader = writePcapGlobalHeader;
    this.sharedOutputStream = false;
  }
  
  private PcapWriteHandler(Builder builder, OutputStream outputStream) {
    this.outputStream = outputStream;
    this.captureZeroByte = builder.captureZeroByte;
    this.sharedOutputStream = builder.sharedOutputStream;
    this.writePcapGlobalHeader = builder.writePcapGlobalHeader;
    this.channelType = builder.channelType;
    this.handlerAddr = builder.handlerAddr;
    this.initiatorAddr = builder.initiatorAddr;
    this.isServerPipeline = builder.isServerPipeline;
  }
  
  public static void writeGlobalHeader(OutputStream outputStream) throws IOException {
    PcapHeaders.writeGlobalHeader(outputStream);
  }
  
  private void initializeIfNecessary(ChannelHandlerContext ctx) throws Exception {
    if (this.state.get() != State.INIT)
      return; 
    this.pCapWriter = new PcapWriter(this);
    if (this.channelType == null)
      if (ctx.channel() instanceof io.netty.channel.socket.SocketChannel) {
        this.channelType = ChannelType.TCP;
        if (ctx.channel().parent() instanceof io.netty.channel.socket.ServerSocketChannel) {
          this.isServerPipeline = true;
          this.initiatorAddr = (InetSocketAddress)ctx.channel().remoteAddress();
          this.handlerAddr = (InetSocketAddress)ctx.channel().localAddress();
        } else {
          this.isServerPipeline = false;
          this.initiatorAddr = (InetSocketAddress)ctx.channel().localAddress();
          this.handlerAddr = (InetSocketAddress)ctx.channel().remoteAddress();
        } 
      } else if (ctx.channel() instanceof DatagramChannel) {
        this.channelType = ChannelType.UDP;
        DatagramChannel datagramChannel = (DatagramChannel)ctx.channel();
        if (datagramChannel.isConnected()) {
          this.initiatorAddr = (InetSocketAddress)ctx.channel().localAddress();
          this.handlerAddr = (InetSocketAddress)ctx.channel().remoteAddress();
        } 
      }  
    if (this.channelType == ChannelType.TCP) {
      this.logger.debug("Initiating Fake TCP 3-Way Handshake");
      ByteBuf tcpBuf = ctx.alloc().buffer();
      try {
        TCPPacket.writePacket(tcpBuf, null, 0, 0, this.initiatorAddr
            .getPort(), this.handlerAddr.getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.SYN });
        completeTCPWrite(this.initiatorAddr, this.handlerAddr, tcpBuf, ctx.alloc(), ctx);
        TCPPacket.writePacket(tcpBuf, null, 0, 1, this.handlerAddr
            .getPort(), this.initiatorAddr.getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.SYN, TCPPacket.TCPFlag.ACK });
        completeTCPWrite(this.handlerAddr, this.initiatorAddr, tcpBuf, ctx.alloc(), ctx);
        TCPPacket.writePacket(tcpBuf, null, 1, 1, this.initiatorAddr.getPort(), this.handlerAddr
            .getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.ACK });
        completeTCPWrite(this.initiatorAddr, this.handlerAddr, tcpBuf, ctx.alloc(), ctx);
      } finally {
        tcpBuf.release();
      } 
      this.logger.debug("Finished Fake TCP 3-Way Handshake");
    } 
    this.state.set(State.WRITING);
  }
  
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    initializeIfNecessary(ctx);
    super.channelActive(ctx);
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (this.state.get() == State.INIT)
      initializeIfNecessary(ctx); 
    if (this.state.get() == State.WRITING)
      if (this.channelType == ChannelType.TCP) {
        handleTCP(ctx, msg, false);
      } else if (this.channelType == ChannelType.UDP) {
        handleUDP(ctx, msg);
      } else {
        logDiscard();
      }  
    super.channelRead(ctx, msg);
  }
  
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    if (this.state.get() == State.INIT)
      initializeIfNecessary(ctx); 
    if (this.state.get() == State.WRITING)
      if (this.channelType == ChannelType.TCP) {
        handleTCP(ctx, msg, true);
      } else if (this.channelType == ChannelType.UDP) {
        handleUDP(ctx, msg);
      } else {
        logDiscard();
      }  
    super.write(ctx, msg, promise);
  }
  
  private void handleTCP(ChannelHandlerContext ctx, Object msg, boolean isWriteOperation) {
    if (msg instanceof ByteBuf) {
      if (((ByteBuf)msg).readableBytes() == 0 && !this.captureZeroByte) {
        this.logger.debug("Discarding Zero Byte TCP Packet. isWriteOperation {}", Boolean.valueOf(isWriteOperation));
        return;
      } 
      ByteBufAllocator byteBufAllocator = ctx.alloc();
      ByteBuf packet = ((ByteBuf)msg).duplicate();
      ByteBuf tcpBuf = byteBufAllocator.buffer();
      int bytes = packet.readableBytes();
      try {
        if (isWriteOperation) {
          InetSocketAddress srcAddr, dstAddr;
          if (this.isServerPipeline) {
            srcAddr = this.handlerAddr;
            dstAddr = this.initiatorAddr;
          } else {
            srcAddr = this.initiatorAddr;
            dstAddr = this.handlerAddr;
          } 
          TCPPacket.writePacket(tcpBuf, packet, this.sendSegmentNumber, this.receiveSegmentNumber, srcAddr.getPort(), dstAddr
              .getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.ACK });
          completeTCPWrite(srcAddr, dstAddr, tcpBuf, byteBufAllocator, ctx);
          logTCP(true, bytes, this.sendSegmentNumber, this.receiveSegmentNumber, srcAddr, dstAddr, false);
          this.sendSegmentNumber += bytes;
          TCPPacket.writePacket(tcpBuf, null, this.receiveSegmentNumber, this.sendSegmentNumber, dstAddr.getPort(), srcAddr
              .getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.ACK });
          completeTCPWrite(dstAddr, srcAddr, tcpBuf, byteBufAllocator, ctx);
          logTCP(true, bytes, this.sendSegmentNumber, this.receiveSegmentNumber, dstAddr, srcAddr, true);
        } else {
          InetSocketAddress srcAddr, dstAddr;
          if (this.isServerPipeline) {
            srcAddr = this.initiatorAddr;
            dstAddr = this.handlerAddr;
          } else {
            srcAddr = this.handlerAddr;
            dstAddr = this.initiatorAddr;
          } 
          TCPPacket.writePacket(tcpBuf, packet, this.receiveSegmentNumber, this.sendSegmentNumber, srcAddr.getPort(), dstAddr
              .getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.ACK });
          completeTCPWrite(srcAddr, dstAddr, tcpBuf, byteBufAllocator, ctx);
          logTCP(false, bytes, this.receiveSegmentNumber, this.sendSegmentNumber, srcAddr, dstAddr, false);
          this.receiveSegmentNumber += bytes;
          TCPPacket.writePacket(tcpBuf, null, this.sendSegmentNumber, this.receiveSegmentNumber, dstAddr.getPort(), srcAddr
              .getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.ACK });
          completeTCPWrite(dstAddr, srcAddr, tcpBuf, byteBufAllocator, ctx);
          logTCP(false, bytes, this.sendSegmentNumber, this.receiveSegmentNumber, dstAddr, srcAddr, true);
        } 
      } finally {
        tcpBuf.release();
      } 
    } else {
      this.logger.debug("Discarding Pcap Write for TCP Object: {}", msg);
    } 
  }
  
  private void completeTCPWrite(InetSocketAddress srcAddr, InetSocketAddress dstAddr, ByteBuf tcpBuf, ByteBufAllocator byteBufAllocator, ChannelHandlerContext ctx) {
    ByteBuf ipBuf = byteBufAllocator.buffer();
    ByteBuf ethernetBuf = byteBufAllocator.buffer();
    ByteBuf pcap = byteBufAllocator.buffer();
    try {
      if (srcAddr.getAddress() instanceof Inet4Address && dstAddr.getAddress() instanceof Inet4Address) {
        IPPacket.writeTCPv4(ipBuf, tcpBuf, 
            NetUtil.ipv4AddressToInt((Inet4Address)srcAddr.getAddress()), 
            NetUtil.ipv4AddressToInt((Inet4Address)dstAddr.getAddress()));
        EthernetPacket.writeIPv4(ethernetBuf, ipBuf);
      } else if (srcAddr.getAddress() instanceof java.net.Inet6Address && dstAddr.getAddress() instanceof java.net.Inet6Address) {
        IPPacket.writeTCPv6(ipBuf, tcpBuf, srcAddr
            .getAddress().getAddress(), dstAddr
            .getAddress().getAddress());
        EthernetPacket.writeIPv6(ethernetBuf, ipBuf);
      } else {
        this.logger.error("Source and Destination IP Address versions are not same. Source Address: {}, Destination Address: {}", srcAddr
            .getAddress(), dstAddr.getAddress());
        return;
      } 
      this.pCapWriter.writePacket(pcap, ethernetBuf);
    } catch (IOException ex) {
      this.logger.error("Caught Exception While Writing Packet into Pcap", ex);
      ctx.fireExceptionCaught(ex);
    } finally {
      ipBuf.release();
      ethernetBuf.release();
      pcap.release();
    } 
  }
  
  private void handleUDP(ChannelHandlerContext ctx, Object msg) {
    ByteBuf udpBuf = ctx.alloc().buffer();
    try {
      if (msg instanceof DatagramPacket) {
        if (((ByteBuf)((DatagramPacket)msg).content()).readableBytes() == 0 && !this.captureZeroByte) {
          this.logger.debug("Discarding Zero Byte UDP Packet");
          return;
        } 
        DatagramPacket datagramPacket = ((DatagramPacket)msg).duplicate();
        InetSocketAddress srcAddr = (InetSocketAddress)datagramPacket.sender();
        InetSocketAddress dstAddr = (InetSocketAddress)datagramPacket.recipient();
        if (srcAddr == null)
          srcAddr = (InetSocketAddress)ctx.channel().localAddress(); 
        this.logger.debug("Writing UDP Data of {} Bytes, Src Addr {}, Dst Addr {}", new Object[] { Integer.valueOf(((ByteBuf)datagramPacket.content()).readableBytes()), srcAddr, dstAddr });
        UDPPacket.writePacket(udpBuf, (ByteBuf)datagramPacket.content(), srcAddr.getPort(), dstAddr.getPort());
        completeUDPWrite(srcAddr, dstAddr, udpBuf, ctx.alloc(), ctx);
      } else if (msg instanceof ByteBuf && (
        !(ctx.channel() instanceof DatagramChannel) || ((DatagramChannel)ctx.channel()).isConnected())) {
        if (((ByteBuf)msg).readableBytes() == 0 && !this.captureZeroByte) {
          this.logger.debug("Discarding Zero Byte UDP Packet");
          return;
        } 
        ByteBuf byteBuf = ((ByteBuf)msg).duplicate();
        this.logger.debug("Writing UDP Data of {} Bytes, Src Addr {}, Dst Addr {}", new Object[] { Integer.valueOf(byteBuf.readableBytes()), this.initiatorAddr, this.handlerAddr });
        UDPPacket.writePacket(udpBuf, byteBuf, this.initiatorAddr.getPort(), this.handlerAddr.getPort());
        completeUDPWrite(this.initiatorAddr, this.handlerAddr, udpBuf, ctx.alloc(), ctx);
      } else {
        this.logger.debug("Discarding Pcap Write for UDP Object: {}", msg);
      } 
    } finally {
      udpBuf.release();
    } 
  }
  
  private void completeUDPWrite(InetSocketAddress srcAddr, InetSocketAddress dstAddr, ByteBuf udpBuf, ByteBufAllocator byteBufAllocator, ChannelHandlerContext ctx) {
    ByteBuf ipBuf = byteBufAllocator.buffer();
    ByteBuf ethernetBuf = byteBufAllocator.buffer();
    ByteBuf pcap = byteBufAllocator.buffer();
    try {
      if (srcAddr.getAddress() instanceof Inet4Address && dstAddr.getAddress() instanceof Inet4Address) {
        IPPacket.writeUDPv4(ipBuf, udpBuf, 
            NetUtil.ipv4AddressToInt((Inet4Address)srcAddr.getAddress()), 
            NetUtil.ipv4AddressToInt((Inet4Address)dstAddr.getAddress()));
        EthernetPacket.writeIPv4(ethernetBuf, ipBuf);
      } else if (srcAddr.getAddress() instanceof java.net.Inet6Address && dstAddr.getAddress() instanceof java.net.Inet6Address) {
        IPPacket.writeUDPv6(ipBuf, udpBuf, srcAddr
            .getAddress().getAddress(), dstAddr
            .getAddress().getAddress());
        EthernetPacket.writeIPv6(ethernetBuf, ipBuf);
      } else {
        this.logger.error("Source and Destination IP Address versions are not same. Source Address: {}, Destination Address: {}", srcAddr
            .getAddress(), dstAddr.getAddress());
        return;
      } 
      this.pCapWriter.writePacket(pcap, ethernetBuf);
    } catch (IOException ex) {
      this.logger.error("Caught Exception While Writing Packet into Pcap", ex);
      ctx.fireExceptionCaught(ex);
    } finally {
      ipBuf.release();
      ethernetBuf.release();
      pcap.release();
    } 
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    if (this.channelType == ChannelType.TCP) {
      this.logger.debug("Starting Fake TCP FIN+ACK Flow to close connection");
      ByteBufAllocator byteBufAllocator = ctx.alloc();
      ByteBuf tcpBuf = byteBufAllocator.buffer();
      try {
        TCPPacket.writePacket(tcpBuf, null, this.sendSegmentNumber, this.receiveSegmentNumber, this.initiatorAddr.getPort(), this.handlerAddr
            .getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.FIN, TCPPacket.TCPFlag.ACK });
        completeTCPWrite(this.initiatorAddr, this.handlerAddr, tcpBuf, byteBufAllocator, ctx);
        TCPPacket.writePacket(tcpBuf, null, this.receiveSegmentNumber, this.sendSegmentNumber, this.handlerAddr.getPort(), this.initiatorAddr
            .getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.FIN, TCPPacket.TCPFlag.ACK });
        completeTCPWrite(this.handlerAddr, this.initiatorAddr, tcpBuf, byteBufAllocator, ctx);
        TCPPacket.writePacket(tcpBuf, null, this.sendSegmentNumber + 1, this.receiveSegmentNumber + 1, this.initiatorAddr
            .getPort(), this.handlerAddr.getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.ACK });
        completeTCPWrite(this.initiatorAddr, this.handlerAddr, tcpBuf, byteBufAllocator, ctx);
      } finally {
        tcpBuf.release();
      } 
      this.logger.debug("Finished Fake TCP FIN+ACK Flow to close connection");
    } 
    close();
    super.handlerRemoved(ctx);
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (this.channelType == ChannelType.TCP) {
      ByteBuf tcpBuf = ctx.alloc().buffer();
      try {
        TCPPacket.writePacket(tcpBuf, null, this.sendSegmentNumber, this.receiveSegmentNumber, this.initiatorAddr.getPort(), this.handlerAddr
            .getPort(), new TCPPacket.TCPFlag[] { TCPPacket.TCPFlag.RST, TCPPacket.TCPFlag.ACK });
        completeTCPWrite(this.initiatorAddr, this.handlerAddr, tcpBuf, ctx.alloc(), ctx);
      } finally {
        tcpBuf.release();
      } 
      this.logger.debug("Sent Fake TCP RST to close connection");
    } 
    close();
    ctx.fireExceptionCaught(cause);
  }
  
  private void logTCP(boolean isWriteOperation, int bytes, int sendSegmentNumber, int receiveSegmentNumber, InetSocketAddress srcAddr, InetSocketAddress dstAddr, boolean ackOnly) {
    if (this.logger.isDebugEnabled())
      if (ackOnly) {
        this.logger.debug("Writing TCP ACK, isWriteOperation {}, Segment Number {}, Ack Number {}, Src Addr {}, Dst Addr {}", new Object[] { Boolean.valueOf(isWriteOperation), Integer.valueOf(sendSegmentNumber), Integer.valueOf(receiveSegmentNumber), dstAddr, srcAddr });
      } else {
        this.logger.debug("Writing TCP Data of {} Bytes, isWriteOperation {}, Segment Number {}, Ack Number {}, Src Addr {}, Dst Addr {}", new Object[] { Integer.valueOf(bytes), Boolean.valueOf(isWriteOperation), Integer.valueOf(sendSegmentNumber), 
              Integer.valueOf(receiveSegmentNumber), srcAddr, dstAddr });
      }  
  }
  
  OutputStream outputStream() {
    return this.outputStream;
  }
  
  boolean sharedOutputStream() {
    return this.sharedOutputStream;
  }
  
  public boolean isWriting() {
    return (this.state.get() == State.WRITING);
  }
  
  State state() {
    return this.state.get();
  }
  
  public void pause() {
    if (!this.state.compareAndSet(State.WRITING, State.PAUSED))
      throw new IllegalStateException("State must be 'STARTED' to pause but current state is: " + this.state); 
  }
  
  public void resume() {
    if (!this.state.compareAndSet(State.PAUSED, State.WRITING))
      throw new IllegalStateException("State must be 'PAUSED' to resume but current state is: " + this.state); 
  }
  
  void markClosed() {
    if (this.state.get() != State.CLOSED)
      this.state.set(State.CLOSED); 
  }
  
  PcapWriter pCapWriter() {
    return this.pCapWriter;
  }
  
  private void logDiscard() {
    this.logger.warn("Discarding pcap write because channel type is unknown. The channel this handler is registered on is not a SocketChannel or DatagramChannel, so the inference does not work. Please call forceTcpChannel or forceUdpChannel before registering the handler.");
  }
  
  public String toString() {
    return "PcapWriteHandler{captureZeroByte=" + this.captureZeroByte + ", writePcapGlobalHeader=" + this.writePcapGlobalHeader + ", sharedOutputStream=" + this.sharedOutputStream + ", sendSegmentNumber=" + this.sendSegmentNumber + ", receiveSegmentNumber=" + this.receiveSegmentNumber + ", channelType=" + this.channelType + ", initiatorAddr=" + this.initiatorAddr + ", handlerAddr=" + this.handlerAddr + ", isServerPipeline=" + this.isServerPipeline + ", state=" + this.state + '}';
  }
  
  public void close() throws IOException {
    if (this.state.get() == State.CLOSED) {
      this.logger.debug("PcapWriterHandler is already closed");
    } else {
      markClosed();
      this.pCapWriter.close();
      this.logger.debug("PcapWriterHandler is now closed");
    } 
  }
  
  private enum ChannelType {
    TCP, UDP;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private boolean captureZeroByte;
    
    private boolean sharedOutputStream;
    
    private boolean writePcapGlobalHeader = true;
    
    private PcapWriteHandler.ChannelType channelType;
    
    private InetSocketAddress initiatorAddr;
    
    private InetSocketAddress handlerAddr;
    
    private boolean isServerPipeline;
    
    public Builder captureZeroByte(boolean captureZeroByte) {
      this.captureZeroByte = captureZeroByte;
      return this;
    }
    
    public Builder sharedOutputStream(boolean sharedOutputStream) {
      this.sharedOutputStream = sharedOutputStream;
      return this;
    }
    
    public Builder writePcapGlobalHeader(boolean writePcapGlobalHeader) {
      this.writePcapGlobalHeader = writePcapGlobalHeader;
      return this;
    }
    
    public Builder forceTcpChannel(InetSocketAddress serverAddress, InetSocketAddress clientAddress, boolean isServerPipeline) {
      this.channelType = PcapWriteHandler.ChannelType.TCP;
      this.handlerAddr = (InetSocketAddress)ObjectUtil.checkNotNull(serverAddress, "serverAddress");
      this.initiatorAddr = (InetSocketAddress)ObjectUtil.checkNotNull(clientAddress, "clientAddress");
      this.isServerPipeline = isServerPipeline;
      return this;
    }
    
    public Builder forceUdpChannel(InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
      this.channelType = PcapWriteHandler.ChannelType.UDP;
      this.handlerAddr = (InetSocketAddress)ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
      this.initiatorAddr = (InetSocketAddress)ObjectUtil.checkNotNull(localAddress, "localAddress");
      return this;
    }
    
    public PcapWriteHandler build(OutputStream outputStream) {
      ObjectUtil.checkNotNull(outputStream, "outputStream");
      return new PcapWriteHandler(this, outputStream);
    }
    
    private Builder() {}
  }
}
