package com.lmax.disruptor;

public interface LifecycleAware {
  void onStart();
  
  void onShutdown();
}
