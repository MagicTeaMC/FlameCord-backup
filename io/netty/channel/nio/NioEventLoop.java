package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public final class NioEventLoop extends SingleThreadEventLoop {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
  
  private static final int CLEANUP_INTERVAL = 256;
  
  private static final boolean DISABLE_KEY_SET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
  
  private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
  
  private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
  
  private final IntSupplier selectNowSupplier = new IntSupplier() {
      public int get() throws Exception {
        return NioEventLoop.this.selectNow();
      }
    };
  
  private Selector selector;
  
  private Selector unwrappedSelector;
  
  private SelectedSelectionKeySet selectedKeys;
  
  private final SelectorProvider provider;
  
  private static final long AWAKE = -1L;
  
  private static final long NONE = 9223372036854775807L;
  
  static {
    if (PlatformDependent.javaVersion() < 7) {
      String key = "sun.nio.ch.bugLevel";
      String bugLevel = SystemPropertyUtil.get("sun.nio.ch.bugLevel");
      if (bugLevel == null)
        try {
          AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                  System.setProperty("sun.nio.ch.bugLevel", "");
                  return null;
                }
              });
        } catch (SecurityException e) {
          logger.debug("Unable to get/set System Property: sun.nio.ch.bugLevel", e);
        }  
    } 
    int selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512);
    if (selectorAutoRebuildThreshold < 3)
      selectorAutoRebuildThreshold = 0; 
    SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
    if (logger.isDebugEnabled()) {
      logger.debug("-Dio.netty.noKeySetOptimization: {}", Boolean.valueOf(DISABLE_KEY_SET_OPTIMIZATION));
      logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", Integer.valueOf(SELECTOR_AUTO_REBUILD_THRESHOLD));
    } 
  }
  
  private final AtomicLong nextWakeupNanos = new AtomicLong(-1L);
  
  private final SelectStrategy selectStrategy;
  
  private volatile int ioRatio = 50;
  
  private int cancelledKeys;
  
  private boolean needsToSelectAgain;
  
  NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler, EventLoopTaskQueueFactory taskQueueFactory, EventLoopTaskQueueFactory tailTaskQueueFactory) {
    super((EventLoopGroup)parent, executor, false, newTaskQueue(taskQueueFactory), newTaskQueue(tailTaskQueueFactory), rejectedExecutionHandler);
    this.provider = (SelectorProvider)ObjectUtil.checkNotNull(selectorProvider, "selectorProvider");
    this.selectStrategy = (SelectStrategy)ObjectUtil.checkNotNull(strategy, "selectStrategy");
    SelectorTuple selectorTuple = openSelector();
    this.selector = selectorTuple.selector;
    this.unwrappedSelector = selectorTuple.unwrappedSelector;
  }
  
  private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
    if (queueFactory == null)
      return newTaskQueue0(DEFAULT_MAX_PENDING_TASKS); 
    return queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
  }
  
  private static final class SelectorTuple {
    final Selector unwrappedSelector;
    
    final Selector selector;
    
    SelectorTuple(Selector unwrappedSelector) {
      this.unwrappedSelector = unwrappedSelector;
      this.selector = unwrappedSelector;
    }
    
    SelectorTuple(Selector unwrappedSelector, Selector selector) {
      this.unwrappedSelector = unwrappedSelector;
      this.selector = selector;
    }
  }
  
  private SelectorTuple openSelector() {
    final Selector unwrappedSelector;
    try {
      unwrappedSelector = this.provider.openSelector();
    } catch (IOException e) {
      throw new ChannelException("failed to open a new selector", e);
    } 
    if (DISABLE_KEY_SET_OPTIMIZATION)
      return new SelectorTuple(unwrappedSelector); 
    Object maybeSelectorImplClass = AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            try {
              return Class.forName("sun.nio.ch.SelectorImpl", false, 
                  
                  PlatformDependent.getSystemClassLoader());
            } catch (Throwable cause) {
              return cause;
            } 
          }
        });
    if (!(maybeSelectorImplClass instanceof Class) || 
      
      !((Class)maybeSelectorImplClass).isAssignableFrom(unwrappedSelector.getClass())) {
      if (maybeSelectorImplClass instanceof Throwable) {
        Throwable t = (Throwable)maybeSelectorImplClass;
        logger.trace("failed to instrument a special java.util.Set into: {}", unwrappedSelector, t);
      } 
      return new SelectorTuple(unwrappedSelector);
    } 
    final Class<?> selectorImplClass = (Class)maybeSelectorImplClass;
    final SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
    Object maybeException = AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            try {
              Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
              Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
              if (PlatformDependent.javaVersion() >= 9 && PlatformDependent.hasUnsafe()) {
                long selectedKeysFieldOffset = PlatformDependent.objectFieldOffset(selectedKeysField);
                long publicSelectedKeysFieldOffset = PlatformDependent.objectFieldOffset(publicSelectedKeysField);
                if (selectedKeysFieldOffset != -1L && publicSelectedKeysFieldOffset != -1L) {
                  PlatformDependent.putObject(unwrappedSelector, selectedKeysFieldOffset, selectedKeySet);
                  PlatformDependent.putObject(unwrappedSelector, publicSelectedKeysFieldOffset, selectedKeySet);
                  return null;
                } 
              } 
              Throwable cause = ReflectionUtil.trySetAccessible(selectedKeysField, true);
              if (cause != null)
                return cause; 
              cause = ReflectionUtil.trySetAccessible(publicSelectedKeysField, true);
              if (cause != null)
                return cause; 
              selectedKeysField.set(unwrappedSelector, selectedKeySet);
              publicSelectedKeysField.set(unwrappedSelector, selectedKeySet);
              return null;
            } catch (NoSuchFieldException e) {
              return e;
            } catch (IllegalAccessException e) {
              return e;
            } 
          }
        });
    if (maybeException instanceof Exception) {
      this.selectedKeys = null;
      Exception e = (Exception)maybeException;
      logger.trace("failed to instrument a special java.util.Set into: {}", unwrappedSelector, e);
      return new SelectorTuple(unwrappedSelector);
    } 
    this.selectedKeys = selectedKeySet;
    logger.trace("instrumented a special java.util.Set into: {}", unwrappedSelector);
    return new SelectorTuple(unwrappedSelector, new SelectedSelectionKeySetSelector(unwrappedSelector, selectedKeySet));
  }
  
  public SelectorProvider selectorProvider() {
    return this.provider;
  }
  
  protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
    return newTaskQueue0(maxPendingTasks);
  }
  
  private static Queue<Runnable> newTaskQueue0(int maxPendingTasks) {
    return (maxPendingTasks == Integer.MAX_VALUE) ? PlatformDependent.newMpscQueue() : 
      PlatformDependent.newMpscQueue(maxPendingTasks);
  }
  
  public void register(final SelectableChannel ch, final int interestOps, final NioTask<?> task) {
    ObjectUtil.checkNotNull(ch, "ch");
    if (interestOps == 0)
      throw new IllegalArgumentException("interestOps must be non-zero."); 
    if ((interestOps & (ch.validOps() ^ 0xFFFFFFFF)) != 0)
      throw new IllegalArgumentException("invalid interestOps: " + interestOps + "(validOps: " + ch
          .validOps() + ')'); 
    ObjectUtil.checkNotNull(task, "task");
    if (isShutdown())
      throw new IllegalStateException("event loop shut down"); 
    if (inEventLoop()) {
      register0(ch, interestOps, task);
    } else {
      try {
        submit(new Runnable() {
              public void run() {
                NioEventLoop.this.register0(ch, interestOps, task);
              }
            }).sync();
      } catch (InterruptedException ignore) {
        Thread.currentThread().interrupt();
      } 
    } 
  }
  
  private void register0(SelectableChannel ch, int interestOps, NioTask<?> task) {
    try {
      ch.register(this.unwrappedSelector, interestOps, task);
    } catch (Exception e) {
      throw new EventLoopException("failed to register a channel", e);
    } 
  }
  
  public int getIoRatio() {
    return this.ioRatio;
  }
  
  public void setIoRatio(int ioRatio) {
    if (ioRatio <= 0 || ioRatio > 100)
      throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)"); 
    this.ioRatio = ioRatio;
  }
  
  public void rebuildSelector() {
    if (!inEventLoop()) {
      execute(new Runnable() {
            public void run() {
              NioEventLoop.this.rebuildSelector0();
            }
          });
      return;
    } 
    rebuildSelector0();
  }
  
  public int registeredChannels() {
    return this.selector.keys().size() - this.cancelledKeys;
  }
  
  public Iterator<Channel> registeredChannelsIterator() {
    assert inEventLoop();
    final Set<SelectionKey> keys = this.selector.keys();
    if (keys.isEmpty())
      return SingleThreadEventLoop.ChannelsReadOnlyIterator.empty(); 
    return new Iterator<Channel>() {
        final Iterator<SelectionKey> selectionKeyIterator = ((Set<SelectionKey>)ObjectUtil.checkNotNull(keys, "selectionKeys"))
          .iterator();
        
        Channel next;
        
        boolean isDone;
        
        public boolean hasNext() {
          if (this.isDone)
            return false; 
          Channel cur = this.next;
          if (cur == null) {
            cur = this.next = nextOrDone();
            return (cur != null);
          } 
          return true;
        }
        
        public Channel next() {
          if (this.isDone)
            throw new NoSuchElementException(); 
          Channel cur = this.next;
          if (cur == null) {
            cur = nextOrDone();
            if (cur == null)
              throw new NoSuchElementException(); 
          } 
          this.next = nextOrDone();
          return cur;
        }
        
        public void remove() {
          throw new UnsupportedOperationException("remove");
        }
        
        private Channel nextOrDone() {
          Iterator<SelectionKey> it = this.selectionKeyIterator;
          while (it.hasNext()) {
            SelectionKey key = it.next();
            if (key.isValid()) {
              Object attachment = key.attachment();
              if (attachment instanceof AbstractNioChannel)
                return (Channel)attachment; 
            } 
          } 
          this.isDone = true;
          return null;
        }
      };
  }
  
  private void rebuildSelector0() {
    SelectorTuple newSelectorTuple;
    Selector oldSelector = this.selector;
    if (oldSelector == null)
      return; 
    try {
      newSelectorTuple = openSelector();
    } catch (Exception e) {
      logger.warn("Failed to create a new Selector.", e);
      return;
    } 
    int nChannels = 0;
    for (SelectionKey key : oldSelector.keys()) {
      Object a = key.attachment();
      try {
        if (!key.isValid() || key.channel().keyFor(newSelectorTuple.unwrappedSelector) != null)
          continue; 
        int interestOps = key.interestOps();
        key.cancel();
        SelectionKey newKey = key.channel().register(newSelectorTuple.unwrappedSelector, interestOps, a);
        if (a instanceof AbstractNioChannel)
          ((AbstractNioChannel)a).selectionKey = newKey; 
        nChannels++;
      } catch (Exception e) {
        logger.warn("Failed to re-register a Channel to the new Selector.", e);
        if (a instanceof AbstractNioChannel) {
          AbstractNioChannel ch = (AbstractNioChannel)a;
          ch.unsafe().close(ch.unsafe().voidPromise());
          continue;
        } 
        NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
        invokeChannelUnregistered(task, key, e);
      } 
    } 
    this.selector = newSelectorTuple.selector;
    this.unwrappedSelector = newSelectorTuple.unwrappedSelector;
    try {
      oldSelector.close();
    } catch (Throwable t) {
      if (logger.isWarnEnabled())
        logger.warn("Failed to close the old Selector.", t); 
    } 
    if (logger.isInfoEnabled())
      logger.info("Migrated " + nChannels + " channel(s) to the new Selector."); 
  }
  
  protected void run() {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: getfield selectStrategy : Lio/netty/channel/SelectStrategy;
    //   6: aload_0
    //   7: getfield selectNowSupplier : Lio/netty/util/IntSupplier;
    //   10: aload_0
    //   11: invokevirtual hasTasks : ()Z
    //   14: invokeinterface calculateStrategy : (Lio/netty/util/IntSupplier;Z)I
    //   19: istore_2
    //   20: iload_2
    //   21: tableswitch default -> 147, -3 -> 81, -2 -> 48, -1 -> 81
    //   48: aload_0
    //   49: invokevirtual isShuttingDown : ()Z
    //   52: ifeq -> 67
    //   55: aload_0
    //   56: invokespecial closeAll : ()V
    //   59: aload_0
    //   60: invokevirtual confirmShutdown : ()Z
    //   63: ifeq -> 67
    //   66: return
    //   67: goto -> 2
    //   70: astore_3
    //   71: aload_3
    //   72: athrow
    //   73: astore_3
    //   74: aload_3
    //   75: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
    //   78: goto -> 2
    //   81: aload_0
    //   82: invokevirtual nextScheduledTaskDeadlineNanos : ()J
    //   85: lstore_3
    //   86: lload_3
    //   87: ldc2_w -1
    //   90: lcmp
    //   91: ifne -> 98
    //   94: ldc2_w 9223372036854775807
    //   97: lstore_3
    //   98: aload_0
    //   99: getfield nextWakeupNanos : Ljava/util/concurrent/atomic/AtomicLong;
    //   102: lload_3
    //   103: invokevirtual set : (J)V
    //   106: aload_0
    //   107: invokevirtual hasTasks : ()Z
    //   110: ifne -> 119
    //   113: aload_0
    //   114: lload_3
    //   115: invokespecial select : (J)I
    //   118: istore_2
    //   119: aload_0
    //   120: getfield nextWakeupNanos : Ljava/util/concurrent/atomic/AtomicLong;
    //   123: ldc2_w -1
    //   126: invokevirtual lazySet : (J)V
    //   129: goto -> 147
    //   132: astore #5
    //   134: aload_0
    //   135: getfield nextWakeupNanos : Ljava/util/concurrent/atomic/AtomicLong;
    //   138: ldc2_w -1
    //   141: invokevirtual lazySet : (J)V
    //   144: aload #5
    //   146: athrow
    //   147: goto -> 198
    //   150: astore_3
    //   151: aload_0
    //   152: invokespecial rebuildSelector0 : ()V
    //   155: iconst_0
    //   156: istore_1
    //   157: aload_3
    //   158: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
    //   161: aload_0
    //   162: invokevirtual isShuttingDown : ()Z
    //   165: ifeq -> 180
    //   168: aload_0
    //   169: invokespecial closeAll : ()V
    //   172: aload_0
    //   173: invokevirtual confirmShutdown : ()Z
    //   176: ifeq -> 180
    //   179: return
    //   180: goto -> 2
    //   183: astore #4
    //   185: aload #4
    //   187: athrow
    //   188: astore #4
    //   190: aload #4
    //   192: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
    //   195: goto -> 2
    //   198: iinc #1, 1
    //   201: aload_0
    //   202: iconst_0
    //   203: putfield cancelledKeys : I
    //   206: aload_0
    //   207: iconst_0
    //   208: putfield needsToSelectAgain : Z
    //   211: aload_0
    //   212: getfield ioRatio : I
    //   215: istore_3
    //   216: iload_3
    //   217: bipush #100
    //   219: if_icmpne -> 253
    //   222: iload_2
    //   223: ifle -> 230
    //   226: aload_0
    //   227: invokespecial processSelectedKeys : ()V
    //   230: aload_0
    //   231: invokevirtual runAllTasks : ()Z
    //   234: istore #4
    //   236: goto -> 250
    //   239: astore #6
    //   241: aload_0
    //   242: invokevirtual runAllTasks : ()Z
    //   245: istore #4
    //   247: aload #6
    //   249: athrow
    //   250: goto -> 334
    //   253: iload_2
    //   254: ifle -> 327
    //   257: invokestatic nanoTime : ()J
    //   260: lstore #5
    //   262: aload_0
    //   263: invokespecial processSelectedKeys : ()V
    //   266: invokestatic nanoTime : ()J
    //   269: lload #5
    //   271: lsub
    //   272: lstore #7
    //   274: aload_0
    //   275: lload #7
    //   277: bipush #100
    //   279: iload_3
    //   280: isub
    //   281: i2l
    //   282: lmul
    //   283: iload_3
    //   284: i2l
    //   285: ldiv
    //   286: invokevirtual runAllTasks : (J)Z
    //   289: istore #4
    //   291: goto -> 324
    //   294: astore #9
    //   296: invokestatic nanoTime : ()J
    //   299: lload #5
    //   301: lsub
    //   302: lstore #10
    //   304: aload_0
    //   305: lload #10
    //   307: bipush #100
    //   309: iload_3
    //   310: isub
    //   311: i2l
    //   312: lmul
    //   313: iload_3
    //   314: i2l
    //   315: ldiv
    //   316: invokevirtual runAllTasks : (J)Z
    //   319: istore #4
    //   321: aload #9
    //   323: athrow
    //   324: goto -> 334
    //   327: aload_0
    //   328: lconst_0
    //   329: invokevirtual runAllTasks : (J)Z
    //   332: istore #4
    //   334: iload #4
    //   336: ifne -> 343
    //   339: iload_2
    //   340: ifle -> 384
    //   343: iload_1
    //   344: iconst_3
    //   345: if_icmple -> 379
    //   348: getstatic io/netty/channel/nio/NioEventLoop.logger : Lio/netty/util/internal/logging/InternalLogger;
    //   351: invokeinterface isDebugEnabled : ()Z
    //   356: ifeq -> 379
    //   359: getstatic io/netty/channel/nio/NioEventLoop.logger : Lio/netty/util/internal/logging/InternalLogger;
    //   362: ldc 'Selector.select() returned prematurely {} times in a row for Selector {}.'
    //   364: iload_1
    //   365: iconst_1
    //   366: isub
    //   367: invokestatic valueOf : (I)Ljava/lang/Integer;
    //   370: aload_0
    //   371: getfield selector : Ljava/nio/channels/Selector;
    //   374: invokeinterface debug : (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   379: iconst_0
    //   380: istore_1
    //   381: goto -> 394
    //   384: aload_0
    //   385: iload_1
    //   386: invokespecial unexpectedSelectorWakeup : (I)Z
    //   389: ifeq -> 394
    //   392: iconst_0
    //   393: istore_1
    //   394: aload_0
    //   395: invokevirtual isShuttingDown : ()Z
    //   398: ifeq -> 413
    //   401: aload_0
    //   402: invokespecial closeAll : ()V
    //   405: aload_0
    //   406: invokevirtual confirmShutdown : ()Z
    //   409: ifeq -> 413
    //   412: return
    //   413: goto -> 588
    //   416: astore_2
    //   417: aload_2
    //   418: athrow
    //   419: astore_2
    //   420: aload_2
    //   421: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
    //   424: goto -> 588
    //   427: astore_2
    //   428: getstatic io/netty/channel/nio/NioEventLoop.logger : Lio/netty/util/internal/logging/InternalLogger;
    //   431: invokeinterface isDebugEnabled : ()Z
    //   436: ifeq -> 475
    //   439: getstatic io/netty/channel/nio/NioEventLoop.logger : Lio/netty/util/internal/logging/InternalLogger;
    //   442: new java/lang/StringBuilder
    //   445: dup
    //   446: invokespecial <init> : ()V
    //   449: ldc java/nio/channels/CancelledKeyException
    //   451: invokevirtual getSimpleName : ()Ljava/lang/String;
    //   454: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   457: ldc ' raised by a Selector {} - JDK bug?'
    //   459: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   462: invokevirtual toString : ()Ljava/lang/String;
    //   465: aload_0
    //   466: getfield selector : Ljava/nio/channels/Selector;
    //   469: aload_2
    //   470: invokeinterface debug : (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   475: aload_0
    //   476: invokevirtual isShuttingDown : ()Z
    //   479: ifeq -> 494
    //   482: aload_0
    //   483: invokespecial closeAll : ()V
    //   486: aload_0
    //   487: invokevirtual confirmShutdown : ()Z
    //   490: ifeq -> 494
    //   493: return
    //   494: goto -> 588
    //   497: astore_2
    //   498: aload_2
    //   499: athrow
    //   500: astore_2
    //   501: aload_2
    //   502: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
    //   505: goto -> 588
    //   508: astore_2
    //   509: aload_2
    //   510: athrow
    //   511: astore_2
    //   512: aload_2
    //   513: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
    //   516: aload_0
    //   517: invokevirtual isShuttingDown : ()Z
    //   520: ifeq -> 535
    //   523: aload_0
    //   524: invokespecial closeAll : ()V
    //   527: aload_0
    //   528: invokevirtual confirmShutdown : ()Z
    //   531: ifeq -> 535
    //   534: return
    //   535: goto -> 588
    //   538: astore_2
    //   539: aload_2
    //   540: athrow
    //   541: astore_2
    //   542: aload_2
    //   543: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
    //   546: goto -> 588
    //   549: astore #12
    //   551: aload_0
    //   552: invokevirtual isShuttingDown : ()Z
    //   555: ifeq -> 570
    //   558: aload_0
    //   559: invokespecial closeAll : ()V
    //   562: aload_0
    //   563: invokevirtual confirmShutdown : ()Z
    //   566: ifeq -> 570
    //   569: return
    //   570: goto -> 585
    //   573: astore #13
    //   575: aload #13
    //   577: athrow
    //   578: astore #13
    //   580: aload #13
    //   582: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
    //   585: aload #12
    //   587: athrow
    //   588: goto -> 2
    // Line number table:
    //   Java source line number -> byte code offset
    //   #505	-> 0
    //   #510	-> 2
    //   #511	-> 20
    //   #594	-> 48
    //   #595	-> 55
    //   #596	-> 59
    //   #597	-> 66
    //   #604	-> 67
    //   #600	-> 70
    //   #601	-> 71
    //   #602	-> 73
    //   #603	-> 74
    //   #604	-> 78
    //   #519	-> 81
    //   #520	-> 86
    //   #521	-> 94
    //   #523	-> 98
    //   #525	-> 106
    //   #526	-> 113
    //   #531	-> 119
    //   #532	-> 129
    //   #531	-> 132
    //   #532	-> 144
    //   #543	-> 147
    //   #536	-> 150
    //   #539	-> 151
    //   #540	-> 155
    //   #541	-> 157
    //   #594	-> 161
    //   #595	-> 168
    //   #596	-> 172
    //   #597	-> 179
    //   #604	-> 180
    //   #600	-> 183
    //   #601	-> 185
    //   #602	-> 188
    //   #603	-> 190
    //   #604	-> 195
    //   #545	-> 198
    //   #546	-> 201
    //   #547	-> 206
    //   #548	-> 211
    //   #550	-> 216
    //   #552	-> 222
    //   #553	-> 226
    //   #557	-> 230
    //   #558	-> 236
    //   #557	-> 239
    //   #558	-> 247
    //   #559	-> 253
    //   #560	-> 257
    //   #562	-> 262
    //   #565	-> 266
    //   #566	-> 274
    //   #567	-> 291
    //   #565	-> 294
    //   #566	-> 304
    //   #567	-> 321
    //   #568	-> 324
    //   #569	-> 327
    //   #572	-> 334
    //   #573	-> 343
    //   #574	-> 359
    //   #575	-> 367
    //   #574	-> 374
    //   #577	-> 379
    //   #578	-> 384
    //   #579	-> 392
    //   #594	-> 394
    //   #595	-> 401
    //   #596	-> 405
    //   #597	-> 412
    //   #604	-> 413
    //   #600	-> 416
    //   #601	-> 417
    //   #602	-> 419
    //   #603	-> 420
    //   #605	-> 424
    //   #581	-> 427
    //   #583	-> 428
    //   #584	-> 439
    //   #594	-> 475
    //   #595	-> 482
    //   #596	-> 486
    //   #597	-> 493
    //   #604	-> 494
    //   #600	-> 497
    //   #601	-> 498
    //   #602	-> 500
    //   #603	-> 501
    //   #605	-> 505
    //   #587	-> 508
    //   #588	-> 509
    //   #589	-> 511
    //   #590	-> 512
    //   #594	-> 516
    //   #595	-> 523
    //   #596	-> 527
    //   #597	-> 534
    //   #604	-> 535
    //   #600	-> 538
    //   #601	-> 539
    //   #602	-> 541
    //   #603	-> 542
    //   #605	-> 546
    //   #593	-> 549
    //   #594	-> 551
    //   #595	-> 558
    //   #596	-> 562
    //   #597	-> 569
    //   #604	-> 570
    //   #600	-> 573
    //   #601	-> 575
    //   #602	-> 578
    //   #603	-> 580
    //   #605	-> 585
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   71	2	3	e	Ljava/lang/Error;
    //   74	4	3	t	Ljava/lang/Throwable;
    //   86	61	3	curDeadlineNanos	J
    //   20	130	2	strategy	I
    //   185	3	4	e	Ljava/lang/Error;
    //   190	5	4	t	Ljava/lang/Throwable;
    //   151	47	3	e	Ljava/io/IOException;
    //   236	3	4	ranTasks	Z
    //   247	6	4	ranTasks	Z
    //   274	17	7	ioTime	J
    //   291	3	4	ranTasks	Z
    //   304	17	10	ioTime	J
    //   262	62	5	ioStartTime	J
    //   321	6	4	ranTasks	Z
    //   198	196	2	strategy	I
    //   216	178	3	ioRatio	I
    //   334	60	4	ranTasks	Z
    //   417	2	2	e	Ljava/lang/Error;
    //   420	4	2	t	Ljava/lang/Throwable;
    //   428	47	2	e	Ljava/nio/channels/CancelledKeyException;
    //   498	2	2	e	Ljava/lang/Error;
    //   501	4	2	t	Ljava/lang/Throwable;
    //   509	2	2	e	Ljava/lang/Error;
    //   512	4	2	t	Ljava/lang/Throwable;
    //   539	2	2	e	Ljava/lang/Error;
    //   542	4	2	t	Ljava/lang/Throwable;
    //   575	3	13	e	Ljava/lang/Error;
    //   580	5	13	t	Ljava/lang/Throwable;
    //   0	591	0	this	Lio/netty/channel/nio/NioEventLoop;
    //   2	589	1	selectCnt	I
    // Exception table:
    //   from	to	target	type
    //   2	48	150	java/io/IOException
    //   2	48	427	java/nio/channels/CancelledKeyException
    //   2	48	508	java/lang/Error
    //   2	48	511	java/lang/Throwable
    //   2	48	549	finally
    //   48	66	70	java/lang/Error
    //   48	66	73	java/lang/Throwable
    //   81	147	150	java/io/IOException
    //   81	161	427	java/nio/channels/CancelledKeyException
    //   81	161	508	java/lang/Error
    //   81	161	511	java/lang/Throwable
    //   81	161	549	finally
    //   106	119	132	finally
    //   132	134	132	finally
    //   161	179	183	java/lang/Error
    //   161	179	188	java/lang/Throwable
    //   198	394	427	java/nio/channels/CancelledKeyException
    //   198	394	508	java/lang/Error
    //   198	394	511	java/lang/Throwable
    //   198	394	549	finally
    //   222	230	239	finally
    //   239	241	239	finally
    //   262	266	294	finally
    //   294	296	294	finally
    //   394	412	416	java/lang/Error
    //   394	412	419	java/lang/Throwable
    //   427	475	549	finally
    //   475	493	497	java/lang/Error
    //   475	493	500	java/lang/Throwable
    //   508	516	549	finally
    //   516	534	538	java/lang/Error
    //   516	534	541	java/lang/Throwable
    //   549	551	549	finally
    //   551	569	573	java/lang/Error
    //   551	569	578	java/lang/Throwable
  }
  
  private boolean unexpectedSelectorWakeup(int selectCnt) {
    if (Thread.interrupted()) {
      if (logger.isDebugEnabled())
        logger.debug("Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop."); 
      return true;
    } 
    if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
      logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", 
          Integer.valueOf(selectCnt), this.selector);
      rebuildSelector();
      return true;
    } 
    return false;
  }
  
  private static void handleLoopException(Throwable t) {
    logger.warn("Unexpected exception in the selector loop.", t);
    try {
      Thread.sleep(1000L);
    } catch (InterruptedException interruptedException) {}
  }
  
  private void processSelectedKeys() {
    if (this.selectedKeys != null) {
      processSelectedKeysOptimized();
    } else {
      processSelectedKeysPlain(this.selector.selectedKeys());
    } 
  }
  
  protected void cleanup() {
    try {
      this.selector.close();
    } catch (IOException e) {
      logger.warn("Failed to close a selector.", e);
    } 
  }
  
  void cancel(SelectionKey key) {
    key.cancel();
    this.cancelledKeys++;
    if (this.cancelledKeys >= 256) {
      this.cancelledKeys = 0;
      this.needsToSelectAgain = true;
    } 
  }
  
  private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
    if (selectedKeys.isEmpty())
      return; 
    Iterator<SelectionKey> i = selectedKeys.iterator();
    while (true) {
      SelectionKey k = i.next();
      Object a = k.attachment();
      i.remove();
      if (a instanceof AbstractNioChannel) {
        processSelectedKey(k, (AbstractNioChannel)a);
      } else {
        NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
        processSelectedKey(k, task);
      } 
      if (!i.hasNext())
        break; 
      if (this.needsToSelectAgain) {
        selectAgain();
        selectedKeys = this.selector.selectedKeys();
        if (selectedKeys.isEmpty())
          break; 
        i = selectedKeys.iterator();
      } 
    } 
  }
  
  private void processSelectedKeysOptimized() {
    for (int i = 0; i < this.selectedKeys.size; i++) {
      SelectionKey k = this.selectedKeys.keys[i];
      this.selectedKeys.keys[i] = null;
      Object a = k.attachment();
      if (a instanceof AbstractNioChannel) {
        processSelectedKey(k, (AbstractNioChannel)a);
      } else {
        NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
        processSelectedKey(k, task);
      } 
      if (this.needsToSelectAgain) {
        this.selectedKeys.reset(i + 1);
        selectAgain();
        i = -1;
      } 
    } 
  }
  
  private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
    AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
    if (!k.isValid()) {
      NioEventLoop nioEventLoop;
      try {
        nioEventLoop = ch.eventLoop();
      } catch (Throwable ignored) {
        return;
      } 
      if (nioEventLoop == this)
        unsafe.close(unsafe.voidPromise()); 
      return;
    } 
    try {
      int readyOps = k.readyOps();
      if ((readyOps & 0x8) != 0) {
        int ops = k.interestOps();
        ops &= 0xFFFFFFF7;
        k.interestOps(ops);
        unsafe.finishConnect();
      } 
      if ((readyOps & 0x4) != 0)
        unsafe.forceFlush(); 
      if ((readyOps & 0x11) != 0 || readyOps == 0)
        unsafe.read(); 
    } catch (CancelledKeyException ignored) {
      EventLoop eventLoop;
      unsafe.close(unsafe.voidPromise());
    } 
  }
  
  private static void processSelectedKey(SelectionKey k, NioTask<SelectableChannel> task) {
    int state = 0;
    try {
      task.channelReady(k.channel(), k);
      state = 1;
    } catch (Exception e) {
      k.cancel();
      invokeChannelUnregistered(task, k, e);
      state = 2;
    } finally {
      switch (state) {
        case 0:
          k.cancel();
          invokeChannelUnregistered(task, k, (Throwable)null);
          break;
        case 1:
          if (!k.isValid())
            invokeChannelUnregistered(task, k, (Throwable)null); 
          break;
      } 
    } 
  }
  
  private void closeAll() {
    selectAgain();
    Set<SelectionKey> keys = this.selector.keys();
    Collection<AbstractNioChannel> channels = new ArrayList<AbstractNioChannel>(keys.size());
    for (SelectionKey k : keys) {
      Object a = k.attachment();
      if (a instanceof AbstractNioChannel) {
        channels.add((AbstractNioChannel)a);
        continue;
      } 
      k.cancel();
      NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
      invokeChannelUnregistered(task, k, (Throwable)null);
    } 
    for (AbstractNioChannel ch : channels)
      ch.unsafe().close(ch.unsafe().voidPromise()); 
  }
  
  private static void invokeChannelUnregistered(NioTask<SelectableChannel> task, SelectionKey k, Throwable cause) {
    try {
      task.channelUnregistered(k.channel(), cause);
    } catch (Exception e) {
      logger.warn("Unexpected exception while running NioTask.channelUnregistered()", e);
    } 
  }
  
  protected void wakeup(boolean inEventLoop) {
    if (!inEventLoop && this.nextWakeupNanos.getAndSet(-1L) != -1L)
      this.selector.wakeup(); 
  }
  
  protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
    return (deadlineNanos < this.nextWakeupNanos.get());
  }
  
  protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
    return (deadlineNanos < this.nextWakeupNanos.get());
  }
  
  Selector unwrappedSelector() {
    return this.unwrappedSelector;
  }
  
  int selectNow() throws IOException {
    return this.selector.selectNow();
  }
  
  private int select(long deadlineNanos) throws IOException {
    if (deadlineNanos == Long.MAX_VALUE)
      return this.selector.select(); 
    long timeoutMillis = deadlineToDelayNanos(deadlineNanos + 995000L) / 1000000L;
    return (timeoutMillis <= 0L) ? this.selector.selectNow() : this.selector.select(timeoutMillis);
  }
  
  private void selectAgain() {
    this.needsToSelectAgain = false;
    try {
      this.selector.selectNow();
    } catch (Throwable t) {
      logger.warn("Failed to update SelectionKeys.", t);
    } 
  }
}
