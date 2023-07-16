package io.netty.bootstrap;

@Deprecated
public interface ChannelFactory<T extends io.netty.channel.Channel> {
  T newChannel();
}
