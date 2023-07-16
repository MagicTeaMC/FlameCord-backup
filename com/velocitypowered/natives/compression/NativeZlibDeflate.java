package com.velocitypowered.natives.compression;

public class NativeZlibDeflate {
  public static native long init(int paramInt);
  
  public static native long free(long paramLong);
  
  public static native int process(long paramLong1, long paramLong2, int paramInt1, long paramLong3, int paramInt2);
}
