package io.netty.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.jetbrains.annotations.VisibleForTesting;

public abstract class Recycler<T> {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(Recycler.class);
  
  private static final EnhancedHandle<?> NOOP_HANDLE = new EnhancedHandle() {
      public void recycle(Object object) {}
      
      public void unguardedRecycle(Object object) {}
      
      public String toString() {
        return "NOOP_HANDLE";
      }
    };
  
  private static final int DEFAULT_INITIAL_MAX_CAPACITY_PER_THREAD = 4096;
  
  private static final int DEFAULT_MAX_CAPACITY_PER_THREAD;
  
  private static final int RATIO;
  
  static {
    int maxCapacityPerThread = SystemPropertyUtil.getInt("io.netty.recycler.maxCapacityPerThread", 
        SystemPropertyUtil.getInt("io.netty.recycler.maxCapacity", 4096));
    if (maxCapacityPerThread < 0)
      maxCapacityPerThread = 4096; 
    DEFAULT_MAX_CAPACITY_PER_THREAD = maxCapacityPerThread;
  }
  
  private static final int DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD = SystemPropertyUtil.getInt("io.netty.recycler.chunkSize", 32);
  
  private static final boolean BLOCKING_POOL;
  
  private static final boolean BATCH_FAST_TL_ONLY;
  
  private final int maxCapacityPerThread;
  
  private final int interval;
  
  private final int chunkSize;
  
  static {
    RATIO = Math.max(0, SystemPropertyUtil.getInt("io.netty.recycler.ratio", 8));
    BLOCKING_POOL = SystemPropertyUtil.getBoolean("io.netty.recycler.blocking", false);
    BATCH_FAST_TL_ONLY = SystemPropertyUtil.getBoolean("io.netty.recycler.batchFastThreadLocalOnly", true);
    if (logger.isDebugEnabled())
      if (DEFAULT_MAX_CAPACITY_PER_THREAD == 0) {
        logger.debug("-Dio.netty.recycler.maxCapacityPerThread: disabled");
        logger.debug("-Dio.netty.recycler.ratio: disabled");
        logger.debug("-Dio.netty.recycler.chunkSize: disabled");
        logger.debug("-Dio.netty.recycler.blocking: disabled");
        logger.debug("-Dio.netty.recycler.batchFastThreadLocalOnly: disabled");
      } else {
        logger.debug("-Dio.netty.recycler.maxCapacityPerThread: {}", Integer.valueOf(DEFAULT_MAX_CAPACITY_PER_THREAD));
        logger.debug("-Dio.netty.recycler.ratio: {}", Integer.valueOf(RATIO));
        logger.debug("-Dio.netty.recycler.chunkSize: {}", Integer.valueOf(DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD));
        logger.debug("-Dio.netty.recycler.blocking: {}", Boolean.valueOf(BLOCKING_POOL));
        logger.debug("-Dio.netty.recycler.batchFastThreadLocalOnly: {}", Boolean.valueOf(BATCH_FAST_TL_ONLY));
      }  
  }
  
  private final FastThreadLocal<LocalPool<T>> threadLocal = new FastThreadLocal<LocalPool<T>>() {
      protected Recycler.LocalPool<T> initialValue() {
        return new Recycler.LocalPool<T>(Recycler.this.maxCapacityPerThread, Recycler.this.interval, Recycler.this.chunkSize);
      }
      
      protected void onRemoval(Recycler.LocalPool<T> value) throws Exception {
        super.onRemoval(value);
        MessagePassingQueue<Recycler.DefaultHandle<T>> handles = value.pooledHandles;
        value.pooledHandles = null;
        value.owner = null;
        handles.clear();
      }
    };
  
  protected Recycler() {
    this(DEFAULT_MAX_CAPACITY_PER_THREAD);
  }
  
  protected Recycler(int maxCapacityPerThread) {
    this(maxCapacityPerThread, RATIO, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
  }
  
  @Deprecated
  protected Recycler(int maxCapacityPerThread, int maxSharedCapacityFactor) {
    this(maxCapacityPerThread, RATIO, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
  }
  
  @Deprecated
  protected Recycler(int maxCapacityPerThread, int maxSharedCapacityFactor, int ratio, int maxDelayedQueuesPerThread) {
    this(maxCapacityPerThread, ratio, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
  }
  
  @Deprecated
  protected Recycler(int maxCapacityPerThread, int maxSharedCapacityFactor, int ratio, int maxDelayedQueuesPerThread, int delayedQueueRatio) {
    this(maxCapacityPerThread, ratio, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
  }
  
  protected Recycler(int maxCapacityPerThread, int ratio, int chunkSize) {
    this.interval = Math.max(0, ratio);
    if (maxCapacityPerThread <= 0) {
      this.maxCapacityPerThread = 0;
      this.chunkSize = 0;
    } else {
      this.maxCapacityPerThread = Math.max(4, maxCapacityPerThread);
      this.chunkSize = Math.max(2, Math.min(chunkSize, this.maxCapacityPerThread >> 1));
    } 
  }
  
  public final T get() {
    T obj;
    if (this.maxCapacityPerThread == 0)
      return newObject((Handle)NOOP_HANDLE); 
    LocalPool<T> localPool = (LocalPool<T>)this.threadLocal.get();
    DefaultHandle<T> handle = localPool.claim();
    if (handle == null) {
      handle = localPool.newHandle();
      if (handle != null) {
        obj = newObject(handle);
        handle.set(obj);
      } else {
        obj = newObject((Handle)NOOP_HANDLE);
      } 
    } else {
      obj = handle.get();
    } 
    return obj;
  }
  
  @Deprecated
  public final boolean recycle(T o, Handle<T> handle) {
    if (handle == NOOP_HANDLE)
      return false; 
    handle.recycle(o);
    return true;
  }
  
  @VisibleForTesting
  final int threadLocalSize() {
    LocalPool<T> localPool = (LocalPool<T>)this.threadLocal.getIfExists();
    return (localPool == null) ? 0 : (localPool.pooledHandles.size() + localPool.batch.size());
  }
  
  protected abstract T newObject(Handle<T> paramHandle);
  
  public static interface Handle<T> extends ObjectPool.Handle<T> {}
  
  public static abstract class EnhancedHandle<T> implements Handle<T> {
    private EnhancedHandle() {}
    
    public abstract void unguardedRecycle(Object param1Object);
  }
  
  private static final class DefaultHandle<T> extends EnhancedHandle<T> {
    private static final int STATE_CLAIMED = 0;
    
    private static final int STATE_AVAILABLE = 1;
    
    private static final AtomicIntegerFieldUpdater<DefaultHandle<?>> STATE_UPDATER;
    
    private volatile int state;
    
    private final Recycler.LocalPool<T> localPool;
    
    private T value;
    
    static {
      AtomicIntegerFieldUpdater<?> updater = AtomicIntegerFieldUpdater.newUpdater(DefaultHandle.class, "state");
      STATE_UPDATER = (AtomicIntegerFieldUpdater)updater;
    }
    
    DefaultHandle(Recycler.LocalPool<T> localPool) {
      this.localPool = localPool;
    }
    
    public void recycle(Object object) {
      if (object != this.value)
        throw new IllegalArgumentException("object does not belong to handle"); 
      this.localPool.release(this, true);
    }
    
    public void unguardedRecycle(Object object) {
      if (object != this.value)
        throw new IllegalArgumentException("object does not belong to handle"); 
      this.localPool.release(this, false);
    }
    
    T get() {
      return this.value;
    }
    
    void set(T value) {
      this.value = value;
    }
    
    void toClaimed() {
      assert this.state == 1;
      STATE_UPDATER.lazySet(this, 0);
    }
    
    void toAvailable() {
      int prev = STATE_UPDATER.getAndSet(this, 1);
      if (prev == 1)
        throw new IllegalStateException("Object has been recycled already."); 
    }
    
    void unguardedToAvailable() {
      int prev = this.state;
      if (prev == 1)
        throw new IllegalStateException("Object has been recycled already."); 
      STATE_UPDATER.lazySet(this, 1);
    }
  }
  
  private static final class LocalPool<T> implements MessagePassingQueue.Consumer<DefaultHandle<T>> {
    private final int ratioInterval;
    
    private final int chunkSize;
    
    private final ArrayDeque<Recycler.DefaultHandle<T>> batch;
    
    private volatile Thread owner;
    
    private volatile MessagePassingQueue<Recycler.DefaultHandle<T>> pooledHandles;
    
    private int ratioCounter;
    
    LocalPool(int maxCapacity, int ratioInterval, int chunkSize) {
      this.ratioInterval = ratioInterval;
      this.chunkSize = chunkSize;
      this.batch = new ArrayDeque<Recycler.DefaultHandle<T>>(chunkSize);
      Thread currentThread = Thread.currentThread();
      this.owner = (!Recycler.BATCH_FAST_TL_ONLY || currentThread instanceof io.netty.util.concurrent.FastThreadLocalThread) ? currentThread : null;
      if (Recycler.BLOCKING_POOL) {
        this.pooledHandles = new Recycler.BlockingMessageQueue<Recycler.DefaultHandle<T>>(maxCapacity);
      } else {
        this.pooledHandles = (MessagePassingQueue<Recycler.DefaultHandle<T>>)PlatformDependent.newMpscQueue(chunkSize, maxCapacity);
      } 
      this.ratioCounter = ratioInterval;
    }
    
    Recycler.DefaultHandle<T> claim() {
      MessagePassingQueue<Recycler.DefaultHandle<T>> handles = this.pooledHandles;
      if (handles == null)
        return null; 
      if (this.batch.isEmpty())
        handles.drain(this, this.chunkSize); 
      Recycler.DefaultHandle<T> handle = this.batch.pollFirst();
      if (null != handle)
        handle.toClaimed(); 
      return handle;
    }
    
    void release(Recycler.DefaultHandle<T> handle, boolean guarded) {
      if (guarded) {
        handle.toAvailable();
      } else {
        handle.unguardedToAvailable();
      } 
      Thread owner = this.owner;
      if (owner != null && Thread.currentThread() == owner && this.batch.size() < this.chunkSize) {
        accept(handle);
      } else if (owner != null && isTerminated(owner)) {
        this.owner = null;
        this.pooledHandles = null;
      } else {
        MessagePassingQueue<Recycler.DefaultHandle<T>> handles = this.pooledHandles;
        if (handles != null)
          handles.relaxedOffer(handle); 
      } 
    }
    
    private static boolean isTerminated(Thread owner) {
      return PlatformDependent.isJ9Jvm() ? (!owner.isAlive()) : ((owner.getState() == Thread.State.TERMINATED));
    }
    
    Recycler.DefaultHandle<T> newHandle() {
      if (++this.ratioCounter >= this.ratioInterval) {
        this.ratioCounter = 0;
        return new Recycler.DefaultHandle<T>(this);
      } 
      return null;
    }
    
    public void accept(Recycler.DefaultHandle<T> e) {
      this.batch.addLast(e);
    }
  }
  
  private static final class BlockingMessageQueue<T> implements MessagePassingQueue<T> {
    private final Queue<T> deque;
    
    private final int maxCapacity;
    
    BlockingMessageQueue(int maxCapacity) {
      this.maxCapacity = maxCapacity;
      this.deque = new ArrayDeque<T>();
    }
    
    public synchronized boolean offer(T e) {
      if (this.deque.size() == this.maxCapacity)
        return false; 
      return this.deque.offer(e);
    }
    
    public synchronized T poll() {
      return this.deque.poll();
    }
    
    public synchronized T peek() {
      return this.deque.peek();
    }
    
    public synchronized int size() {
      return this.deque.size();
    }
    
    public synchronized void clear() {
      this.deque.clear();
    }
    
    public synchronized boolean isEmpty() {
      return this.deque.isEmpty();
    }
    
    public int capacity() {
      return this.maxCapacity;
    }
    
    public boolean relaxedOffer(T e) {
      return offer(e);
    }
    
    public T relaxedPoll() {
      return poll();
    }
    
    public T relaxedPeek() {
      return peek();
    }
    
    public int drain(MessagePassingQueue.Consumer<T> c, int limit) {
      int i = 0;
      T obj;
      for (; i < limit && (obj = poll()) != null; i++)
        c.accept(obj); 
      return i;
    }
    
    public int fill(MessagePassingQueue.Supplier<T> s, int limit) {
      throw new UnsupportedOperationException();
    }
    
    public int drain(MessagePassingQueue.Consumer<T> c) {
      throw new UnsupportedOperationException();
    }
    
    public int fill(MessagePassingQueue.Supplier<T> s) {
      throw new UnsupportedOperationException();
    }
    
    public void drain(MessagePassingQueue.Consumer<T> c, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
      throw new UnsupportedOperationException();
    }
    
    public void fill(MessagePassingQueue.Supplier<T> s, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
      throw new UnsupportedOperationException();
    }
  }
}
