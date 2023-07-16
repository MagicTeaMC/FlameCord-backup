package io.netty.channel.epoll;

import io.netty.channel.RecvByteBufAllocator;

final class EpollRecvByteAllocatorStreamingHandle extends EpollRecvByteAllocatorHandle {
  EpollRecvByteAllocatorStreamingHandle(RecvByteBufAllocator.ExtendedHandle handle) {
    super(handle);
  }
  
  boolean maybeMoreDataToRead() {
    return (lastBytesRead() == attemptedBytesRead() || isReceivedRdHup());
  }
}
