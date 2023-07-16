package org.eclipse.aether.util.concurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class WorkerThreadFactory implements ThreadFactory {
  private final ThreadFactory factory;
  
  private final String namePrefix;
  
  private final AtomicInteger threadIndex;
  
  private static final AtomicInteger POOL_INDEX = new AtomicInteger();
  
  public WorkerThreadFactory(String namePrefix) {
    this.factory = Executors.defaultThreadFactory();
    this
      
      .namePrefix = ((namePrefix != null && namePrefix.length() > 0) ? namePrefix : (getCallerSimpleClassName() + '-')) + POOL_INDEX.getAndIncrement() + '-';
    this.threadIndex = new AtomicInteger();
  }
  
  private static String getCallerSimpleClassName() {
    StackTraceElement[] stack = (new Exception()).getStackTrace();
    if (stack == null || stack.length <= 2)
      return "Worker-"; 
    String name = stack[2].getClassName();
    name = name.substring(name.lastIndexOf('.') + 1);
    return name;
  }
  
  public Thread newThread(Runnable r) {
    Thread thread = this.factory.newThread(r);
    thread.setName(this.namePrefix + this.threadIndex.getAndIncrement());
    thread.setDaemon(true);
    return thread;
  }
}
