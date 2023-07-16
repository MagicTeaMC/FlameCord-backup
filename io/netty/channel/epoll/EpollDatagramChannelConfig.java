package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;

public final class EpollDatagramChannelConfig extends EpollChannelConfig implements DatagramChannelConfig {
  private boolean activeOnOpen;
  
  private volatile int maxDatagramSize;
  
  private volatile boolean gro;
  
  EpollDatagramChannelConfig(EpollDatagramChannel channel) {
    super(channel);
    setRecvByteBufAllocator((RecvByteBufAllocator)new FixedRecvByteBufAllocator(2048));
  }
  
  public Map<ChannelOption<?>, Object> getOptions() {
    return getOptions(super
        .getOptions(), new ChannelOption[] { 
          ChannelOption.SO_BROADCAST, ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.SO_REUSEADDR, ChannelOption.IP_MULTICAST_LOOP_DISABLED, ChannelOption.IP_MULTICAST_ADDR, ChannelOption.IP_MULTICAST_IF, ChannelOption.IP_MULTICAST_TTL, ChannelOption.IP_TOS, ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, 
          EpollChannelOption.SO_REUSEPORT, EpollChannelOption.IP_FREEBIND, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.IP_RECVORIGDSTADDR, EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE, EpollChannelOption.UDP_GRO });
  }
  
  public <T> T getOption(ChannelOption<T> option) {
    if (option == ChannelOption.SO_BROADCAST)
      return (T)Boolean.valueOf(isBroadcast()); 
    if (option == ChannelOption.SO_RCVBUF)
      return (T)Integer.valueOf(getReceiveBufferSize()); 
    if (option == ChannelOption.SO_SNDBUF)
      return (T)Integer.valueOf(getSendBufferSize()); 
    if (option == ChannelOption.SO_REUSEADDR)
      return (T)Boolean.valueOf(isReuseAddress()); 
    if (option == ChannelOption.IP_MULTICAST_LOOP_DISABLED)
      return (T)Boolean.valueOf(isLoopbackModeDisabled()); 
    if (option == ChannelOption.IP_MULTICAST_ADDR)
      return (T)getInterface(); 
    if (option == ChannelOption.IP_MULTICAST_IF)
      return (T)getNetworkInterface(); 
    if (option == ChannelOption.IP_MULTICAST_TTL)
      return (T)Integer.valueOf(getTimeToLive()); 
    if (option == ChannelOption.IP_TOS)
      return (T)Integer.valueOf(getTrafficClass()); 
    if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION)
      return (T)Boolean.valueOf(this.activeOnOpen); 
    if (option == EpollChannelOption.SO_REUSEPORT)
      return (T)Boolean.valueOf(isReusePort()); 
    if (option == EpollChannelOption.IP_TRANSPARENT)
      return (T)Boolean.valueOf(isIpTransparent()); 
    if (option == EpollChannelOption.IP_FREEBIND)
      return (T)Boolean.valueOf(isFreeBind()); 
    if (option == EpollChannelOption.IP_RECVORIGDSTADDR)
      return (T)Boolean.valueOf(isIpRecvOrigDestAddr()); 
    if (option == EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE)
      return (T)Integer.valueOf(getMaxDatagramPayloadSize()); 
    if (option == EpollChannelOption.UDP_GRO)
      return (T)Boolean.valueOf(isUdpGro()); 
    return super.getOption(option);
  }
  
  public <T> boolean setOption(ChannelOption<T> option, T value) {
    validate(option, value);
    if (option == ChannelOption.SO_BROADCAST) {
      setBroadcast(((Boolean)value).booleanValue());
    } else if (option == ChannelOption.SO_RCVBUF) {
      setReceiveBufferSize(((Integer)value).intValue());
    } else if (option == ChannelOption.SO_SNDBUF) {
      setSendBufferSize(((Integer)value).intValue());
    } else if (option == ChannelOption.SO_REUSEADDR) {
      setReuseAddress(((Boolean)value).booleanValue());
    } else if (option == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
      setLoopbackModeDisabled(((Boolean)value).booleanValue());
    } else if (option == ChannelOption.IP_MULTICAST_ADDR) {
      setInterface((InetAddress)value);
    } else if (option == ChannelOption.IP_MULTICAST_IF) {
      setNetworkInterface((NetworkInterface)value);
    } else if (option == ChannelOption.IP_MULTICAST_TTL) {
      setTimeToLive(((Integer)value).intValue());
    } else if (option == ChannelOption.IP_TOS) {
      setTrafficClass(((Integer)value).intValue());
    } else if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
      setActiveOnOpen(((Boolean)value).booleanValue());
    } else if (option == EpollChannelOption.SO_REUSEPORT) {
      setReusePort(((Boolean)value).booleanValue());
    } else if (option == EpollChannelOption.IP_FREEBIND) {
      setFreeBind(((Boolean)value).booleanValue());
    } else if (option == EpollChannelOption.IP_TRANSPARENT) {
      setIpTransparent(((Boolean)value).booleanValue());
    } else if (option == EpollChannelOption.IP_RECVORIGDSTADDR) {
      setIpRecvOrigDestAddr(((Boolean)value).booleanValue());
    } else if (option == EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE) {
      setMaxDatagramPayloadSize(((Integer)value).intValue());
    } else if (option == EpollChannelOption.UDP_GRO) {
      setUdpGro(((Boolean)value).booleanValue());
    } else {
      return super.setOption(option, value);
    } 
    return true;
  }
  
  private void setActiveOnOpen(boolean activeOnOpen) {
    if (this.channel.isRegistered())
      throw new IllegalStateException("Can only changed before channel was registered"); 
    this.activeOnOpen = activeOnOpen;
  }
  
  boolean getActiveOnOpen() {
    return this.activeOnOpen;
  }
  
  public EpollDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
    super.setMessageSizeEstimator(estimator);
    return this;
  }
  
  @Deprecated
  public EpollDatagramChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
    super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
    return this;
  }
  
  @Deprecated
  public EpollDatagramChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
    super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
    return this;
  }
  
  public EpollDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
    super.setWriteBufferWaterMark(writeBufferWaterMark);
    return this;
  }
  
  public EpollDatagramChannelConfig setAutoClose(boolean autoClose) {
    super.setAutoClose(autoClose);
    return this;
  }
  
  public EpollDatagramChannelConfig setAutoRead(boolean autoRead) {
    super.setAutoRead(autoRead);
    return this;
  }
  
  public EpollDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
    super.setRecvByteBufAllocator(allocator);
    return this;
  }
  
  public EpollDatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
    super.setWriteSpinCount(writeSpinCount);
    return this;
  }
  
  public EpollDatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
    super.setAllocator(allocator);
    return this;
  }
  
  public EpollDatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
    super.setConnectTimeoutMillis(connectTimeoutMillis);
    return this;
  }
  
  @Deprecated
  public EpollDatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
    super.setMaxMessagesPerRead(maxMessagesPerRead);
    return this;
  }
  
  public int getSendBufferSize() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.getSendBufferSize();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setSendBufferSize(int sendBufferSize) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setSendBufferSize(sendBufferSize);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getReceiveBufferSize() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.getReceiveBufferSize();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setReceiveBufferSize(int receiveBufferSize) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setReceiveBufferSize(receiveBufferSize);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getTrafficClass() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.getTrafficClass();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setTrafficClass(int trafficClass) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setTrafficClass(trafficClass);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isReuseAddress() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.isReuseAddress();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setReuseAddress(boolean reuseAddress) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setReuseAddress(reuseAddress);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isBroadcast() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.isBroadcast();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setBroadcast(boolean broadcast) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setBroadcast(broadcast);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isLoopbackModeDisabled() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.isLoopbackModeDisabled();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setLoopbackModeDisabled(loopbackModeDisabled);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getTimeToLive() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.getTimeToLive();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setTimeToLive(int ttl) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setTimeToLive(ttl);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public InetAddress getInterface() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.getInterface();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setInterface(InetAddress interfaceAddress) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setInterface(interfaceAddress);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public NetworkInterface getNetworkInterface() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.getNetworkInterface();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
    try {
      EpollDatagramChannel datagramChannel = (EpollDatagramChannel)this.channel;
      datagramChannel.socket.setNetworkInterface(networkInterface);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setEpollMode(EpollMode mode) {
    super.setEpollMode(mode);
    return this;
  }
  
  public boolean isReusePort() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.isReusePort();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setReusePort(boolean reusePort) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setReusePort(reusePort);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isIpTransparent() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.isIpTransparent();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setIpTransparent(boolean ipTransparent) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setIpTransparent(ipTransparent);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isFreeBind() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.isIpFreeBind();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setFreeBind(boolean freeBind) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setIpFreeBind(freeBind);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isIpRecvOrigDestAddr() {
    try {
      return ((EpollDatagramChannel)this.channel).socket.isIpRecvOrigDestAddr();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setIpRecvOrigDestAddr(boolean ipTransparent) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setIpRecvOrigDestAddr(ipTransparent);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollDatagramChannelConfig setMaxDatagramPayloadSize(int maxDatagramSize) {
    this.maxDatagramSize = ObjectUtil.checkPositiveOrZero(maxDatagramSize, "maxDatagramSize");
    return this;
  }
  
  public int getMaxDatagramPayloadSize() {
    return this.maxDatagramSize;
  }
  
  public EpollDatagramChannelConfig setUdpGro(boolean gro) {
    try {
      ((EpollDatagramChannel)this.channel).socket.setUdpGro(gro);
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
    this.gro = gro;
    return this;
  }
  
  public boolean isUdpGro() {
    return this.gro;
  }
  
  public EpollDatagramChannelConfig setMaxMessagesPerWrite(int maxMessagesPerWrite) {
    super.setMaxMessagesPerWrite(maxMessagesPerWrite);
    return this;
  }
}
