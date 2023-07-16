package com.velocitypowered.natives.compression;

import java.util.zip.DataFormatException;

public class NativeZlibInflate {
  public static native long init();
  
  public static native long free(long paramLong);
  
  public static native boolean process(long paramLong1, long paramLong2, int paramInt1, long paramLong3, int paramInt2) throws DataFormatException;
}
