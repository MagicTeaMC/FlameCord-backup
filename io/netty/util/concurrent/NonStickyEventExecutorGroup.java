package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class NonStickyEventExecutorGroup implements EventExecutorGroup {
  private final EventExecutorGroup group;
  
  private final int maxTaskExecutePerRun;
  
  public NonStickyEventExecutorGroup(EventExecutorGroup group) {
    this(group, 1024);
  }
  
  public NonStickyEventExecutorGroup(EventExecutorGroup group, int maxTaskExecutePerRun) {
    this.group = verify(group);
    this.maxTaskExecutePerRun = ObjectUtil.checkPositive(maxTaskExecutePerRun, "maxTaskExecutePerRun");
  }
  
  private static EventExecutorGroup verify(EventExecutorGroup group) {
    Iterator<EventExecutor> executors = ((EventExecutorGroup)ObjectUtil.checkNotNull(group, "group")).iterator();
    while (executors.hasNext()) {
      EventExecutor executor = executors.next();
      if (executor instanceof OrderedEventExecutor)
        throw new IllegalArgumentException("EventExecutorGroup " + group + " contains OrderedEventExecutors: " + executor); 
    } 
    return group;
  }
  
  private NonStickyOrderedEventExecutor newExecutor(EventExecutor executor) {
    return new NonStickyOrderedEventExecutor(executor, this.maxTaskExecutePerRun);
  }
  
  public boolean isShuttingDown() {
    return this.group.isShuttingDown();
  }
  
  public Future<?> shutdownGracefully() {
    return this.group.shutdownGracefully();
  }
  
  public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
    return this.group.shutdownGracefully(quietPeriod, timeout, unit);
  }
  
  public Future<?> terminationFuture() {
    return this.group.terminationFuture();
  }
  
  public void shutdown() {
    this.group.shutdown();
  }
  
  public List<Runnable> shutdownNow() {
    return this.group.shutdownNow();
  }
  
  public EventExecutor next() {
    return newExecutor(this.group.next());
  }
  
  public Iterator<EventExecutor> iterator() {
    final Iterator<EventExecutor> itr = this.group.iterator();
    return new Iterator<EventExecutor>() {
        public boolean hasNext() {
          return itr.hasNext();
        }
        
        public EventExecutor next() {
          return NonStickyEventExecutorGroup.this.newExecutor(itr.next());
        }
        
        public void remove() {
          itr.remove();
        }
      };
  }
  
  public Future<?> submit(Runnable task) {
    return this.group.submit(task);
  }
  
  public <T> Future<T> submit(Runnable task, T result) {
    return this.group.submit(task, result);
  }
  
  public <T> Future<T> submit(Callable<T> task) {
    return this.group.submit(task);
  }
  
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return this.group.schedule(command, delay, unit);
  }
  
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    return this.group.schedule(callable, delay, unit);
  }
  
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return this.group.scheduleAtFixedRate(command, initialDelay, period, unit);
  }
  
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return this.group.scheduleWithFixedDelay(command, initialDelay, delay, unit);
  }
  
  public boolean isShutdown() {
    return this.group.isShutdown();
  }
  
  public boolean isTerminated() {
    return this.group.isTerminated();
  }
  
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return this.group.awaitTermination(timeout, unit);
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return this.group.invokeAll(tasks);
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    return this.group.invokeAll(tasks, timeout, unit);
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    return this.group.invokeAny(tasks);
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return this.group.invokeAny(tasks, timeout, unit);
  }
  
  public void execute(Runnable command) {
    this.group.execute(command);
  }
  
  private static final class NonStickyOrderedEventExecutor extends AbstractEventExecutor implements Runnable, OrderedEventExecutor {
    private final EventExecutor executor;
    
    private final Queue<Runnable> tasks = PlatformDependent.newMpscQueue();
    
    private static final int NONE = 0;
    
    private static final int SUBMITTED = 1;
    
    private static final int RUNNING = 2;
    
    private final AtomicInteger state = new AtomicInteger();
    
    private final int maxTaskExecutePerRun;
    
    private final AtomicReference<Thread> executingThread = new AtomicReference<Thread>();
    
    NonStickyOrderedEventExecutor(EventExecutor executor, int maxTaskExecutePerRun) {
      super(executor);
      this.executor = executor;
      this.maxTaskExecutePerRun = maxTaskExecutePerRun;
    }
    
    public void run() {
      // Byte code:
      //   0: aload_0
      //   1: getfield state : Ljava/util/concurrent/atomic/AtomicInteger;
      //   4: iconst_1
      //   5: iconst_2
      //   6: invokevirtual compareAndSet : (II)Z
      //   9: ifne -> 13
      //   12: return
      //   13: invokestatic currentThread : ()Ljava/lang/Thread;
      //   16: astore_1
      //   17: aload_0
      //   18: getfield executingThread : Ljava/util/concurrent/atomic/AtomicReference;
      //   21: aload_1
      //   22: invokevirtual set : (Ljava/lang/Object;)V
      //   25: iconst_0
      //   26: istore_2
      //   27: iload_2
      //   28: aload_0
      //   29: getfield maxTaskExecutePerRun : I
      //   32: if_icmpge -> 65
      //   35: aload_0
      //   36: getfield tasks : Ljava/util/Queue;
      //   39: invokeinterface poll : ()Ljava/lang/Object;
      //   44: checkcast java/lang/Runnable
      //   47: astore_3
      //   48: aload_3
      //   49: ifnonnull -> 55
      //   52: goto -> 65
      //   55: aload_3
      //   56: invokestatic safeExecute : (Ljava/lang/Runnable;)V
      //   59: iinc #2, 1
      //   62: goto -> 27
      //   65: iload_2
      //   66: aload_0
      //   67: getfield maxTaskExecutePerRun : I
      //   70: if_icmpne -> 114
      //   73: aload_0
      //   74: getfield state : Ljava/util/concurrent/atomic/AtomicInteger;
      //   77: iconst_1
      //   78: invokevirtual set : (I)V
      //   81: aload_0
      //   82: getfield executingThread : Ljava/util/concurrent/atomic/AtomicReference;
      //   85: aload_1
      //   86: aconst_null
      //   87: invokevirtual compareAndSet : (Ljava/lang/Object;Ljava/lang/Object;)Z
      //   90: pop
      //   91: aload_0
      //   92: getfield executor : Lio/netty/util/concurrent/EventExecutor;
      //   95: aload_0
      //   96: invokeinterface execute : (Ljava/lang/Runnable;)V
      //   101: return
      //   102: astore_3
      //   103: aload_0
      //   104: getfield state : Ljava/util/concurrent/atomic/AtomicInteger;
      //   107: iconst_2
      //   108: invokevirtual set : (I)V
      //   111: goto -> 255
      //   114: aload_0
      //   115: getfield state : Ljava/util/concurrent/atomic/AtomicInteger;
      //   118: iconst_0
      //   119: invokevirtual set : (I)V
      //   122: aload_0
      //   123: getfield tasks : Ljava/util/Queue;
      //   126: invokeinterface isEmpty : ()Z
      //   131: ifne -> 146
      //   134: aload_0
      //   135: getfield state : Ljava/util/concurrent/atomic/AtomicInteger;
      //   138: iconst_0
      //   139: iconst_2
      //   140: invokevirtual compareAndSet : (II)Z
      //   143: ifne -> 255
      //   146: aload_0
      //   147: getfield executingThread : Ljava/util/concurrent/atomic/AtomicReference;
      //   150: aload_1
      //   151: aconst_null
      //   152: invokevirtual compareAndSet : (Ljava/lang/Object;Ljava/lang/Object;)Z
      //   155: pop
      //   156: return
      //   157: astore #4
      //   159: iload_2
      //   160: aload_0
      //   161: getfield maxTaskExecutePerRun : I
      //   164: if_icmpne -> 209
      //   167: aload_0
      //   168: getfield state : Ljava/util/concurrent/atomic/AtomicInteger;
      //   171: iconst_1
      //   172: invokevirtual set : (I)V
      //   175: aload_0
      //   176: getfield executingThread : Ljava/util/concurrent/atomic/AtomicReference;
      //   179: aload_1
      //   180: aconst_null
      //   181: invokevirtual compareAndSet : (Ljava/lang/Object;Ljava/lang/Object;)Z
      //   184: pop
      //   185: aload_0
      //   186: getfield executor : Lio/netty/util/concurrent/EventExecutor;
      //   189: aload_0
      //   190: invokeinterface execute : (Ljava/lang/Runnable;)V
      //   195: return
      //   196: astore #5
      //   198: aload_0
      //   199: getfield state : Ljava/util/concurrent/atomic/AtomicInteger;
      //   202: iconst_2
      //   203: invokevirtual set : (I)V
      //   206: goto -> 252
      //   209: aload_0
      //   210: getfield state : Ljava/util/concurrent/atomic/AtomicInteger;
      //   213: iconst_0
      //   214: invokevirtual set : (I)V
      //   217: aload_0
      //   218: getfield tasks : Ljava/util/Queue;
      //   221: invokeinterface isEmpty : ()Z
      //   226: ifne -> 241
      //   229: aload_0
      //   230: getfield state : Ljava/util/concurrent/atomic/AtomicInteger;
      //   233: iconst_0
      //   234: iconst_2
      //   235: invokevirtual compareAndSet : (II)Z
      //   238: ifne -> 252
      //   241: aload_0
      //   242: getfield executingThread : Ljava/util/concurrent/atomic/AtomicReference;
      //   245: aload_1
      //   246: aconst_null
      //   247: invokevirtual compareAndSet : (Ljava/lang/Object;Ljava/lang/Object;)Z
      //   250: pop
      //   251: return
      //   252: aload #4
      //   254: athrow
      //   255: goto -> 25
      // Line number table:
      //   Java source line number -> byte code offset
      //   #237	-> 0
      //   #238	-> 12
      //   #240	-> 13
      //   #241	-> 17
      //   #243	-> 25
      //   #245	-> 27
      //   #246	-> 35
      //   #247	-> 48
      //   #248	-> 52
      //   #250	-> 55
      //   #245	-> 59
      //   #253	-> 65
      //   #255	-> 73
      //   #257	-> 81
      //   #258	-> 91
      //   #259	-> 101
      //   #260	-> 102
      //   #262	-> 103
      //   #266	-> 111
      //   #268	-> 114
      //   #284	-> 122
      //   #286	-> 146
      //   #287	-> 156
      //   #253	-> 157
      //   #255	-> 167
      //   #257	-> 175
      //   #258	-> 185
      //   #259	-> 195
      //   #260	-> 196
      //   #262	-> 198
      //   #266	-> 206
      //   #268	-> 209
      //   #284	-> 217
      //   #286	-> 241
      //   #287	-> 251
      //   #290	-> 252
      //   #291	-> 255
      // Local variable table:
      //   start	length	slot	name	descriptor
      //   48	11	3	task	Ljava/lang/Runnable;
      //   103	8	3	ignore	Ljava/lang/Throwable;
      //   198	8	5	ignore	Ljava/lang/Throwable;
      //   27	228	2	i	I
      //   0	258	0	this	Lio/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor;
      //   17	241	1	current	Ljava/lang/Thread;
      // Exception table:
      //   from	to	target	type
      //   27	65	157	finally
      //   73	101	102	java/lang/Throwable
      //   157	159	157	finally
      //   167	195	196	java/lang/Throwable
    }
    
    public boolean inEventLoop(Thread thread) {
      return (this.executingThread.get() == thread);
    }
    
    public boolean isShuttingDown() {
      return this.executor.isShutdown();
    }
    
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      return this.executor.shutdownGracefully(quietPeriod, timeout, unit);
    }
    
    public Future<?> terminationFuture() {
      return this.executor.terminationFuture();
    }
    
    public void shutdown() {
      this.executor.shutdown();
    }
    
    public boolean isShutdown() {
      return this.executor.isShutdown();
    }
    
    public boolean isTerminated() {
      return this.executor.isTerminated();
    }
    
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return this.executor.awaitTermination(timeout, unit);
    }
    
    public void execute(Runnable command) {
      if (!this.tasks.offer(command))
        throw new RejectedExecutionException(); 
      if (this.state.compareAndSet(0, 1))
        this.executor.execute(this); 
    }
  }
}
