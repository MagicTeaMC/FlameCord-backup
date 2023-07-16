package io.netty.buffer;

import java.util.concurrent.locks.ReentrantLock;

final class PoolSubpage<T> implements PoolSubpageMetric {
  final PoolChunk<T> chunk;
  
  final int elemSize;
  
  private final int pageShifts;
  
  private final int runOffset;
  
  private final int runSize;
  
  private final long[] bitmap;
  
  PoolSubpage<T> prev;
  
  PoolSubpage<T> next;
  
  boolean doNotDestroy;
  
  private int maxNumElems;
  
  private int bitmapLength;
  
  private int nextAvail;
  
  private int numAvail;
  
  private final ReentrantLock lock = new ReentrantLock();
  
  PoolSubpage() {
    this.chunk = null;
    this.pageShifts = -1;
    this.runOffset = -1;
    this.elemSize = -1;
    this.runSize = -1;
    this.bitmap = null;
  }
  
  PoolSubpage(PoolSubpage<T> head, PoolChunk<T> chunk, int pageShifts, int runOffset, int runSize, int elemSize) {
    this.chunk = chunk;
    this.pageShifts = pageShifts;
    this.runOffset = runOffset;
    this.runSize = runSize;
    this.elemSize = elemSize;
    this.bitmap = new long[runSize >>> 10];
    this.doNotDestroy = true;
    if (elemSize != 0) {
      this.maxNumElems = this.numAvail = runSize / elemSize;
      this.nextAvail = 0;
      this.bitmapLength = this.maxNumElems >>> 6;
      if ((this.maxNumElems & 0x3F) != 0)
        this.bitmapLength++; 
    } 
    addToPool(head);
  }
  
  long allocate() {
    if (this.numAvail == 0 || !this.doNotDestroy)
      return -1L; 
    int bitmapIdx = getNextAvail();
    if (bitmapIdx < 0) {
      removeFromPool();
      throw new AssertionError("No next available bitmap index found (bitmapIdx = " + bitmapIdx + "), even though there are supposed to be (numAvail = " + this.numAvail + ") out of (maxNumElems = " + this.maxNumElems + ") available indexes.");
    } 
    int q = bitmapIdx >>> 6;
    int r = bitmapIdx & 0x3F;
    assert (this.bitmap[q] >>> r & 0x1L) == 0L;
    this.bitmap[q] = this.bitmap[q] | 1L << r;
    if (--this.numAvail == 0)
      removeFromPool(); 
    return toHandle(bitmapIdx);
  }
  
  boolean free(PoolSubpage<T> head, int bitmapIdx) {
    if (this.elemSize == 0)
      return true; 
    int q = bitmapIdx >>> 6;
    int r = bitmapIdx & 0x3F;
    assert (this.bitmap[q] >>> r & 0x1L) != 0L;
    this.bitmap[q] = this.bitmap[q] ^ 1L << r;
    setNextAvail(bitmapIdx);
    if (this.numAvail++ == 0) {
      addToPool(head);
      if (this.maxNumElems > 1)
        return true; 
    } 
    if (this.numAvail != this.maxNumElems)
      return true; 
    if (this.prev == this.next)
      return true; 
    this.doNotDestroy = false;
    removeFromPool();
    return false;
  }
  
  private void addToPool(PoolSubpage<T> head) {
    assert this.prev == null && this.next == null;
    this.prev = head;
    this.next = head.next;
    this.next.prev = this;
    head.next = this;
  }
  
  private void removeFromPool() {
    assert this.prev != null && this.next != null;
    this.prev.next = this.next;
    this.next.prev = this.prev;
    this.next = null;
    this.prev = null;
  }
  
  private void setNextAvail(int bitmapIdx) {
    this.nextAvail = bitmapIdx;
  }
  
  private int getNextAvail() {
    int nextAvail = this.nextAvail;
    if (nextAvail >= 0) {
      this.nextAvail = -1;
      return nextAvail;
    } 
    return findNextAvail();
  }
  
  private int findNextAvail() {
    long[] bitmap = this.bitmap;
    int bitmapLength = this.bitmapLength;
    for (int i = 0; i < bitmapLength; i++) {
      long bits = bitmap[i];
      if ((bits ^ 0xFFFFFFFFFFFFFFFFL) != 0L)
        return findNextAvail0(i, bits); 
    } 
    return -1;
  }
  
  private int findNextAvail0(int i, long bits) {
    int maxNumElems = this.maxNumElems;
    int baseVal = i << 6;
    for (int j = 0; j < 64; j++) {
      if ((bits & 0x1L) == 0L) {
        int val = baseVal | j;
        if (val < maxNumElems)
          return val; 
        break;
      } 
      bits >>>= 1L;
    } 
    return -1;
  }
  
  private long toHandle(int bitmapIdx) {
    int pages = this.runSize >> this.pageShifts;
    return this.runOffset << 49L | pages << 34L | 0x200000000L | 0x100000000L | bitmapIdx;
  }
  
  public String toString() {
    boolean doNotDestroy;
    int maxNumElems;
    int numAvail;
    int elemSize;
    if (this.chunk == null) {
      doNotDestroy = true;
      maxNumElems = 0;
      numAvail = 0;
      elemSize = -1;
    } else {
      this.chunk.arena.lock();
      try {
        if (!this.doNotDestroy) {
          doNotDestroy = false;
          maxNumElems = numAvail = elemSize = -1;
        } else {
          doNotDestroy = true;
          maxNumElems = this.maxNumElems;
          numAvail = this.numAvail;
          elemSize = this.elemSize;
        } 
      } finally {
        this.chunk.arena.unlock();
      } 
    } 
    if (!doNotDestroy)
      return "(" + this.runOffset + ": not in use)"; 
    return "(" + this.runOffset + ": " + (maxNumElems - numAvail) + '/' + maxNumElems + ", offset: " + this.runOffset + ", length: " + this.runSize + ", elemSize: " + elemSize + ')';
  }
  
  public int maxNumElements() {
    if (this.chunk == null)
      return 0; 
    this.chunk.arena.lock();
    try {
      return this.maxNumElems;
    } finally {
      this.chunk.arena.unlock();
    } 
  }
  
  public int numAvailable() {
    if (this.chunk == null)
      return 0; 
    this.chunk.arena.lock();
    try {
      return this.numAvail;
    } finally {
      this.chunk.arena.unlock();
    } 
  }
  
  public int elementSize() {
    if (this.chunk == null)
      return -1; 
    this.chunk.arena.lock();
    try {
      return this.elemSize;
    } finally {
      this.chunk.arena.unlock();
    } 
  }
  
  public int pageSize() {
    return 1 << this.pageShifts;
  }
  
  void destroy() {
    if (this.chunk != null)
      this.chunk.destroy(); 
  }
  
  void lock() {
    this.lock.lock();
  }
  
  void unlock() {
    this.lock.unlock();
  }
}
