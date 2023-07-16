package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DuplexChannelConfig;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public final class EpollSocketChannelConfig extends EpollChannelConfig implements SocketChannelConfig {
  private volatile boolean allowHalfClosure;
  
  private volatile boolean tcpFastopen;
  
  EpollSocketChannelConfig(EpollSocketChannel channel) {
    super(channel);
    if (PlatformDependent.canEnableTcpNoDelayByDefault())
      setTcpNoDelay(true); 
    calculateMaxBytesPerGatheringWrite();
  }
  
  public Map<ChannelOption<?>, Object> getOptions() {
    return getOptions(super
        .getOptions(), new ChannelOption[] { 
          ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE, EpollChannelOption.TCP_CORK, EpollChannelOption.TCP_NOTSENT_LOWAT, 
          EpollChannelOption.TCP_KEEPCNT, EpollChannelOption.TCP_KEEPIDLE, EpollChannelOption.TCP_KEEPINTVL, EpollChannelOption.TCP_MD5SIG, EpollChannelOption.TCP_QUICKACK, EpollChannelOption.IP_TRANSPARENT, ChannelOption.TCP_FASTOPEN_CONNECT, EpollChannelOption.SO_BUSY_POLL });
  }
  
  public <T> T getOption(ChannelOption<T> option) {
    if (option == ChannelOption.SO_RCVBUF)
      return (T)Integer.valueOf(getReceiveBufferSize()); 
    if (option == ChannelOption.SO_SNDBUF)
      return (T)Integer.valueOf(getSendBufferSize()); 
    if (option == ChannelOption.TCP_NODELAY)
      return (T)Boolean.valueOf(isTcpNoDelay()); 
    if (option == ChannelOption.SO_KEEPALIVE)
      return (T)Boolean.valueOf(isKeepAlive()); 
    if (option == ChannelOption.SO_REUSEADDR)
      return (T)Boolean.valueOf(isReuseAddress()); 
    if (option == ChannelOption.SO_LINGER)
      return (T)Integer.valueOf(getSoLinger()); 
    if (option == ChannelOption.IP_TOS)
      return (T)Integer.valueOf(getTrafficClass()); 
    if (option == ChannelOption.ALLOW_HALF_CLOSURE)
      return (T)Boolean.valueOf(isAllowHalfClosure()); 
    if (option == EpollChannelOption.TCP_CORK)
      return (T)Boolean.valueOf(isTcpCork()); 
    if (option == EpollChannelOption.TCP_NOTSENT_LOWAT)
      return (T)Long.valueOf(getTcpNotSentLowAt()); 
    if (option == EpollChannelOption.TCP_KEEPIDLE)
      return (T)Integer.valueOf(getTcpKeepIdle()); 
    if (option == EpollChannelOption.TCP_KEEPINTVL)
      return (T)Integer.valueOf(getTcpKeepIntvl()); 
    if (option == EpollChannelOption.TCP_KEEPCNT)
      return (T)Integer.valueOf(getTcpKeepCnt()); 
    if (option == EpollChannelOption.TCP_USER_TIMEOUT)
      return (T)Integer.valueOf(getTcpUserTimeout()); 
    if (option == EpollChannelOption.TCP_QUICKACK)
      return (T)Boolean.valueOf(isTcpQuickAck()); 
    if (option == EpollChannelOption.IP_TRANSPARENT)
      return (T)Boolean.valueOf(isIpTransparent()); 
    if (option == ChannelOption.TCP_FASTOPEN_CONNECT)
      return (T)Boolean.valueOf(isTcpFastOpenConnect()); 
    if (option == EpollChannelOption.SO_BUSY_POLL)
      return (T)Integer.valueOf(getSoBusyPoll()); 
    return super.getOption(option);
  }
  
  public <T> boolean setOption(ChannelOption<T> option, T value) {
    validate(option, value);
    if (option == ChannelOption.SO_RCVBUF) {
      setReceiveBufferSize(((Integer)value).intValue());
    } else if (option == ChannelOption.SO_SNDBUF) {
      setSendBufferSize(((Integer)value).intValue());
    } else if (option == ChannelOption.TCP_NODELAY) {
      setTcpNoDelay(((Boolean)value).booleanValue());
    } else if (option == ChannelOption.SO_KEEPALIVE) {
      setKeepAlive(((Boolean)value).booleanValue());
    } else if (option == ChannelOption.SO_REUSEADDR) {
      setReuseAddress(((Boolean)value).booleanValue());
    } else if (option == ChannelOption.SO_LINGER) {
      setSoLinger(((Integer)value).intValue());
    } else if (option == ChannelOption.IP_TOS) {
      setTrafficClass(((Integer)value).intValue());
    } else if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
      setAllowHalfClosure(((Boolean)value).booleanValue());
    } else if (option == EpollChannelOption.TCP_CORK) {
      setTcpCork(((Boolean)value).booleanValue());
    } else if (option == EpollChannelOption.TCP_NOTSENT_LOWAT) {
      setTcpNotSentLowAt(((Long)value).longValue());
    } else if (option == EpollChannelOption.TCP_KEEPIDLE) {
      setTcpKeepIdle(((Integer)value).intValue());
    } else if (option == EpollChannelOption.TCP_KEEPCNT) {
      setTcpKeepCnt(((Integer)value).intValue());
    } else if (option == EpollChannelOption.TCP_KEEPINTVL) {
      setTcpKeepIntvl(((Integer)value).intValue());
    } else if (option == EpollChannelOption.TCP_USER_TIMEOUT) {
      setTcpUserTimeout(((Integer)value).intValue());
    } else if (option == EpollChannelOption.IP_TRANSPARENT) {
      setIpTransparent(((Boolean)value).booleanValue());
    } else if (option == EpollChannelOption.TCP_MD5SIG) {
      Map<InetAddress, byte[]> m = (Map<InetAddress, byte[]>)value;
      setTcpMd5Sig(m);
    } else if (option == EpollChannelOption.TCP_QUICKACK) {
      setTcpQuickAck(((Boolean)value).booleanValue());
    } else if (option == ChannelOption.TCP_FASTOPEN_CONNECT) {
      setTcpFastOpenConnect(((Boolean)value).booleanValue());
    } else if (option == EpollChannelOption.SO_BUSY_POLL) {
      setSoBusyPoll(((Integer)value).intValue());
    } else {
      return super.setOption(option, value);
    } 
    return true;
  }
  
  public int getReceiveBufferSize() {
    try {
      return ((EpollSocketChannel)this.channel).socket.getReceiveBufferSize();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getSendBufferSize() {
    try {
      return ((EpollSocketChannel)this.channel).socket.getSendBufferSize();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getSoLinger() {
    try {
      return ((EpollSocketChannel)this.channel).socket.getSoLinger();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getTrafficClass() {
    try {
      return ((EpollSocketChannel)this.channel).socket.getTrafficClass();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isKeepAlive() {
    try {
      return ((EpollSocketChannel)this.channel).socket.isKeepAlive();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isReuseAddress() {
    try {
      return ((EpollSocketChannel)this.channel).socket.isReuseAddress();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isTcpNoDelay() {
    try {
      return ((EpollSocketChannel)this.channel).socket.isTcpNoDelay();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isTcpCork() {
    try {
      return ((EpollSocketChannel)this.channel).socket.isTcpCork();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getSoBusyPoll() {
    try {
      return ((EpollSocketChannel)this.channel).socket.getSoBusyPoll();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public long getTcpNotSentLowAt() {
    try {
      return ((EpollSocketChannel)this.channel).socket.getTcpNotSentLowAt();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getTcpKeepIdle() {
    try {
      return ((EpollSocketChannel)this.channel).socket.getTcpKeepIdle();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getTcpKeepIntvl() {
    try {
      return ((EpollSocketChannel)this.channel).socket.getTcpKeepIntvl();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getTcpKeepCnt() {
    try {
      return ((EpollSocketChannel)this.channel).socket.getTcpKeepCnt();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public int getTcpUserTimeout() {
    try {
      return ((EpollSocketChannel)this.channel).socket.getTcpUserTimeout();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setKeepAlive(boolean keepAlive) {
    try {
      ((EpollSocketChannel)this.channel).socket.setKeepAlive(keepAlive);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
    return this;
  }
  
  public EpollSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
    try {
      ((EpollSocketChannel)this.channel).socket.setReceiveBufferSize(receiveBufferSize);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setReuseAddress(boolean reuseAddress) {
    try {
      ((EpollSocketChannel)this.channel).socket.setReuseAddress(reuseAddress);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setSendBufferSize(int sendBufferSize) {
    try {
      ((EpollSocketChannel)this.channel).socket.setSendBufferSize(sendBufferSize);
      calculateMaxBytesPerGatheringWrite();
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setSoLinger(int soLinger) {
    try {
      ((EpollSocketChannel)this.channel).socket.setSoLinger(soLinger);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
    try {
      ((EpollSocketChannel)this.channel).socket.setTcpNoDelay(tcpNoDelay);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setTcpCork(boolean tcpCork) {
    try {
      ((EpollSocketChannel)this.channel).socket.setTcpCork(tcpCork);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setSoBusyPoll(int loopMicros) {
    try {
      ((EpollSocketChannel)this.channel).socket.setSoBusyPoll(loopMicros);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setTcpNotSentLowAt(long tcpNotSentLowAt) {
    try {
      ((EpollSocketChannel)this.channel).socket.setTcpNotSentLowAt(tcpNotSentLowAt);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setTrafficClass(int trafficClass) {
    try {
      ((EpollSocketChannel)this.channel).socket.setTrafficClass(trafficClass);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setTcpKeepIdle(int seconds) {
    try {
      ((EpollSocketChannel)this.channel).socket.setTcpKeepIdle(seconds);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setTcpKeepIntvl(int seconds) {
    try {
      ((EpollSocketChannel)this.channel).socket.setTcpKeepIntvl(seconds);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  @Deprecated
  public EpollSocketChannelConfig setTcpKeepCntl(int probes) {
    return setTcpKeepCnt(probes);
  }
  
  public EpollSocketChannelConfig setTcpKeepCnt(int probes) {
    try {
      ((EpollSocketChannel)this.channel).socket.setTcpKeepCnt(probes);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setTcpUserTimeout(int milliseconds) {
    try {
      ((EpollSocketChannel)this.channel).socket.setTcpUserTimeout(milliseconds);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isIpTransparent() {
    try {
      return ((EpollSocketChannel)this.channel).socket.isIpTransparent();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setIpTransparent(boolean transparent) {
    try {
      ((EpollSocketChannel)this.channel).socket.setIpTransparent(transparent);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setTcpMd5Sig(Map<InetAddress, byte[]> keys) {
    try {
      ((EpollSocketChannel)this.channel).setTcpMd5Sig(keys);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setTcpQuickAck(boolean quickAck) {
    try {
      ((EpollSocketChannel)this.channel).socket.setTcpQuickAck(quickAck);
      return this;
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public boolean isTcpQuickAck() {
    try {
      return ((EpollSocketChannel)this.channel).socket.isTcpQuickAck();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  public EpollSocketChannelConfig setTcpFastOpenConnect(boolean fastOpenConnect) {
    this.tcpFastopen = fastOpenConnect;
    return this;
  }
  
  public boolean isTcpFastOpenConnect() {
    return this.tcpFastopen;
  }
  
  public boolean isAllowHalfClosure() {
    return this.allowHalfClosure;
  }
  
  public EpollSocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
    this.allowHalfClosure = allowHalfClosure;
    return this;
  }
  
  public EpollSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
    super.setConnectTimeoutMillis(connectTimeoutMillis);
    return this;
  }
  
  @Deprecated
  public EpollSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
    super.setMaxMessagesPerRead(maxMessagesPerRead);
    return this;
  }
  
  public EpollSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
    super.setWriteSpinCount(writeSpinCount);
    return this;
  }
  
  public EpollSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
    super.setAllocator(allocator);
    return this;
  }
  
  public EpollSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
    super.setRecvByteBufAllocator(allocator);
    return this;
  }
  
  public EpollSocketChannelConfig setAutoRead(boolean autoRead) {
    super.setAutoRead(autoRead);
    return this;
  }
  
  public EpollSocketChannelConfig setAutoClose(boolean autoClose) {
    super.setAutoClose(autoClose);
    return this;
  }
  
  @Deprecated
  public EpollSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
    super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
    return this;
  }
  
  @Deprecated
  public EpollSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
    super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
    return this;
  }
  
  public EpollSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
    super.setWriteBufferWaterMark(writeBufferWaterMark);
    return this;
  }
  
  public EpollSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
    super.setMessageSizeEstimator(estimator);
    return this;
  }
  
  public EpollSocketChannelConfig setEpollMode(EpollMode mode) {
    super.setEpollMode(mode);
    return this;
  }
  
  private void calculateMaxBytesPerGatheringWrite() {
    int newSendBufferSize = getSendBufferSize() << 1;
    if (newSendBufferSize > 0)
      setMaxBytesPerGatheringWrite((getSendBufferSize() << 1)); 
  }
}
