package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.concurrent.Callable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public final class Callables {
  public static <T> Callable<T> returning(@ParametricNullness T value) {
    return () -> value;
  }
  
  @Beta
  @GwtIncompatible
  public static <T> AsyncCallable<T> asAsyncCallable(Callable<T> callable, ListeningExecutorService listeningExecutorService) {
    Preconditions.checkNotNull(callable);
    Preconditions.checkNotNull(listeningExecutorService);
    return () -> listeningExecutorService.submit(callable);
  }
  
  @GwtIncompatible
  static <T> Callable<T> threadRenaming(Callable<T> callable, Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(nameSupplier);
    Preconditions.checkNotNull(callable);
    return () -> {
        Thread currentThread = Thread.currentThread();
        String oldName = currentThread.getName();
        boolean restoreName = trySetName((String)nameSupplier.get(), currentThread);
        try {
          return callable.call();
        } finally {
          if (restoreName)
            boolean bool = trySetName(oldName, currentThread); 
        } 
      };
  }
  
  @GwtIncompatible
  static Runnable threadRenaming(Runnable task, Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(nameSupplier);
    Preconditions.checkNotNull(task);
    return () -> {
        Thread currentThread = Thread.currentThread();
        String oldName = currentThread.getName();
        boolean restoreName = trySetName((String)nameSupplier.get(), currentThread);
        try {
          task.run();
        } finally {
          if (restoreName)
            boolean bool = trySetName(oldName, currentThread); 
        } 
      };
  }
  
  @GwtIncompatible
  private static boolean trySetName(String threadName, Thread currentThread) {
    try {
      currentThread.setName(threadName);
      return true;
    } catch (SecurityException e) {
      return false;
    } 
  }
}
