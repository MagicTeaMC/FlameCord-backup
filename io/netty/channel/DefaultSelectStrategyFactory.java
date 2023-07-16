package io.netty.channel;

public final class DefaultSelectStrategyFactory implements SelectStrategyFactory {
  public static final SelectStrategyFactory INSTANCE = new DefaultSelectStrategyFactory();
  
  public SelectStrategy newSelectStrategy() {
    return DefaultSelectStrategy.INSTANCE;
  }
}