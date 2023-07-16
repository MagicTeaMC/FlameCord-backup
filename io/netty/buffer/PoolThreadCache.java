package io.netty.buffer;

import io.netty.util.Recycler;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

final class PoolThreadCache extends PoolArenasCache {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
  
  private final MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
  
  private final MemoryRegionCache<ByteBuffer>[] smallSubPageDirectCaches;
  
  private final MemoryRegionCache<byte[]>[] normalHeapCaches;
  
  private final MemoryRegionCache<ByteBuffer>[] normalDirectCaches;
  
  private final int freeSweepAllocationThreshold;
  
  private final AtomicBoolean freed = new AtomicBoolean();
  
  private int allocations;
  
  PoolThreadCache(PoolArena<byte[]> heapArena, PoolArena<ByteBuffer> directArena, int smallCacheSize, int normalCacheSize, int maxCachedBufferCapacity, int freeSweepAllocationThreshold) {
    super(heapArena, directArena);
    ObjectUtil.checkPositiveOrZero(maxCachedBufferCapacity, "maxCachedBufferCapacity");
    this.freeSweepAllocationThreshold = freeSweepAllocationThreshold;
    if (directArena != null) {
      this.smallSubPageDirectCaches = createSubPageCaches(smallCacheSize, directArena.numSmallSubpagePools);
      this.normalDirectCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, directArena);
      directArena.numThreadCaches.getAndIncrement();
    } else {
      this.smallSubPageDirectCaches = null;
      this.normalDirectCaches = null;
    } 
    if (heapArena != null) {
      this.smallSubPageHeapCaches = createSubPageCaches(smallCacheSize, heapArena.numSmallSubpagePools);
      this.normalHeapCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, (PoolArena)heapArena);
      heapArena.numThreadCaches.getAndIncrement();
    } else {
      this.smallSubPageHeapCaches = null;
      this.normalHeapCaches = null;
    } 
    if ((this.smallSubPageDirectCaches != null || this.normalDirectCaches != null || this.smallSubPageHeapCaches != null || this.normalHeapCaches != null) && freeSweepAllocationThreshold < 1)
      throw new IllegalArgumentException("freeSweepAllocationThreshold: " + freeSweepAllocationThreshold + " (expected: > 0)"); 
  }
  
  private static <T> MemoryRegionCache<T>[] createSubPageCaches(int cacheSize, int numCaches) {
    if (cacheSize > 0 && numCaches > 0) {
      MemoryRegionCache[] arrayOfMemoryRegionCache = new MemoryRegionCache[numCaches];
      for (int i = 0; i < arrayOfMemoryRegionCache.length; i++)
        arrayOfMemoryRegionCache[i] = new SubPageMemoryRegionCache(cacheSize); 
      return (MemoryRegionCache<T>[])arrayOfMemoryRegionCache;
    } 
    return null;
  }
  
  private static <T> MemoryRegionCache<T>[] createNormalCaches(int cacheSize, int maxCachedBufferCapacity, PoolArena<T> area) {
    if (cacheSize > 0 && maxCachedBufferCapacity > 0) {
      int max = Math.min(area.chunkSize, maxCachedBufferCapacity);
      List<MemoryRegionCache<T>> cache = new ArrayList<MemoryRegionCache<T>>();
      for (int idx = area.numSmallSubpagePools; idx < area.nSizes && area.sizeIdx2size(idx) <= max; idx++)
        cache.add(new NormalMemoryRegionCache<T>(cacheSize)); 
      return cache.<MemoryRegionCache<T>>toArray((MemoryRegionCache<T>[])new MemoryRegionCache[0]);
    } 
    return null;
  }
  
  boolean allocateSmall(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int sizeIdx) {
    return allocate(cacheForSmall(area, sizeIdx), buf, reqCapacity);
  }
  
  boolean allocateNormal(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int sizeIdx) {
    return allocate(cacheForNormal(area, sizeIdx), buf, reqCapacity);
  }
  
  private boolean allocate(MemoryRegionCache<?> cache, PooledByteBuf<?> buf, int reqCapacity) {
    if (cache == null)
      return false; 
    boolean allocated = cache.allocate(buf, reqCapacity, this);
    if (++this.allocations >= this.freeSweepAllocationThreshold) {
      this.allocations = 0;
      trim();
    } 
    return allocated;
  }
  
  boolean add(PoolArena<?> area, PoolChunk<?> chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolArena.SizeClass sizeClass) {
    int sizeIdx = area.size2SizeIdx(normCapacity);
    MemoryRegionCache<?> cache = cache(area, sizeIdx, sizeClass);
    if (cache == null)
      return false; 
    if (this.freed.get())
      return false; 
    return cache.add(chunk, nioBuffer, handle, normCapacity);
  }
  
  private MemoryRegionCache<?> cache(PoolArena<?> area, int sizeIdx, PoolArena.SizeClass sizeClass) {
    switch (sizeClass) {
      case Normal:
        return cacheForNormal(area, sizeIdx);
      case Small:
        return cacheForSmall(area, sizeIdx);
    } 
    throw new Error();
  }
  
  protected void finalize() throws Throwable {
    try {
      super.finalize();
    } finally {
      free(true);
    } 
  }
  
  void free(boolean finalizer) {
    if (this.freed.compareAndSet(false, true)) {
      int numFreed = free((MemoryRegionCache<?>[])this.smallSubPageDirectCaches, finalizer) + free((MemoryRegionCache<?>[])this.normalDirectCaches, finalizer) + free((MemoryRegionCache<?>[])this.smallSubPageHeapCaches, finalizer) + free((MemoryRegionCache<?>[])this.normalHeapCaches, finalizer);
      if (numFreed > 0 && logger.isDebugEnabled())
        logger.debug("Freed {} thread-local buffer(s) from thread: {}", Integer.valueOf(numFreed), 
            Thread.currentThread().getName()); 
      if (this.directArena != null)
        this.directArena.numThreadCaches.getAndDecrement(); 
      if (this.heapArena != null)
        this.heapArena.numThreadCaches.getAndDecrement(); 
    } else {
      checkCacheMayLeak((MemoryRegionCache<?>[])this.smallSubPageDirectCaches, "SmallSubPageDirectCaches");
      checkCacheMayLeak((MemoryRegionCache<?>[])this.normalDirectCaches, "NormalDirectCaches");
      checkCacheMayLeak((MemoryRegionCache<?>[])this.smallSubPageHeapCaches, "SmallSubPageHeapCaches");
      checkCacheMayLeak((MemoryRegionCache<?>[])this.normalHeapCaches, "NormalHeapCaches");
    } 
  }
  
  private static void checkCacheMayLeak(MemoryRegionCache<?>[] caches, String type) {
    for (MemoryRegionCache<?> cache : caches) {
      if (!cache.queue.isEmpty()) {
        logger.debug("{} memory may leak.", type);
        return;
      } 
    } 
  }
  
  private static int free(MemoryRegionCache<?>[] caches, boolean finalizer) {
    if (caches == null)
      return 0; 
    int numFreed = 0;
    for (MemoryRegionCache<?> c : caches)
      numFreed += free(c, finalizer); 
    return numFreed;
  }
  
  private static int free(MemoryRegionCache<?> cache, boolean finalizer) {
    if (cache == null)
      return 0; 
    return cache.free(finalizer);
  }
  
  void trim() {
    trim((MemoryRegionCache<?>[])this.smallSubPageDirectCaches);
    trim((MemoryRegionCache<?>[])this.normalDirectCaches);
    trim((MemoryRegionCache<?>[])this.smallSubPageHeapCaches);
    trim((MemoryRegionCache<?>[])this.normalHeapCaches);
  }
  
  private static void trim(MemoryRegionCache<?>[] caches) {
    if (caches == null)
      return; 
    for (MemoryRegionCache<?> c : caches)
      trim(c); 
  }
  
  private static void trim(MemoryRegionCache<?> cache) {
    if (cache == null)
      return; 
    cache.trim();
  }
  
  private MemoryRegionCache<?> cacheForSmall(PoolArena<?> area, int sizeIdx) {
    if (area.isDirect())
      return cache((MemoryRegionCache<?>[])this.smallSubPageDirectCaches, sizeIdx); 
    return cache((MemoryRegionCache<?>[])this.smallSubPageHeapCaches, sizeIdx);
  }
  
  private MemoryRegionCache<?> cacheForNormal(PoolArena<?> area, int sizeIdx) {
    int idx = sizeIdx - area.numSmallSubpagePools;
    if (area.isDirect())
      return cache((MemoryRegionCache<?>[])this.normalDirectCaches, idx); 
    return cache((MemoryRegionCache<?>[])this.normalHeapCaches, idx);
  }
  
  private static <T> MemoryRegionCache<T> cache(MemoryRegionCache<T>[] cache, int sizeIdx) {
    if (cache == null || sizeIdx > cache.length - 1)
      return null; 
    return cache[sizeIdx];
  }
  
  private static final class SubPageMemoryRegionCache<T> extends MemoryRegionCache<T> {
    SubPageMemoryRegionCache(int size) {
      super(size, PoolArena.SizeClass.Small);
    }
    
    protected void initBuf(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, PooledByteBuf<T> buf, int reqCapacity, PoolThreadCache threadCache) {
      chunk.initBufWithSubpage(buf, nioBuffer, handle, reqCapacity, threadCache);
    }
  }
  
  private static final class NormalMemoryRegionCache<T> extends MemoryRegionCache<T> {
    NormalMemoryRegionCache(int size) {
      super(size, PoolArena.SizeClass.Normal);
    }
    
    protected void initBuf(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, PooledByteBuf<T> buf, int reqCapacity, PoolThreadCache threadCache) {
      chunk.initBuf(buf, nioBuffer, handle, reqCapacity, threadCache);
    }
  }
  
  private static abstract class MemoryRegionCache<T> {
    private final int size;
    
    private final Queue<Entry<T>> queue;
    
    private final PoolArena.SizeClass sizeClass;
    
    private int allocations;
    
    MemoryRegionCache(int size, PoolArena.SizeClass sizeClass) {
      this.size = MathUtil.safeFindNextPositivePowerOfTwo(size);
      this.queue = PlatformDependent.newFixedMpscQueue(this.size);
      this.sizeClass = sizeClass;
    }
    
    public final boolean add(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int normCapacity) {
      Entry<T> entry = newEntry(chunk, nioBuffer, handle, normCapacity);
      boolean queued = this.queue.offer(entry);
      if (!queued)
        entry.unguardedRecycle(); 
      return queued;
    }
    
    public final boolean allocate(PooledByteBuf<T> buf, int reqCapacity, PoolThreadCache threadCache) {
      Entry<T> entry = this.queue.poll();
      if (entry == null)
        return false; 
      initBuf(entry.chunk, entry.nioBuffer, entry.handle, buf, reqCapacity, threadCache);
      entry.unguardedRecycle();
      this.allocations++;
      return true;
    }
    
    public final int free(boolean finalizer) {
      return free(2147483647, finalizer);
    }
    
    private int free(int max, boolean finalizer) {
      int numFreed = 0;
      for (; numFreed < max; numFreed++) {
        Entry<T> entry = this.queue.poll();
        if (entry != null) {
          freeEntry(entry, finalizer);
        } else {
          return numFreed;
        } 
      } 
      return numFreed;
    }
    
    public final void trim() {
      int free = this.size - this.allocations;
      this.allocations = 0;
      if (free > 0)
        free(free, false); 
    }
    
    private void freeEntry(Entry entry, boolean finalizer) {
      PoolChunk chunk = entry.chunk;
      long handle = entry.handle;
      ByteBuffer nioBuffer = entry.nioBuffer;
      int normCapacity = entry.normCapacity;
      if (!finalizer)
        entry.recycle(); 
      chunk.arena.freeChunk(chunk, handle, normCapacity, this.sizeClass, nioBuffer, finalizer);
    }
    
    static final class Entry<T> {
      final Recycler.EnhancedHandle<Entry<?>> recyclerHandle;
      
      PoolChunk<T> chunk;
      
      ByteBuffer nioBuffer;
      
      long handle = -1L;
      
      int normCapacity;
      
      Entry(ObjectPool.Handle<Entry<?>> recyclerHandle) {
        this.recyclerHandle = (Recycler.EnhancedHandle<Entry<?>>)recyclerHandle;
      }
      
      void recycle() {
        this.chunk = null;
        this.nioBuffer = null;
        this.handle = -1L;
        this.recyclerHandle.recycle(this);
      }
      
      void unguardedRecycle() {
        this.chunk = null;
        this.nioBuffer = null;
        this.handle = -1L;
        this.recyclerHandle.unguardedRecycle(this);
      }
    }
    
    private static Entry newEntry(PoolChunk<?> chunk, ByteBuffer nioBuffer, long handle, int normCapacity) {
      Entry entry = (Entry)RECYCLER.get();
      entry.chunk = chunk;
      entry.nioBuffer = nioBuffer;
      entry.handle = handle;
      entry.normCapacity = normCapacity;
      return entry;
    }
    
    private static final ObjectPool<Entry> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<Entry>() {
          public PoolThreadCache.MemoryRegionCache.Entry newObject(ObjectPool.Handle<PoolThreadCache.MemoryRegionCache.Entry> handle) {
            return new PoolThreadCache.MemoryRegionCache.Entry((ObjectPool.Handle)handle);
          }
        });
    
    protected abstract void initBuf(PoolChunk<T> param1PoolChunk, ByteBuffer param1ByteBuffer, long param1Long, PooledByteBuf<T> param1PooledByteBuf, int param1Int, PoolThreadCache param1PoolThreadCache);
  }
}
