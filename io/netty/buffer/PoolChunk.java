package io.netty.buffer;

import io.netty.util.internal.LongCounter;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReentrantLock;

final class PoolChunk<T> implements PoolChunkMetric {
  private static final int SIZE_BIT_LENGTH = 15;
  
  private static final int INUSED_BIT_LENGTH = 1;
  
  private static final int SUBPAGE_BIT_LENGTH = 1;
  
  private static final int BITMAP_IDX_BIT_LENGTH = 32;
  
  static final int IS_SUBPAGE_SHIFT = 32;
  
  static final int IS_USED_SHIFT = 33;
  
  static final int SIZE_SHIFT = 34;
  
  static final int RUN_OFFSET_SHIFT = 49;
  
  final PoolArena<T> arena;
  
  final Object base;
  
  final T memory;
  
  final boolean unpooled;
  
  private final LongLongHashMap runsAvailMap;
  
  private final LongPriorityQueue[] runsAvail;
  
  private final ReentrantLock runsAvailLock;
  
  private final PoolSubpage<T>[] subpages;
  
  private final LongCounter pinnedBytes = PlatformDependent.newLongCounter();
  
  private final int pageSize;
  
  private final int pageShifts;
  
  private final int chunkSize;
  
  private final Deque<ByteBuffer> cachedNioBuffers;
  
  int freeBytes;
  
  PoolChunkList<T> parent;
  
  PoolChunk<T> prev;
  
  PoolChunk<T> next;
  
  PoolChunk(PoolArena<T> arena, Object base, T memory, int pageSize, int pageShifts, int chunkSize, int maxPageIdx) {
    this.unpooled = false;
    this.arena = arena;
    this.base = base;
    this.memory = memory;
    this.pageSize = pageSize;
    this.pageShifts = pageShifts;
    this.chunkSize = chunkSize;
    this.freeBytes = chunkSize;
    this.runsAvail = newRunsAvailqueueArray(maxPageIdx);
    this.runsAvailLock = new ReentrantLock();
    this.runsAvailMap = new LongLongHashMap(-1L);
    this.subpages = (PoolSubpage<T>[])new PoolSubpage[chunkSize >> pageShifts];
    int pages = chunkSize >> pageShifts;
    long initHandle = pages << 34L;
    insertAvailRun(0, pages, initHandle);
    this.cachedNioBuffers = new ArrayDeque<ByteBuffer>(8);
  }
  
  PoolChunk(PoolArena<T> arena, Object base, T memory, int size) {
    this.unpooled = true;
    this.arena = arena;
    this.base = base;
    this.memory = memory;
    this.pageSize = 0;
    this.pageShifts = 0;
    this.runsAvailMap = null;
    this.runsAvail = null;
    this.runsAvailLock = null;
    this.subpages = null;
    this.chunkSize = size;
    this.cachedNioBuffers = null;
  }
  
  private static LongPriorityQueue[] newRunsAvailqueueArray(int size) {
    LongPriorityQueue[] queueArray = new LongPriorityQueue[size];
    for (int i = 0; i < queueArray.length; i++)
      queueArray[i] = new LongPriorityQueue(); 
    return queueArray;
  }
  
  private void insertAvailRun(int runOffset, int pages, long handle) {
    int pageIdxFloor = this.arena.pages2pageIdxFloor(pages);
    LongPriorityQueue queue = this.runsAvail[pageIdxFloor];
    queue.offer(handle);
    insertAvailRun0(runOffset, handle);
    if (pages > 1)
      insertAvailRun0(lastPage(runOffset, pages), handle); 
  }
  
  private void insertAvailRun0(int runOffset, long handle) {
    long pre = this.runsAvailMap.put(runOffset, handle);
    assert pre == -1L;
  }
  
  private void removeAvailRun(long handle) {
    int pageIdxFloor = this.arena.pages2pageIdxFloor(runPages(handle));
    this.runsAvail[pageIdxFloor].remove(handle);
    removeAvailRun0(handle);
  }
  
  private void removeAvailRun0(long handle) {
    int runOffset = runOffset(handle);
    int pages = runPages(handle);
    this.runsAvailMap.remove(runOffset);
    if (pages > 1)
      this.runsAvailMap.remove(lastPage(runOffset, pages)); 
  }
  
  private static int lastPage(int runOffset, int pages) {
    return runOffset + pages - 1;
  }
  
  private long getAvailRunByOffset(int runOffset) {
    return this.runsAvailMap.get(runOffset);
  }
  
  public int usage() {
    int freeBytes;
    if (this.unpooled) {
      freeBytes = this.freeBytes;
    } else {
      this.runsAvailLock.lock();
      try {
        freeBytes = this.freeBytes;
      } finally {
        this.runsAvailLock.unlock();
      } 
    } 
    return usage(freeBytes);
  }
  
  private int usage(int freeBytes) {
    if (freeBytes == 0)
      return 100; 
    int freePercentage = (int)(freeBytes * 100L / this.chunkSize);
    if (freePercentage == 0)
      return 99; 
    return 100 - freePercentage;
  }
  
  boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int sizeIdx, PoolArenasCache cache) {
    long handle;
    if (sizeIdx <= this.arena.smallMaxSizeIdx) {
      handle = allocateSubpage(sizeIdx);
      if (handle < 0L)
        return false; 
      assert isSubpage(handle);
    } else {
      int runSize = this.arena.sizeIdx2size(sizeIdx);
      handle = allocateRun(runSize);
      if (handle < 0L)
        return false; 
      assert !isSubpage(handle);
    } 
    ByteBuffer nioBuffer = (this.cachedNioBuffers != null) ? this.cachedNioBuffers.pollLast() : null;
    initBuf(buf, nioBuffer, handle, reqCapacity, cache);
    return true;
  }
  
  private long allocateRun(int runSize) {
    int pages = runSize >> this.pageShifts;
    int pageIdx = this.arena.pages2pageIdx(pages);
    this.runsAvailLock.lock();
    try {
      int queueIdx = runFirstBestFit(pageIdx);
      if (queueIdx == -1)
        return -1L; 
      LongPriorityQueue queue = this.runsAvail[queueIdx];
      long handle = queue.poll();
      assert handle != -1L && !isUsed(handle) : "invalid handle: " + handle;
      removeAvailRun0(handle);
      if (handle != -1L)
        handle = splitLargeRun(handle, pages); 
      int pinnedSize = runSize(this.pageShifts, handle);
      this.freeBytes -= pinnedSize;
      return handle;
    } finally {
      this.runsAvailLock.unlock();
    } 
  }
  
  private int calculateRunSize(int sizeIdx) {
    int nElements, maxElements = 1 << this.pageShifts - 4;
    int runSize = 0;
    int elemSize = this.arena.sizeIdx2size(sizeIdx);
    do {
      runSize += this.pageSize;
      nElements = runSize / elemSize;
    } while (nElements < maxElements && runSize != nElements * elemSize);
    while (nElements > maxElements) {
      runSize -= this.pageSize;
      nElements = runSize / elemSize;
    } 
    assert nElements > 0;
    assert runSize <= this.chunkSize;
    assert runSize >= elemSize;
    return runSize;
  }
  
  private int runFirstBestFit(int pageIdx) {
    if (this.freeBytes == this.chunkSize)
      return this.arena.nPSizes - 1; 
    for (int i = pageIdx; i < this.arena.nPSizes; i++) {
      LongPriorityQueue queue = this.runsAvail[i];
      if (queue != null && !queue.isEmpty())
        return i; 
    } 
    return -1;
  }
  
  private long splitLargeRun(long handle, int needPages) {
    assert needPages > 0;
    int totalPages = runPages(handle);
    assert needPages <= totalPages;
    int remPages = totalPages - needPages;
    if (remPages > 0) {
      int runOffset = runOffset(handle);
      int availOffset = runOffset + needPages;
      long availRun = toRunHandle(availOffset, remPages, 0);
      insertAvailRun(availOffset, remPages, availRun);
      return toRunHandle(runOffset, needPages, 1);
    } 
    handle |= 0x200000000L;
    return handle;
  }
  
  private long allocateSubpage(int sizeIdx) {
    PoolSubpage<T> head = this.arena.findSubpagePoolHead(sizeIdx);
    head.lock();
    try {
      int runSize = calculateRunSize(sizeIdx);
      long runHandle = allocateRun(runSize);
      if (runHandle < 0L)
        return -1L; 
      int runOffset = runOffset(runHandle);
      assert this.subpages[runOffset] == null;
      int elemSize = this.arena.sizeIdx2size(sizeIdx);
      PoolSubpage<T> subpage = new PoolSubpage<T>(head, this, this.pageShifts, runOffset, runSize(this.pageShifts, runHandle), elemSize);
      this.subpages[runOffset] = subpage;
      return subpage.allocate();
    } finally {
      head.unlock();
    } 
  }
  
  void free(long handle, int normCapacity, ByteBuffer nioBuffer) {
    int runSize = runSize(this.pageShifts, handle);
    if (isSubpage(handle)) {
      int sizeIdx = this.arena.size2SizeIdx(normCapacity);
      PoolSubpage<T> head = this.arena.findSubpagePoolHead(sizeIdx);
      int sIdx = runOffset(handle);
      PoolSubpage<T> subpage = this.subpages[sIdx];
      head.lock();
      try {
        assert subpage != null && subpage.doNotDestroy;
        if (subpage.free(head, bitmapIdx(handle)))
          return; 
        assert !subpage.doNotDestroy;
        this.subpages[sIdx] = null;
      } finally {
        head.unlock();
      } 
    } 
    this.runsAvailLock.lock();
    try {
      long finalRun = collapseRuns(handle);
      finalRun &= 0xFFFFFFFDFFFFFFFFL;
      finalRun &= 0xFFFFFFFEFFFFFFFFL;
      insertAvailRun(runOffset(finalRun), runPages(finalRun), finalRun);
      this.freeBytes += runSize;
    } finally {
      this.runsAvailLock.unlock();
    } 
    if (nioBuffer != null && this.cachedNioBuffers != null && this.cachedNioBuffers
      .size() < PooledByteBufAllocator.DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK)
      this.cachedNioBuffers.offer(nioBuffer); 
  }
  
  private long collapseRuns(long handle) {
    return collapseNext(collapsePast(handle));
  }
  
  private long collapsePast(long handle) {
    while (true) {
      int runOffset = runOffset(handle);
      int runPages = runPages(handle);
      long pastRun = getAvailRunByOffset(runOffset - 1);
      if (pastRun == -1L)
        return handle; 
      int pastOffset = runOffset(pastRun);
      int pastPages = runPages(pastRun);
      if (pastRun != handle && pastOffset + pastPages == runOffset) {
        removeAvailRun(pastRun);
        handle = toRunHandle(pastOffset, pastPages + runPages, 0);
        continue;
      } 
      break;
    } 
    return handle;
  }
  
  private long collapseNext(long handle) {
    while (true) {
      int runOffset = runOffset(handle);
      int runPages = runPages(handle);
      long nextRun = getAvailRunByOffset(runOffset + runPages);
      if (nextRun == -1L)
        return handle; 
      int nextOffset = runOffset(nextRun);
      int nextPages = runPages(nextRun);
      if (nextRun != handle && runOffset + runPages == nextOffset) {
        removeAvailRun(nextRun);
        handle = toRunHandle(runOffset, runPages + nextPages, 0);
        continue;
      } 
      break;
    } 
    return handle;
  }
  
  private static long toRunHandle(int runOffset, int runPages, int inUsed) {
    return runOffset << 49L | runPages << 34L | inUsed << 33L;
  }
  
  void initBuf(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity, PoolArenasCache threadCache) {
    if (isSubpage(handle)) {
      initBufWithSubpage(buf, nioBuffer, handle, reqCapacity, threadCache);
    } else {
      int maxLength = runSize(this.pageShifts, handle);
      buf.init(this, nioBuffer, handle, runOffset(handle) << this.pageShifts, reqCapacity, maxLength, this.arena.parent
          .threadCache());
    } 
  }
  
  void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity, PoolArenasCache threadCache) {
    int runOffset = runOffset(handle);
    int bitmapIdx = bitmapIdx(handle);
    PoolSubpage<T> s = this.subpages[runOffset];
    assert s.doNotDestroy;
    assert reqCapacity <= s.elemSize : reqCapacity + "<=" + s.elemSize;
    int offset = (runOffset << this.pageShifts) + bitmapIdx * s.elemSize;
    buf.init(this, nioBuffer, handle, offset, reqCapacity, s.elemSize, threadCache);
  }
  
  void incrementPinnedMemory(int delta) {
    assert delta > 0;
    this.pinnedBytes.add(delta);
  }
  
  void decrementPinnedMemory(int delta) {
    assert delta > 0;
    this.pinnedBytes.add(-delta);
  }
  
  public int chunkSize() {
    return this.chunkSize;
  }
  
  public int freeBytes() {
    if (this.unpooled)
      return this.freeBytes; 
    this.runsAvailLock.lock();
    try {
      return this.freeBytes;
    } finally {
      this.runsAvailLock.unlock();
    } 
  }
  
  public int pinnedBytes() {
    return (int)this.pinnedBytes.value();
  }
  
  public String toString() {
    int freeBytes;
    if (this.unpooled) {
      freeBytes = this.freeBytes;
    } else {
      this.runsAvailLock.lock();
      try {
        freeBytes = this.freeBytes;
      } finally {
        this.runsAvailLock.unlock();
      } 
    } 
    return "Chunk(" + 
      
      Integer.toHexString(System.identityHashCode(this)) + ": " + 
      
      usage(freeBytes) + "%, " + (
      this.chunkSize - freeBytes) + 
      '/' + 
      this.chunkSize + 
      ')';
  }
  
  void destroy() {
    this.arena.destroyChunk(this);
  }
  
  static int runOffset(long handle) {
    return (int)(handle >> 49L);
  }
  
  static int runSize(int pageShifts, long handle) {
    return runPages(handle) << pageShifts;
  }
  
  static int runPages(long handle) {
    return (int)(handle >> 34L & 0x7FFFL);
  }
  
  static boolean isUsed(long handle) {
    return ((handle >> 33L & 0x1L) == 1L);
  }
  
  static boolean isRun(long handle) {
    return !isSubpage(handle);
  }
  
  static boolean isSubpage(long handle) {
    return ((handle >> 32L & 0x1L) == 1L);
  }
  
  static int bitmapIdx(long handle) {
    return (int)handle;
  }
}
