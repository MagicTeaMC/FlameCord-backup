package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.IntegerUnixChannelOption;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.RawUnixChannelOption;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class EpollChannelConfig extends DefaultChannelConfig {
  private volatile long maxBytesPerGatheringWrite = Limits.SSIZE_MAX;
  
  EpollChannelConfig(AbstractEpollChannel channel) {
    super((Channel)channel);
  }
  
  EpollChannelConfig(AbstractEpollChannel channel, RecvByteBufAllocator recvByteBufAllocator) {
    super((Channel)channel, recvByteBufAllocator);
  }
  
  public Map<ChannelOption<?>, Object> getOptions() {
    return getOptions(super.getOptions(), new ChannelOption[] { EpollChannelOption.EPOLL_MODE });
  }
  
  public <T> T getOption(ChannelOption<T> option) {
    if (option == EpollChannelOption.EPOLL_MODE)
      return (T)getEpollMode(); 
    try {
      if (option instanceof IntegerUnixChannelOption) {
        IntegerUnixChannelOption opt = (IntegerUnixChannelOption)option;
        return (T)Integer.valueOf(((AbstractEpollChannel)this.channel).socket.getIntOpt(opt
              .level(), opt.optname()));
      } 
      if (option instanceof RawUnixChannelOption) {
        RawUnixChannelOption opt = (RawUnixChannelOption)option;
        ByteBuffer out = ByteBuffer.allocate(opt.length());
        ((AbstractEpollChannel)this.channel).socket.getRawOpt(opt.level(), opt.optname(), out);
        return (T)out.flip();
      } 
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
    return (T)super.getOption(option);
  }
  
  public <T> boolean setOption(ChannelOption<T> option, T value) {
    validate(option, value);
    if (option == EpollChannelOption.EPOLL_MODE) {
      setEpollMode((EpollMode)value);
    } else {
      try {
        if (option instanceof IntegerUnixChannelOption) {
          IntegerUnixChannelOption opt = (IntegerUnixChannelOption)option;
          ((AbstractEpollChannel)this.channel).socket.setIntOpt(opt.level(), opt.optname(), ((Integer)value).intValue());
          return true;
        } 
        if (option instanceof RawUnixChannelOption) {
          RawUnixChannelOption opt = (RawUnixChannelOption)option;
          ((AbstractEpollChannel)this.channel).socket.setRawOpt(opt.level(), opt.optname(), (ByteBuffer)value);
          return true;
        } 
      } catch (IOException e) {
        throw new ChannelException(e);
      } 
      return super.setOption(option, value);
    } 
    return true;
  }
  
  public EpollChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
    super.setConnectTimeoutMillis(connectTimeoutMillis);
    return this;
  }
  
  @Deprecated
  public EpollChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
    super.setMaxMessagesPerRead(maxMessagesPerRead);
    return this;
  }
  
  public EpollChannelConfig setWriteSpinCount(int writeSpinCount) {
    super.setWriteSpinCount(writeSpinCount);
    return this;
  }
  
  public EpollChannelConfig setAllocator(ByteBufAllocator allocator) {
    super.setAllocator(allocator);
    return this;
  }
  
  public EpollChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
    if (!(allocator.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle))
      throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class); 
    super.setRecvByteBufAllocator(allocator);
    return this;
  }
  
  public EpollChannelConfig setAutoRead(boolean autoRead) {
    super.setAutoRead(autoRead);
    return this;
  }
  
  @Deprecated
  public EpollChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
    super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
    return this;
  }
  
  @Deprecated
  public EpollChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
    super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
    return this;
  }
  
  public EpollChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
    super.setWriteBufferWaterMark(writeBufferWaterMark);
    return this;
  }
  
  public EpollChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
    super.setMessageSizeEstimator(estimator);
    return this;
  }
  
  public EpollMode getEpollMode() {
    return ((AbstractEpollChannel)this.channel).isFlagSet(Native.EPOLLET) ? EpollMode.EDGE_TRIGGERED : EpollMode.LEVEL_TRIGGERED;
  }
  
  public EpollChannelConfig setEpollMode(EpollMode mode) {
    ObjectUtil.checkNotNull(mode, "mode");
    try {
      switch (mode) {
        case EDGE_TRIGGERED:
          checkChannelNotRegistered();
          ((AbstractEpollChannel)this.channel).setFlag(Native.EPOLLET);
          return this;
        case LEVEL_TRIGGERED:
          checkChannelNotRegistered();
          ((AbstractEpollChannel)this.channel).clearFlag(Native.EPOLLET);
          return this;
      } 
      throw new Error();
    } catch (IOException e) {
      throw new ChannelException(e);
    } 
  }
  
  private void checkChannelNotRegistered() {
    if (this.channel.isRegistered())
      throw new IllegalStateException("EpollMode can only be changed before channel is registered"); 
  }
  
  protected final void autoReadCleared() {
    ((AbstractEpollChannel)this.channel).clearEpollIn();
  }
  
  final void setMaxBytesPerGatheringWrite(long maxBytesPerGatheringWrite) {
    this.maxBytesPerGatheringWrite = maxBytesPerGatheringWrite;
  }
  
  final long getMaxBytesPerGatheringWrite() {
    return this.maxBytesPerGatheringWrite;
  }
}
