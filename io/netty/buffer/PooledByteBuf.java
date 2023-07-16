package io.netty.buffer;

import io.netty.util.Recycler;
import io.netty.util.internal.ObjectPool;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

abstract class PooledByteBuf<T> extends AbstractReferenceCountedByteBuf {
  private final Recycler.EnhancedHandle<PooledByteBuf<T>> recyclerHandle;
  
  protected PoolChunk<T> chunk;
  
  protected long handle;
  
  protected T memory;
  
  protected int offset;
  
  protected int length;
  
  int maxLength;
  
  PoolArenasCache cache;
  
  ByteBuffer tmpNioBuf;
  
  private ByteBufAllocator allocator;
  
  protected PooledByteBuf(ObjectPool.Handle<? extends PooledByteBuf<T>> recyclerHandle, int maxCapacity) {
    super(maxCapacity);
    this.recyclerHandle = (Recycler.EnhancedHandle)recyclerHandle;
  }
  
  void init(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int offset, int length, int maxLength, PoolArenasCache cache) {
    init0(chunk, nioBuffer, handle, offset, length, maxLength, cache);
  }
  
  void initUnpooled(PoolChunk<T> chunk, int length) {
    init0(chunk, (ByteBuffer)null, 0L, 0, length, length, (PoolArenasCache)null);
  }
  
  private void init0(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int offset, int length, int maxLength, PoolArenasCache cache) {
    assert handle >= 0L;
    assert chunk != null;
    assert !PoolChunk.isSubpage(handle) || chunk.arena.size2SizeIdx(maxLength) <= chunk.arena.smallMaxSizeIdx : "Allocated small sub-page handle for a buffer size that isn't \"small.\"";
    chunk.incrementPinnedMemory(maxLength);
    this.chunk = chunk;
    this.memory = chunk.memory;
    this.tmpNioBuf = nioBuffer;
    this.allocator = chunk.arena.parent;
    this.cache = cache;
    this.handle = handle;
    this.offset = offset;
    this.length = length;
    this.maxLength = maxLength;
  }
  
  final void reuse(int maxCapacity) {
    maxCapacity(maxCapacity);
    resetRefCnt();
    setIndex0(0, 0);
    discardMarks();
  }
  
  public final int capacity() {
    return this.length;
  }
  
  public int maxFastWritableBytes() {
    return Math.min(this.maxLength, maxCapacity()) - this.writerIndex;
  }
  
  public final ByteBuf capacity(int newCapacity) {
    if (newCapacity == this.length) {
      ensureAccessible();
      return this;
    } 
    checkNewCapacity(newCapacity);
    if (!this.chunk.unpooled)
      if (newCapacity > this.length) {
        if (newCapacity <= this.maxLength) {
          this.length = newCapacity;
          return this;
        } 
      } else if (newCapacity > this.maxLength >>> 1 && (this.maxLength > 512 || newCapacity > this.maxLength - 16)) {
        this.length = newCapacity;
        trimIndicesToCapacity(newCapacity);
        return this;
      }  
    this.chunk.decrementPinnedMemory(this.maxLength);
    this.chunk.arena.reallocate(this, newCapacity, true);
    return this;
  }
  
  public final ByteBufAllocator alloc() {
    return this.allocator;
  }
  
  public final ByteOrder order() {
    return ByteOrder.BIG_ENDIAN;
  }
  
  public final ByteBuf unwrap() {
    return null;
  }
  
  public final ByteBuf retainedDuplicate() {
    return PooledDuplicatedByteBuf.newInstance(this, this, readerIndex(), writerIndex());
  }
  
  public final ByteBuf retainedSlice() {
    int index = readerIndex();
    return retainedSlice(index, writerIndex() - index);
  }
  
  public final ByteBuf retainedSlice(int index, int length) {
    return PooledSlicedByteBuf.newInstance(this, this, index, length);
  }
  
  protected final ByteBuffer internalNioBuffer() {
    ByteBuffer tmpNioBuf = this.tmpNioBuf;
    if (tmpNioBuf == null) {
      this.tmpNioBuf = tmpNioBuf = newInternalNioBuffer(this.memory);
    } else {
      tmpNioBuf.clear();
    } 
    return tmpNioBuf;
  }
  
  protected abstract ByteBuffer newInternalNioBuffer(T paramT);
  
  protected final void deallocate() {
    if (this.handle >= 0L) {
      long handle = this.handle;
      this.handle = -1L;
      this.memory = null;
      this.chunk.decrementPinnedMemory(this.maxLength);
      this.chunk.arena.free(this.chunk, this.tmpNioBuf, handle, this.maxLength, this.cache);
      this.tmpNioBuf = null;
      this.chunk = null;
      this.cache = null;
      this.recyclerHandle.unguardedRecycle(this);
    } 
  }
  
  protected final int idx(int index) {
    return this.offset + index;
  }
  
  final ByteBuffer _internalNioBuffer(int index, int length, boolean duplicate) {
    index = idx(index);
    ByteBuffer buffer = duplicate ? newInternalNioBuffer(this.memory) : internalNioBuffer();
    buffer.limit(index + length).position(index);
    return buffer;
  }
  
  ByteBuffer duplicateInternalNioBuffer(int index, int length) {
    checkIndex(index, length);
    return _internalNioBuffer(index, length, true);
  }
  
  public final ByteBuffer internalNioBuffer(int index, int length) {
    checkIndex(index, length);
    return _internalNioBuffer(index, length, false);
  }
  
  public final int nioBufferCount() {
    return 1;
  }
  
  public final ByteBuffer nioBuffer(int index, int length) {
    return duplicateInternalNioBuffer(index, length).slice();
  }
  
  public final ByteBuffer[] nioBuffers(int index, int length) {
    return new ByteBuffer[] { nioBuffer(index, length) };
  }
  
  public final boolean isContiguous() {
    return true;
  }
  
  public final int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
    return out.write(duplicateInternalNioBuffer(index, length));
  }
  
  public final int readBytes(GatheringByteChannel out, int length) throws IOException {
    checkReadableBytes(length);
    int readBytes = out.write(_internalNioBuffer(this.readerIndex, length, false));
    this.readerIndex += readBytes;
    return readBytes;
  }
  
  public final int getBytes(int index, FileChannel out, long position, int length) throws IOException {
    return out.write(duplicateInternalNioBuffer(index, length), position);
  }
  
  public final int readBytes(FileChannel out, long position, int length) throws IOException {
    checkReadableBytes(length);
    int readBytes = out.write(_internalNioBuffer(this.readerIndex, length, false), position);
    this.readerIndex += readBytes;
    return readBytes;
  }
  
  public final int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
    try {
      return in.read(internalNioBuffer(index, length));
    } catch (ClosedChannelException ignored) {
      return -1;
    } 
  }
  
  public final int setBytes(int index, FileChannel in, long position, int length) throws IOException {
    try {
      return in.read(internalNioBuffer(index, length), position);
    } catch (ClosedChannelException ignored) {
      return -1;
    } 
  }
}
