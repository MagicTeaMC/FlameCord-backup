package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;
import java.util.AbstractMap;
import java.util.Map;

public class DefaultMaxBytesRecvByteBufAllocator implements MaxBytesRecvByteBufAllocator {
  private volatile int maxBytesPerRead;
  
  private volatile int maxBytesPerIndividualRead;
  
  private final class HandleImpl implements RecvByteBufAllocator.ExtendedHandle {
    private int individualReadMax;
    
    private int bytesToRead;
    
    private int lastBytesRead;
    
    private int attemptBytesRead;
    
    private final UncheckedBooleanSupplier defaultMaybeMoreSupplier = new UncheckedBooleanSupplier() {
        public boolean get() {
          return (DefaultMaxBytesRecvByteBufAllocator.HandleImpl.this.attemptBytesRead == DefaultMaxBytesRecvByteBufAllocator.HandleImpl.this.lastBytesRead);
        }
      };
    
    public ByteBuf allocate(ByteBufAllocator alloc) {
      return alloc.ioBuffer(guess());
    }
    
    public int guess() {
      return Math.min(this.individualReadMax, this.bytesToRead);
    }
    
    public void reset(ChannelConfig config) {
      this.bytesToRead = DefaultMaxBytesRecvByteBufAllocator.this.maxBytesPerRead();
      this.individualReadMax = DefaultMaxBytesRecvByteBufAllocator.this.maxBytesPerIndividualRead();
    }
    
    public void incMessagesRead(int amt) {}
    
    public void lastBytesRead(int bytes) {
      this.lastBytesRead = bytes;
      this.bytesToRead -= bytes;
    }
    
    public int lastBytesRead() {
      return this.lastBytesRead;
    }
    
    public boolean continueReading() {
      return continueReading(this.defaultMaybeMoreSupplier);
    }
    
    public boolean continueReading(UncheckedBooleanSupplier maybeMoreDataSupplier) {
      return (this.bytesToRead > 0 && maybeMoreDataSupplier.get());
    }
    
    public void readComplete() {}
    
    public void attemptedBytesRead(int bytes) {
      this.attemptBytesRead = bytes;
    }
    
    public int attemptedBytesRead() {
      return this.attemptBytesRead;
    }
    
    private HandleImpl() {}
  }
  
  public DefaultMaxBytesRecvByteBufAllocator() {
    this(65536, 65536);
  }
  
  public DefaultMaxBytesRecvByteBufAllocator(int maxBytesPerRead, int maxBytesPerIndividualRead) {
    checkMaxBytesPerReadPair(maxBytesPerRead, maxBytesPerIndividualRead);
    this.maxBytesPerRead = maxBytesPerRead;
    this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
  }
  
  public RecvByteBufAllocator.Handle newHandle() {
    return new HandleImpl();
  }
  
  public int maxBytesPerRead() {
    return this.maxBytesPerRead;
  }
  
  public DefaultMaxBytesRecvByteBufAllocator maxBytesPerRead(int maxBytesPerRead) {
    ObjectUtil.checkPositive(maxBytesPerRead, "maxBytesPerRead");
    synchronized (this) {
      int maxBytesPerIndividualRead = maxBytesPerIndividualRead();
      if (maxBytesPerRead < maxBytesPerIndividualRead)
        throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + maxBytesPerIndividualRead + "): " + maxBytesPerRead); 
      this.maxBytesPerRead = maxBytesPerRead;
    } 
    return this;
  }
  
  public int maxBytesPerIndividualRead() {
    return this.maxBytesPerIndividualRead;
  }
  
  public DefaultMaxBytesRecvByteBufAllocator maxBytesPerIndividualRead(int maxBytesPerIndividualRead) {
    ObjectUtil.checkPositive(maxBytesPerIndividualRead, "maxBytesPerIndividualRead");
    synchronized (this) {
      int maxBytesPerRead = maxBytesPerRead();
      if (maxBytesPerIndividualRead > maxBytesPerRead)
        throw new IllegalArgumentException("maxBytesPerIndividualRead cannot be greater than maxBytesPerRead (" + maxBytesPerRead + "): " + maxBytesPerIndividualRead); 
      this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
    } 
    return this;
  }
  
  public synchronized Map.Entry<Integer, Integer> maxBytesPerReadPair() {
    return new AbstractMap.SimpleEntry<Integer, Integer>(Integer.valueOf(this.maxBytesPerRead), Integer.valueOf(this.maxBytesPerIndividualRead));
  }
  
  private static void checkMaxBytesPerReadPair(int maxBytesPerRead, int maxBytesPerIndividualRead) {
    ObjectUtil.checkPositive(maxBytesPerRead, "maxBytesPerRead");
    ObjectUtil.checkPositive(maxBytesPerIndividualRead, "maxBytesPerIndividualRead");
    if (maxBytesPerRead < maxBytesPerIndividualRead)
      throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + maxBytesPerIndividualRead + "): " + maxBytesPerRead); 
  }
  
  public DefaultMaxBytesRecvByteBufAllocator maxBytesPerReadPair(int maxBytesPerRead, int maxBytesPerIndividualRead) {
    checkMaxBytesPerReadPair(maxBytesPerRead, maxBytesPerIndividualRead);
    synchronized (this) {
      this.maxBytesPerRead = maxBytesPerRead;
      this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
    } 
    return this;
  }
}
