package io.netty.buffer.search;

import io.netty.util.internal.PlatformDependent;

public class BitapSearchProcessorFactory extends AbstractSearchProcessorFactory {
  private final long[] bitMasks = new long[256];
  
  private final long successBit;
  
  public static class Processor implements SearchProcessor {
    private final long[] bitMasks;
    
    private final long successBit;
    
    private long currentMask;
    
    Processor(long[] bitMasks, long successBit) {
      this.bitMasks = bitMasks;
      this.successBit = successBit;
    }
    
    public boolean process(byte value) {
      this.currentMask = (this.currentMask << 1L | 0x1L) & PlatformDependent.getLong(this.bitMasks, value & 0xFFL);
      return ((this.currentMask & this.successBit) == 0L);
    }
    
    public void reset() {
      this.currentMask = 0L;
    }
  }
  
  BitapSearchProcessorFactory(byte[] needle) {
    if (needle.length > 64)
      throw new IllegalArgumentException("Maximum supported search pattern length is 64, got " + needle.length); 
    long bit = 1L;
    for (byte c : needle) {
      this.bitMasks[c & 0xFF] = this.bitMasks[c & 0xFF] | bit;
      bit <<= 1L;
    } 
    this.successBit = 1L << needle.length - 1;
  }
  
  public Processor newSearchProcessor() {
    return new Processor(this.bitMasks, this.successBit);
  }
}
