package io.netty.buffer;

import java.nio.ByteBuffer;

class PoolArenasCache {
  private static final int INTEGER_SIZE_MINUS_ONE = 31;
  
  final PoolArena<byte[]> heapArena;
  
  final PoolArena<ByteBuffer> directArena;
  
  PoolArenasCache(PoolArena<byte[]> heapArena, PoolArena<ByteBuffer> directArena) {
    this.heapArena = heapArena;
    this.directArena = directArena;
  }
  
  boolean allocateSmall(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int sizeIdx) {
    return false;
  }
  
  boolean allocateNormal(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int sizeIdx) {
    return false;
  }
  
  boolean add(PoolArena<?> area, PoolChunk chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolArena.SizeClass sizeClass) {
    return false;
  }
  
  void trim() {}
  
  void free(boolean b) {}
  
  static int log2(int val) {
    return 31 - Integer.numberOfLeadingZeros(val);
  }
}
