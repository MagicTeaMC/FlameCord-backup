package io.netty.channel;

public final class ServerChannelRecvByteBufAllocator extends DefaultMaxMessagesRecvByteBufAllocator {
  public ServerChannelRecvByteBufAllocator() {
    super(1, true);
  }
  
  public RecvByteBufAllocator.Handle newHandle() {
    return new DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle() {
        public int guess() {
          return 128;
        }
      };
  }
}
