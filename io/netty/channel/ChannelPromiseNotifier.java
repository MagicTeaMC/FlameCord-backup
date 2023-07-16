package io.netty.channel;

import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseNotifier;

@Deprecated
public final class ChannelPromiseNotifier extends PromiseNotifier<Void, ChannelFuture> implements ChannelFutureListener {
  public ChannelPromiseNotifier(ChannelPromise... promises) {
    super((Promise[])promises);
  }
  
  public ChannelPromiseNotifier(boolean logNotifyFailure, ChannelPromise... promises) {
    super(logNotifyFailure, (Promise[])promises);
  }
}
