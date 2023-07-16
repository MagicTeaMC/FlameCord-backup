package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.util.IntSupplier;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

class EpollEventLoop extends SingleThreadEventLoop {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
  
  private static final long EPOLL_WAIT_MILLIS_THRESHOLD = SystemPropertyUtil.getLong("io.netty.channel.epoll.epollWaitThreshold", 10L);
  
  private final FileDescriptor epollFd;
  
  private final FileDescriptor eventFd;
  
  private final FileDescriptor timerFd;
  
  static {
    Epoll.ensureAvailability();
  }
  
  private final IntObjectMap<AbstractEpollChannel> channels = (IntObjectMap<AbstractEpollChannel>)new IntObjectHashMap(4096);
  
  private final boolean allowGrowing;
  
  private final EpollEventArray events;
  
  private IovArray iovArray;
  
  private NativeDatagramPacketArray datagramPacketArray;
  
  private final SelectStrategy selectStrategy;
  
  private final IntSupplier selectNowSupplier = new IntSupplier() {
      public int get() throws Exception {
        return EpollEventLoop.this.epollWaitNow();
      }
    };
  
  private static final long AWAKE = -1L;
  
  private static final long NONE = 9223372036854775807L;
  
  private final AtomicLong nextWakeupNanos = new AtomicLong(-1L);
  
  private boolean pendingWakeup;
  
  private volatile int ioRatio = 50;
  
  private static final long MAX_SCHEDULED_TIMERFD_NS = 999999999L;
  
  EpollEventLoop(EventLoopGroup parent, Executor executor, int maxEvents, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler, EventLoopTaskQueueFactory taskQueueFactory, EventLoopTaskQueueFactory tailTaskQueueFactory) {
    super(parent, executor, false, newTaskQueue(taskQueueFactory), newTaskQueue(tailTaskQueueFactory), rejectedExecutionHandler);
    this.selectStrategy = (SelectStrategy)ObjectUtil.checkNotNull(strategy, "strategy");
    if (maxEvents == 0) {
      this.allowGrowing = true;
      this.events = new EpollEventArray(4096);
    } else {
      this.allowGrowing = false;
      this.events = new EpollEventArray(maxEvents);
    } 
    boolean success = false;
    FileDescriptor epollFd = null;
    FileDescriptor eventFd = null;
    FileDescriptor timerFd = null;
    try {
      this.epollFd = epollFd = Native.newEpollCreate();
      this.eventFd = eventFd = Native.newEventFd();
      try {
        Native.epollCtlAdd(epollFd.intValue(), eventFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
      } catch (IOException e) {
        throw new IllegalStateException("Unable to add eventFd filedescriptor to epoll", e);
      } 
      this.timerFd = timerFd = Native.newTimerFd();
      try {
        Native.epollCtlAdd(epollFd.intValue(), timerFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
      } catch (IOException e) {
        throw new IllegalStateException("Unable to add timerFd filedescriptor to epoll", e);
      } 
      success = true;
    } finally {
      if (!success) {
        if (epollFd != null)
          try {
            epollFd.close();
          } catch (Exception exception) {} 
        if (eventFd != null)
          try {
            eventFd.close();
          } catch (Exception exception) {} 
        if (timerFd != null)
          try {
            timerFd.close();
          } catch (Exception exception) {} 
      } 
    } 
  }
  
  private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
    if (queueFactory == null)
      return newTaskQueue0(DEFAULT_MAX_PENDING_TASKS); 
    return queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
  }
  
  IovArray cleanIovArray() {
    if (this.iovArray == null) {
      this.iovArray = new IovArray();
    } else {
      this.iovArray.clear();
    } 
    return this.iovArray;
  }
  
  NativeDatagramPacketArray cleanDatagramPacketArray() {
    if (this.datagramPacketArray == null) {
      this.datagramPacketArray = new NativeDatagramPacketArray();
    } else {
      this.datagramPacketArray.clear();
    } 
    return this.datagramPacketArray;
  }
  
  protected void wakeup(boolean inEventLoop) {
    if (!inEventLoop && this.nextWakeupNanos.getAndSet(-1L) != -1L)
      Native.eventFdWrite(this.eventFd.intValue(), 1L); 
  }
  
  protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
    return (deadlineNanos < this.nextWakeupNanos.get());
  }
  
  protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
    return (deadlineNanos < this.nextWakeupNanos.get());
  }
  
  void add(AbstractEpollChannel ch) throws IOException {
    assert inEventLoop();
    int fd = ch.socket.intValue();
    Native.epollCtlAdd(this.epollFd.intValue(), fd, ch.flags);
    AbstractEpollChannel old = (AbstractEpollChannel)this.channels.put(fd, ch);
    assert old == null || !old.isOpen();
  }
  
  void modify(AbstractEpollChannel ch) throws IOException {
    assert inEventLoop();
    Native.epollCtlMod(this.epollFd.intValue(), ch.socket.intValue(), ch.flags);
  }
  
  void remove(AbstractEpollChannel ch) throws IOException {
    assert inEventLoop();
    int fd = ch.socket.intValue();
    AbstractEpollChannel old = (AbstractEpollChannel)this.channels.remove(fd);
    if (old != null && old != ch) {
      this.channels.put(fd, old);
      assert !ch.isOpen();
    } else if (ch.isOpen()) {
      Native.epollCtlDel(this.epollFd.intValue(), fd);
    } 
  }
  
  protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
    return newTaskQueue0(maxPendingTasks);
  }
  
  private static Queue<Runnable> newTaskQueue0(int maxPendingTasks) {
    return (maxPendingTasks == Integer.MAX_VALUE) ? PlatformDependent.newMpscQueue() : 
      PlatformDependent.newMpscQueue(maxPendingTasks);
  }
  
  public int getIoRatio() {
    return this.ioRatio;
  }
  
  public void setIoRatio(int ioRatio) {
    if (ioRatio <= 0 || ioRatio > 100)
      throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)"); 
    this.ioRatio = ioRatio;
  }
  
  public int registeredChannels() {
    return this.channels.size();
  }
  
  public Iterator<Channel> registeredChannelsIterator() {
    assert inEventLoop();
    IntObjectMap<AbstractEpollChannel> ch = this.channels;
    if (ch.isEmpty())
      return SingleThreadEventLoop.ChannelsReadOnlyIterator.empty(); 
    return (Iterator<Channel>)new SingleThreadEventLoop.ChannelsReadOnlyIterator(ch.values());
  }
  
  private long epollWait(long deadlineNanos) throws IOException {
    if (deadlineNanos == Long.MAX_VALUE)
      return Native.epollWait(this.epollFd, this.events, this.timerFd, 2147483647, 0, EPOLL_WAIT_MILLIS_THRESHOLD); 
    long totalDelay = deadlineToDelayNanos(deadlineNanos);
    int delaySeconds = (int)Math.min(totalDelay / 1000000000L, 2147483647L);
    int delayNanos = (int)Math.min(totalDelay - delaySeconds * 1000000000L, 999999999L);
    return Native.epollWait(this.epollFd, this.events, this.timerFd, delaySeconds, delayNanos, EPOLL_WAIT_MILLIS_THRESHOLD);
  }
  
  private int epollWaitNoTimerChange() throws IOException {
    return Native.epollWait(this.epollFd, this.events, false);
  }
  
  private int epollWaitNow() throws IOException {
    return Native.epollWait(this.epollFd, this.events, true);
  }
  
  private int epollBusyWait() throws IOException {
    return Native.epollBusyWait(this.epollFd, this.events);
  }
  
  private int epollWaitTimeboxed() throws IOException {
    return Native.epollWait(this.epollFd, this.events, 1000);
  }
  
  protected void run() {
    long prevDeadlineNanos = Long.MAX_VALUE;
    while (true) {
      try {
        long curDeadlineNanos;
        int strategy = this.selectStrategy.calculateStrategy(this.selectNowSupplier, hasTasks());
        switch (strategy) {
          case -2:
            try {
              if (isShuttingDown()) {
                closeAll();
                if (confirmShutdown())
                  break; 
              } 
            } catch (Error e) {
              throw e;
            } catch (Throwable t) {
              handleLoopException(t);
            } 
            continue;
          case -3:
            strategy = epollBusyWait();
            break;
          case -1:
            if (this.pendingWakeup) {
              strategy = epollWaitTimeboxed();
              if (strategy != 0)
                break; 
              logger.warn("Missed eventfd write (not seen after > 1 second)");
              this.pendingWakeup = false;
              if (hasTasks())
                break; 
            } 
            curDeadlineNanos = nextScheduledTaskDeadlineNanos();
            if (curDeadlineNanos == -1L)
              curDeadlineNanos = Long.MAX_VALUE; 
            this.nextWakeupNanos.set(curDeadlineNanos);
            try {
              if (!hasTasks())
                if (curDeadlineNanos == prevDeadlineNanos) {
                  strategy = epollWaitNoTimerChange();
                } else {
                  long result = epollWait(curDeadlineNanos);
                  strategy = Native.epollReady(result);
                  prevDeadlineNanos = Native.epollTimerWasUsed(result) ? curDeadlineNanos : Long.MAX_VALUE;
                }  
            } finally {
              if (this.nextWakeupNanos.get() == -1L || this.nextWakeupNanos.getAndSet(-1L) == -1L)
                this.pendingWakeup = true; 
            } 
            break;
        } 
        int ioRatio = this.ioRatio;
        if (ioRatio == 100) {
          try {
            if (strategy > 0 && processReady(this.events, strategy))
              prevDeadlineNanos = Long.MAX_VALUE; 
          } finally {
            runAllTasks();
          } 
        } else if (strategy > 0) {
          long ioStartTime = System.nanoTime();
          try {
            if (processReady(this.events, strategy))
              prevDeadlineNanos = Long.MAX_VALUE; 
          } finally {
            long ioTime = System.nanoTime() - ioStartTime;
            runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
          } 
        } else {
          runAllTasks(0L);
        } 
        if (this.allowGrowing && strategy == this.events.length())
          this.events.increase(); 
      } catch (Error e) {
        throw e;
      } catch (Throwable t) {
        handleLoopException(t);
      } finally {
        try {
          if (isShuttingDown()) {
            closeAll();
            if (confirmShutdown())
              return; 
          } 
        } catch (Error e) {
          throw e;
        } catch (Throwable t) {
          handleLoopException(t);
        } 
      } 
    } 
  }
  
  void handleLoopException(Throwable t) {
    logger.warn("Unexpected exception in the selector loop.", t);
    try {
      Thread.sleep(1000L);
    } catch (InterruptedException interruptedException) {}
  }
  
  private void closeAll() {
    AbstractEpollChannel[] localChannels = (AbstractEpollChannel[])this.channels.values().toArray((Object[])new AbstractEpollChannel[0]);
    for (AbstractEpollChannel ch : localChannels)
      ch.unsafe().close(ch.unsafe().voidPromise()); 
  }
  
  private boolean processReady(EpollEventArray events, int ready) {
    boolean timerFired = false;
    for (int i = 0; i < ready; i++) {
      int fd = events.fd(i);
      if (fd == this.eventFd.intValue()) {
        this.pendingWakeup = false;
      } else if (fd == this.timerFd.intValue()) {
        timerFired = true;
      } else {
        long ev = events.events(i);
        AbstractEpollChannel ch = (AbstractEpollChannel)this.channels.get(fd);
        if (ch != null) {
          AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)ch.unsafe();
          if ((ev & (Native.EPOLLERR | Native.EPOLLOUT)) != 0L)
            unsafe.epollOutReady(); 
          if ((ev & (Native.EPOLLERR | Native.EPOLLIN)) != 0L)
            unsafe.epollInReady(); 
          if ((ev & Native.EPOLLRDHUP) != 0L)
            unsafe.epollRdHupReady(); 
        } else {
          try {
            Native.epollCtlDel(this.epollFd.intValue(), fd);
          } catch (IOException iOException) {}
        } 
      } 
    } 
    return timerFired;
  }
  
  protected void cleanup() {
    try {
      while (this.pendingWakeup) {
        try {
          int count = epollWaitTimeboxed();
          if (count == 0)
            break; 
          for (int i = 0; i < count; i++) {
            if (this.events.fd(i) == this.eventFd.intValue()) {
              this.pendingWakeup = false;
              break;
            } 
          } 
        } catch (IOException iOException) {}
      } 
      try {
        this.eventFd.close();
      } catch (IOException e) {
        logger.warn("Failed to close the event fd.", e);
      } 
      try {
        this.timerFd.close();
      } catch (IOException e) {
        logger.warn("Failed to close the timer fd.", e);
      } 
      try {
        this.epollFd.close();
      } catch (IOException e) {
        logger.warn("Failed to close the epoll fd.", e);
      } 
    } finally {
      if (this.iovArray != null) {
        this.iovArray.release();
        this.iovArray = null;
      } 
      if (this.datagramPacketArray != null) {
        this.datagramPacketArray.release();
        this.datagramPacketArray = null;
      } 
      this.events.free();
    } 
  }
}
