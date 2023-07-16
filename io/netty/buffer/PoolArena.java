package io.netty.buffer;

import io.netty.util.internal.LongCounter;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

abstract class PoolArena<T> extends SizeClasses implements PoolArenaMetric {
  private static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();
  
  final PooledByteBufAllocator parent;
  
  final int numSmallSubpagePools;
  
  final int directMemoryCacheAlignment;
  
  private final PoolSubpage<T>[] smallSubpagePools;
  
  private final PoolChunkList<T> q050;
  
  private final PoolChunkList<T> q025;
  
  private final PoolChunkList<T> q000;
  
  private final PoolChunkList<T> qInit;
  
  private final PoolChunkList<T> q075;
  
  private final PoolChunkList<T> q100;
  
  private final List<PoolChunkListMetric> chunkListMetrics;
  
  private long allocationsNormal;
  
  enum SizeClass {
    Small, Normal;
  }
  
  private final LongCounter allocationsSmall = PlatformDependent.newLongCounter();
  
  private final LongCounter allocationsHuge = PlatformDependent.newLongCounter();
  
  private final LongCounter activeBytesHuge = PlatformDependent.newLongCounter();
  
  private long deallocationsSmall;
  
  private long deallocationsNormal;
  
  private final LongCounter deallocationsHuge = PlatformDependent.newLongCounter();
  
  final AtomicInteger numThreadCaches = new AtomicInteger();
  
  private final ReentrantLock lock = new ReentrantLock();
  
  protected PoolArena(PooledByteBufAllocator parent, int pageSize, int pageShifts, int chunkSize, int cacheAlignment) {
    super(pageSize, pageShifts, chunkSize, cacheAlignment);
    this.parent = parent;
    this.directMemoryCacheAlignment = cacheAlignment;
    this.numSmallSubpagePools = this.nSubpages;
    this.smallSubpagePools = newSubpagePoolArray(this.numSmallSubpagePools);
    for (int i = 0; i < this.smallSubpagePools.length; i++)
      this.smallSubpagePools[i] = newSubpagePoolHead(); 
    this.q100 = new PoolChunkList<T>(this, null, 100, 2147483647, chunkSize);
    this.q075 = new PoolChunkList<T>(this, this.q100, 75, 100, chunkSize);
    this.q050 = new PoolChunkList<T>(this, this.q075, 50, 100, chunkSize);
    this.q025 = new PoolChunkList<T>(this, this.q050, 25, 75, chunkSize);
    this.q000 = new PoolChunkList<T>(this, this.q025, 1, 50, chunkSize);
    this.qInit = new PoolChunkList<T>(this, this.q000, -2147483648, 25, chunkSize);
    this.q100.prevList(this.q075);
    this.q075.prevList(this.q050);
    this.q050.prevList(this.q025);
    this.q025.prevList(this.q000);
    this.q000.prevList(null);
    this.qInit.prevList(this.qInit);
    List<PoolChunkListMetric> metrics = new ArrayList<PoolChunkListMetric>(6);
    metrics.add(this.qInit);
    metrics.add(this.q000);
    metrics.add(this.q025);
    metrics.add(this.q050);
    metrics.add(this.q075);
    metrics.add(this.q100);
    this.chunkListMetrics = Collections.unmodifiableList(metrics);
  }
  
  private PoolSubpage<T> newSubpagePoolHead() {
    PoolSubpage<T> head = new PoolSubpage<T>();
    head.prev = head;
    head.next = head;
    return head;
  }
  
  private PoolSubpage<T>[] newSubpagePoolArray(int size) {
    return (PoolSubpage<T>[])new PoolSubpage[size];
  }
  
  PooledByteBuf<T> allocate(PoolArenasCache cache, int reqCapacity, int maxCapacity) {
    PooledByteBuf<T> buf = newByteBuf(maxCapacity);
    allocate(cache, buf, reqCapacity);
    return buf;
  }
  
  private void allocate(PoolArenasCache cache, PooledByteBuf<T> buf, int reqCapacity) {
    int sizeIdx = size2SizeIdx(reqCapacity);
    if (sizeIdx <= this.smallMaxSizeIdx) {
      tcacheAllocateSmall(cache, buf, reqCapacity, sizeIdx);
    } else if (sizeIdx < this.nSizes) {
      tcacheAllocateNormal(cache, buf, reqCapacity, sizeIdx);
    } else {
      int normCapacity = (this.directMemoryCacheAlignment > 0) ? normalizeSize(reqCapacity) : reqCapacity;
      allocateHuge(buf, normCapacity);
    } 
  }
  
  private void tcacheAllocateSmall(PoolArenasCache cache, PooledByteBuf<T> buf, int reqCapacity, int sizeIdx) {
    boolean needsNormalAllocation;
    if (cache.allocateSmall(this, buf, reqCapacity, sizeIdx))
      return; 
    PoolSubpage<T> head = findSubpagePoolHead(sizeIdx);
    head.lock();
    try {
      PoolSubpage<T> s = head.next;
      needsNormalAllocation = (s == head);
      if (!needsNormalAllocation) {
        assert s.doNotDestroy && s.elemSize == sizeIdx2size(sizeIdx) : "doNotDestroy=" + s.doNotDestroy + ", elemSize=" + s.elemSize + ", sizeIdx=" + sizeIdx;
        long handle = s.allocate();
        assert handle >= 0L;
        s.chunk.initBufWithSubpage(buf, null, handle, reqCapacity, cache);
      } 
    } finally {
      head.unlock();
    } 
    if (needsNormalAllocation) {
      lock();
      try {
        allocateNormal(buf, reqCapacity, sizeIdx, cache);
      } finally {
        unlock();
      } 
    } 
    incSmallAllocation();
  }
  
  private void tcacheAllocateNormal(PoolArenasCache cache, PooledByteBuf<T> buf, int reqCapacity, int sizeIdx) {
    if (cache.allocateNormal(this, buf, reqCapacity, sizeIdx))
      return; 
    lock();
    try {
      allocateNormal(buf, reqCapacity, sizeIdx, cache);
      this.allocationsNormal++;
    } finally {
      unlock();
    } 
  }
  
  private void allocateNormal(PooledByteBuf<T> buf, int reqCapacity, int sizeIdx, PoolArenasCache threadCache) {
    assert this.lock.isHeldByCurrentThread();
    if (this.q050.allocate(buf, reqCapacity, sizeIdx, threadCache) || this.q025
      .allocate(buf, reqCapacity, sizeIdx, threadCache) || this.q000
      .allocate(buf, reqCapacity, sizeIdx, threadCache) || this.qInit
      .allocate(buf, reqCapacity, sizeIdx, threadCache) || this.q075
      .allocate(buf, reqCapacity, sizeIdx, threadCache))
      return; 
    PoolChunk<T> c = newChunk(this.pageSize, this.nPSizes, this.pageShifts, this.chunkSize);
    boolean success = c.allocate(buf, reqCapacity, sizeIdx, threadCache);
    assert success;
    this.qInit.add(c);
  }
  
  private void incSmallAllocation() {
    this.allocationsSmall.increment();
  }
  
  private void allocateHuge(PooledByteBuf<T> buf, int reqCapacity) {
    PoolChunk<T> chunk = newUnpooledChunk(reqCapacity);
    this.activeBytesHuge.add(chunk.chunkSize());
    buf.initUnpooled(chunk, reqCapacity);
    this.allocationsHuge.increment();
  }
  
  void free(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolArenasCache cache) {
    if (chunk.unpooled) {
      int size = chunk.chunkSize();
      destroyChunk(chunk);
      this.activeBytesHuge.add(-size);
      this.deallocationsHuge.increment();
    } else {
      SizeClass sizeClass = sizeClass(handle);
      if (cache != null && cache.add(this, chunk, nioBuffer, handle, normCapacity, sizeClass))
        return; 
      freeChunk(chunk, handle, normCapacity, sizeClass, nioBuffer, false);
    } 
  }
  
  private static SizeClass sizeClass(long handle) {
    return PoolChunk.isSubpage(handle) ? SizeClass.Small : SizeClass.Normal;
  }
  
  void freeChunk(PoolChunk<T> chunk, long handle, int normCapacity, SizeClass sizeClass, ByteBuffer nioBuffer, boolean finalizer) {
    boolean destroyChunk;
    lock();
    try {
      if (!finalizer)
        switch (sizeClass) {
          case Normal:
            this.deallocationsNormal++;
            break;
          case Small:
            this.deallocationsSmall++;
            break;
          default:
            throw new Error();
        }  
      destroyChunk = !chunk.parent.free(chunk, handle, normCapacity, nioBuffer);
    } finally {
      unlock();
    } 
    if (destroyChunk)
      destroyChunk(chunk); 
  }
  
  PoolSubpage<T> findSubpagePoolHead(int sizeIdx) {
    return this.smallSubpagePools[sizeIdx];
  }
  
  void reallocate(PooledByteBuf<T> buf, int newCapacity, boolean freeOldMemory) {
    int bytesToCopy;
    assert newCapacity >= 0 && newCapacity <= buf.maxCapacity();
    int oldCapacity = buf.length;
    if (oldCapacity == newCapacity)
      return; 
    PoolChunk<T> oldChunk = buf.chunk;
    ByteBuffer oldNioBuffer = buf.tmpNioBuf;
    long oldHandle = buf.handle;
    T oldMemory = buf.memory;
    int oldOffset = buf.offset;
    int oldMaxLength = buf.maxLength;
    allocate(this.parent.threadCache(), buf, newCapacity);
    if (newCapacity > oldCapacity) {
      bytesToCopy = oldCapacity;
    } else {
      buf.trimIndicesToCapacity(newCapacity);
      bytesToCopy = newCapacity;
    } 
    memoryCopy(oldMemory, oldOffset, buf, bytesToCopy);
    if (freeOldMemory)
      free(oldChunk, oldNioBuffer, oldHandle, oldMaxLength, buf.cache); 
  }
  
  public int numThreadCaches() {
    return this.numThreadCaches.get();
  }
  
  public int numTinySubpages() {
    return 0;
  }
  
  public int numSmallSubpages() {
    return this.smallSubpagePools.length;
  }
  
  public int numChunkLists() {
    return this.chunkListMetrics.size();
  }
  
  public List<PoolSubpageMetric> tinySubpages() {
    return Collections.emptyList();
  }
  
  public List<PoolSubpageMetric> smallSubpages() {
    return subPageMetricList((PoolSubpage<?>[])this.smallSubpagePools);
  }
  
  public List<PoolChunkListMetric> chunkLists() {
    return this.chunkListMetrics;
  }
  
  private static List<PoolSubpageMetric> subPageMetricList(PoolSubpage<?>[] pages) {
    List<PoolSubpageMetric> metrics = new ArrayList<PoolSubpageMetric>();
    for (PoolSubpage<?> head : pages) {
      if (head.next != head) {
        PoolSubpage<?> s = head.next;
        do {
          metrics.add(s);
          s = s.next;
        } while (s != head);
      } 
    } 
    return metrics;
  }
  
  public long numAllocations() {
    long allocsNormal;
    lock();
    try {
      allocsNormal = this.allocationsNormal;
    } finally {
      unlock();
    } 
    return this.allocationsSmall.value() + allocsNormal + this.allocationsHuge.value();
  }
  
  public long numTinyAllocations() {
    return 0L;
  }
  
  public long numSmallAllocations() {
    return this.allocationsSmall.value();
  }
  
  public long numNormalAllocations() {
    lock();
    try {
      return this.allocationsNormal;
    } finally {
      unlock();
    } 
  }
  
  public long numDeallocations() {
    long deallocs;
    lock();
    try {
      deallocs = this.deallocationsSmall + this.deallocationsNormal;
    } finally {
      unlock();
    } 
    return deallocs + this.deallocationsHuge.value();
  }
  
  public long numTinyDeallocations() {
    return 0L;
  }
  
  public long numSmallDeallocations() {
    lock();
    try {
      return this.deallocationsSmall;
    } finally {
      unlock();
    } 
  }
  
  public long numNormalDeallocations() {
    lock();
    try {
      return this.deallocationsNormal;
    } finally {
      unlock();
    } 
  }
  
  public long numHugeAllocations() {
    return this.allocationsHuge.value();
  }
  
  public long numHugeDeallocations() {
    return this.deallocationsHuge.value();
  }
  
  public long numActiveAllocations() {
    long val = this.allocationsSmall.value() + this.allocationsHuge.value() - this.deallocationsHuge.value();
    lock();
    try {
      val += this.allocationsNormal - this.deallocationsSmall + this.deallocationsNormal;
    } finally {
      unlock();
    } 
    return Math.max(val, 0L);
  }
  
  public long numActiveTinyAllocations() {
    return 0L;
  }
  
  public long numActiveSmallAllocations() {
    return Math.max(numSmallAllocations() - numSmallDeallocations(), 0L);
  }
  
  public long numActiveNormalAllocations() {
    long val;
    lock();
    try {
      val = this.allocationsNormal - this.deallocationsNormal;
    } finally {
      unlock();
    } 
    return Math.max(val, 0L);
  }
  
  public long numActiveHugeAllocations() {
    return Math.max(numHugeAllocations() - numHugeDeallocations(), 0L);
  }
  
  public long numActiveBytes() {
    long val = this.activeBytesHuge.value();
    lock();
    try {
      for (int i = 0; i < this.chunkListMetrics.size(); i++) {
        for (PoolChunkMetric m : this.chunkListMetrics.get(i))
          val += m.chunkSize(); 
      } 
    } finally {
      unlock();
    } 
    return Math.max(0L, val);
  }
  
  public long numPinnedBytes() {
    long val = this.activeBytesHuge.value();
    lock();
    try {
      for (int i = 0; i < this.chunkListMetrics.size(); i++) {
        for (PoolChunkMetric m : this.chunkListMetrics.get(i))
          val += ((PoolChunk)m).pinnedBytes(); 
      } 
    } finally {
      unlock();
    } 
    return Math.max(0L, val);
  }
  
  public String toString() {
    lock();
    try {
      StringBuilder buf = (new StringBuilder()).append("Chunk(s) at 0~25%:").append(StringUtil.NEWLINE).append(this.qInit).append(StringUtil.NEWLINE).append("Chunk(s) at 0~50%:").append(StringUtil.NEWLINE).append(this.q000).append(StringUtil.NEWLINE).append("Chunk(s) at 25~75%:").append(StringUtil.NEWLINE).append(this.q025).append(StringUtil.NEWLINE).append("Chunk(s) at 50~100%:").append(StringUtil.NEWLINE).append(this.q050).append(StringUtil.NEWLINE).append("Chunk(s) at 75~100%:").append(StringUtil.NEWLINE).append(this.q075).append(StringUtil.NEWLINE).append("Chunk(s) at 100%:").append(StringUtil.NEWLINE).append(this.q100).append(StringUtil.NEWLINE).append("small subpages:");
      appendPoolSubPages(buf, (PoolSubpage<?>[])this.smallSubpagePools);
      buf.append(StringUtil.NEWLINE);
      return buf.toString();
    } finally {
      unlock();
    } 
  }
  
  private static void appendPoolSubPages(StringBuilder buf, PoolSubpage<?>[] subpages) {
    for (int i = 0; i < subpages.length; i++) {
      PoolSubpage<?> head = subpages[i];
      if (head.next != head && head.next != null) {
        buf.append(StringUtil.NEWLINE)
          .append(i)
          .append(": ");
        PoolSubpage<?> s = head.next;
        while (s != null) {
          buf.append(s);
          s = s.next;
          if (s == head)
            break; 
        } 
      } 
    } 
  }
  
  protected final void finalize() throws Throwable {
    try {
      super.finalize();
    } finally {
      destroyPoolSubPages((PoolSubpage<?>[])this.smallSubpagePools);
      destroyPoolChunkLists((PoolChunkList<T>[])new PoolChunkList[] { this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100 });
    } 
  }
  
  private static void destroyPoolSubPages(PoolSubpage<?>[] pages) {
    for (PoolSubpage<?> page : pages)
      page.destroy(); 
  }
  
  private void destroyPoolChunkLists(PoolChunkList<T>... chunkLists) {
    for (PoolChunkList<T> chunkList : chunkLists)
      chunkList.destroy(this); 
  }
  
  static final class HeapArena extends PoolArena<byte[]> {
    HeapArena(PooledByteBufAllocator parent, int pageSize, int pageShifts, int chunkSize) {
      super(parent, pageSize, pageShifts, chunkSize, 0);
    }
    
    private static byte[] newByteArray(int size) {
      return PlatformDependent.allocateUninitializedArray(size);
    }
    
    boolean isDirect() {
      return false;
    }
    
    protected PoolChunk<byte[]> newChunk(int pageSize, int maxPageIdx, int pageShifts, int chunkSize) {
      return (PoolChunk)new PoolChunk<byte>(this, null, 
          newByteArray(chunkSize), pageSize, pageShifts, chunkSize, maxPageIdx);
    }
    
    protected PoolChunk<byte[]> newUnpooledChunk(int capacity) {
      return (PoolChunk)new PoolChunk<byte>(this, null, newByteArray(capacity), capacity);
    }
    
    protected void destroyChunk(PoolChunk<byte[]> chunk) {}
    
    protected PooledByteBuf<byte[]> newByteBuf(int maxCapacity) {
      return PoolArena.HAS_UNSAFE ? PooledUnsafeHeapByteBuf.newUnsafeInstance(maxCapacity) : 
        PooledHeapByteBuf.newInstance(maxCapacity);
    }
    
    protected void memoryCopy(byte[] src, int srcOffset, PooledByteBuf<byte[]> dst, int length) {
      if (length == 0)
        return; 
      System.arraycopy(src, srcOffset, dst.memory, dst.offset, length);
    }
  }
  
  static final class DirectArena extends PoolArena<ByteBuffer> {
    DirectArena(PooledByteBufAllocator parent, int pageSize, int pageShifts, int chunkSize, int directMemoryCacheAlignment) {
      super(parent, pageSize, pageShifts, chunkSize, directMemoryCacheAlignment);
    }
    
    boolean isDirect() {
      return true;
    }
    
    protected PoolChunk<ByteBuffer> newChunk(int pageSize, int maxPageIdx, int pageShifts, int chunkSize) {
      if (this.directMemoryCacheAlignment == 0) {
        ByteBuffer byteBuffer = allocateDirect(chunkSize);
        return new PoolChunk<ByteBuffer>(this, byteBuffer, byteBuffer, pageSize, pageShifts, chunkSize, maxPageIdx);
      } 
      ByteBuffer base = allocateDirect(chunkSize + this.directMemoryCacheAlignment);
      ByteBuffer memory = PlatformDependent.alignDirectBuffer(base, this.directMemoryCacheAlignment);
      return new PoolChunk<ByteBuffer>(this, base, memory, pageSize, pageShifts, chunkSize, maxPageIdx);
    }
    
    protected PoolChunk<ByteBuffer> newUnpooledChunk(int capacity) {
      if (this.directMemoryCacheAlignment == 0) {
        ByteBuffer byteBuffer = allocateDirect(capacity);
        return new PoolChunk<ByteBuffer>(this, byteBuffer, byteBuffer, capacity);
      } 
      ByteBuffer base = allocateDirect(capacity + this.directMemoryCacheAlignment);
      ByteBuffer memory = PlatformDependent.alignDirectBuffer(base, this.directMemoryCacheAlignment);
      return new PoolChunk<ByteBuffer>(this, base, memory, capacity);
    }
    
    private static ByteBuffer allocateDirect(int capacity) {
      return PlatformDependent.useDirectBufferNoCleaner() ? 
        PlatformDependent.allocateDirectNoCleaner(capacity) : ByteBuffer.allocateDirect(capacity);
    }
    
    protected void destroyChunk(PoolChunk<ByteBuffer> chunk) {
      if (PlatformDependent.useDirectBufferNoCleaner()) {
        PlatformDependent.freeDirectNoCleaner((ByteBuffer)chunk.base);
      } else {
        PlatformDependent.freeDirectBuffer((ByteBuffer)chunk.base);
      } 
    }
    
    protected PooledByteBuf<ByteBuffer> newByteBuf(int maxCapacity) {
      if (PoolArena.HAS_UNSAFE)
        return PooledUnsafeDirectByteBuf.newInstance(maxCapacity); 
      return PooledDirectByteBuf.newInstance(maxCapacity);
    }
    
    protected void memoryCopy(ByteBuffer src, int srcOffset, PooledByteBuf<ByteBuffer> dstBuf, int length) {
      if (length == 0)
        return; 
      if (PoolArena.HAS_UNSAFE) {
        PlatformDependent.copyMemory(
            PlatformDependent.directBufferAddress(src) + srcOffset, 
            PlatformDependent.directBufferAddress((ByteBuffer)dstBuf.memory) + dstBuf.offset, length);
      } else {
        src = src.duplicate();
        ByteBuffer dst = dstBuf.internalNioBuffer();
        src.position(srcOffset).limit(srcOffset + length);
        dst.position(dstBuf.offset);
        dst.put(src);
      } 
    }
  }
  
  void lock() {
    this.lock.lock();
  }
  
  void unlock() {
    this.lock.unlock();
  }
  
  abstract boolean isDirect();
  
  protected abstract PoolChunk<T> newChunk(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  protected abstract PoolChunk<T> newUnpooledChunk(int paramInt);
  
  protected abstract PooledByteBuf<T> newByteBuf(int paramInt);
  
  protected abstract void memoryCopy(T paramT, int paramInt1, PooledByteBuf<T> paramPooledByteBuf, int paramInt2);
  
  protected abstract void destroyChunk(PoolChunk<T> paramPoolChunk);
}
