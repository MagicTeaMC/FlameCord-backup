package io.netty.buffer;

import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class PoolChunkList<T> implements PoolChunkListMetric {
  private static final Iterator<PoolChunkMetric> EMPTY_METRICS = Collections.<PoolChunkMetric>emptyList().iterator();
  
  private final PoolArena<T> arena;
  
  private final PoolChunkList<T> nextList;
  
  private final int minUsage;
  
  private final int maxUsage;
  
  private final int maxCapacity;
  
  private PoolChunk<T> head;
  
  private final int freeMinThreshold;
  
  private final int freeMaxThreshold;
  
  private PoolChunkList<T> prevList;
  
  PoolChunkList(PoolArena<T> arena, PoolChunkList<T> nextList, int minUsage, int maxUsage, int chunkSize) {
    assert minUsage <= maxUsage;
    this.arena = arena;
    this.nextList = nextList;
    this.minUsage = minUsage;
    this.maxUsage = maxUsage;
    this.maxCapacity = calculateMaxCapacity(minUsage, chunkSize);
    this.freeMinThreshold = (maxUsage == 100) ? 0 : (int)(chunkSize * (100.0D - maxUsage + 0.99999999D) / 100.0D);
    this.freeMaxThreshold = (minUsage == 100) ? 0 : (int)(chunkSize * (100.0D - minUsage + 0.99999999D) / 100.0D);
  }
  
  private static int calculateMaxCapacity(int minUsage, int chunkSize) {
    minUsage = minUsage0(minUsage);
    if (minUsage == 100)
      return 0; 
    return (int)(chunkSize * (100L - minUsage) / 100L);
  }
  
  void prevList(PoolChunkList<T> prevList) {
    assert this.prevList == null;
    this.prevList = prevList;
  }
  
  boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int sizeIdx, PoolArenasCache threadCache) {
    int normCapacity = this.arena.sizeIdx2size(sizeIdx);
    if (normCapacity > this.maxCapacity)
      return false; 
    for (PoolChunk<T> cur = this.head; cur != null; cur = cur.next) {
      if (cur.allocate(buf, reqCapacity, sizeIdx, threadCache)) {
        if (cur.freeBytes <= this.freeMinThreshold) {
          remove(cur);
          this.nextList.add(cur);
        } 
        return true;
      } 
    } 
    return false;
  }
  
  boolean free(PoolChunk<T> chunk, long handle, int normCapacity, ByteBuffer nioBuffer) {
    chunk.free(handle, normCapacity, nioBuffer);
    if (chunk.freeBytes > this.freeMaxThreshold) {
      remove(chunk);
      return move0(chunk);
    } 
    return true;
  }
  
  private boolean move(PoolChunk<T> chunk) {
    assert chunk.usage() < this.maxUsage;
    if (chunk.freeBytes > this.freeMaxThreshold)
      return move0(chunk); 
    add0(chunk);
    return true;
  }
  
  private boolean move0(PoolChunk<T> chunk) {
    if (this.prevList == null) {
      assert chunk.usage() == 0;
      return false;
    } 
    return this.prevList.move(chunk);
  }
  
  void add(PoolChunk<T> chunk) {
    if (chunk.freeBytes <= this.freeMinThreshold) {
      this.nextList.add(chunk);
      return;
    } 
    add0(chunk);
  }
  
  void add0(PoolChunk<T> chunk) {
    chunk.parent = this;
    if (this.head == null) {
      this.head = chunk;
      chunk.prev = null;
      chunk.next = null;
    } else {
      chunk.prev = null;
      chunk.next = this.head;
      this.head.prev = chunk;
      this.head = chunk;
    } 
  }
  
  private void remove(PoolChunk<T> cur) {
    if (cur == this.head) {
      this.head = cur.next;
      if (this.head != null)
        this.head.prev = null; 
    } else {
      PoolChunk<T> next = cur.next;
      cur.prev.next = next;
      if (next != null)
        next.prev = cur.prev; 
    } 
  }
  
  public int minUsage() {
    return minUsage0(this.minUsage);
  }
  
  public int maxUsage() {
    return Math.min(this.maxUsage, 100);
  }
  
  private static int minUsage0(int value) {
    return Math.max(1, value);
  }
  
  public Iterator<PoolChunkMetric> iterator() {
    this.arena.lock();
    try {
      if (this.head == null)
        return EMPTY_METRICS; 
      List<PoolChunkMetric> metrics = new ArrayList<PoolChunkMetric>();
      PoolChunk<T> cur = this.head;
      do {
        metrics.add(cur);
        cur = cur.next;
      } while (cur != null);
      return metrics.iterator();
    } finally {
      this.arena.unlock();
    } 
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    this.arena.lock();
    try {
      if (this.head == null)
        return "none"; 
      PoolChunk<T> cur = this.head;
      while (true) {
        buf.append(cur);
        cur = cur.next;
        if (cur == null)
          break; 
        buf.append(StringUtil.NEWLINE);
      } 
    } finally {
      this.arena.unlock();
    } 
    return buf.toString();
  }
  
  void destroy(PoolArena<T> arena) {
    PoolChunk<T> chunk = this.head;
    while (chunk != null) {
      arena.destroyChunk(chunk);
      chunk = chunk.next;
    } 
    this.head = null;
  }
}
