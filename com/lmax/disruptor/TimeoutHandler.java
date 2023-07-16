package com.lmax.disruptor;

public interface TimeoutHandler {
  void onTimeout(long paramLong) throws Exception;
}
