package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.unix.PreferredDirectByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;

class EpollRecvByteAllocatorHandle extends RecvByteBufAllocator.DelegatingHandle implements RecvByteBufAllocator.ExtendedHandle {
  private final PreferredDirectByteBufAllocator preferredDirectByteBufAllocator = new PreferredDirectByteBufAllocator();
  
  private final UncheckedBooleanSupplier defaultMaybeMoreDataSupplier = new UncheckedBooleanSupplier() {
      public boolean get() {
        return EpollRecvByteAllocatorHandle.this.maybeMoreDataToRead();
      }
    };
  
  private boolean isEdgeTriggered;
  
  private boolean receivedRdHup;
  
  EpollRecvByteAllocatorHandle(RecvByteBufAllocator.ExtendedHandle handle) {
    super((RecvByteBufAllocator.Handle)handle);
  }
  
  final void receivedRdHup() {
    this.receivedRdHup = true;
  }
  
  final boolean isReceivedRdHup() {
    return this.receivedRdHup;
  }
  
  boolean maybeMoreDataToRead() {
    return ((this.isEdgeTriggered && lastBytesRead() > 0) || (!this.isEdgeTriggered && 
      lastBytesRead() == attemptedBytesRead()));
  }
  
  final void edgeTriggered(boolean edgeTriggered) {
    this.isEdgeTriggered = edgeTriggered;
  }
  
  final boolean isEdgeTriggered() {
    return this.isEdgeTriggered;
  }
  
  public final ByteBuf allocate(ByteBufAllocator alloc) {
    this.preferredDirectByteBufAllocator.updateAllocator(alloc);
    return delegate().allocate((ByteBufAllocator)this.preferredDirectByteBufAllocator);
  }
  
  public final boolean continueReading(UncheckedBooleanSupplier maybeMoreDataSupplier) {
    return ((RecvByteBufAllocator.ExtendedHandle)delegate()).continueReading(maybeMoreDataSupplier);
  }
  
  public final boolean continueReading() {
    return continueReading(this.defaultMaybeMoreDataSupplier);
  }
}
