package com.lmax.disruptor.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class ThreadHints {
  private static final MethodHandle ON_SPIN_WAIT_METHOD_HANDLE;
  
  static {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodHandle methodHandle = null;
    try {
      methodHandle = lookup.findStatic(Thread.class, "onSpinWait", MethodType.methodType(void.class));
    } catch (Exception exception) {}
    ON_SPIN_WAIT_METHOD_HANDLE = methodHandle;
  }
  
  public static void onSpinWait() {
    if (null != ON_SPIN_WAIT_METHOD_HANDLE)
      try {
        ON_SPIN_WAIT_METHOD_HANDLE.invokeExact();
      } catch (Throwable throwable) {} 
  }
}
