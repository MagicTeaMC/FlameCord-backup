package net.md_5.bungee.jni.zlib;

public class NativeCompressImpl {
  int consumed;
  
  boolean finished;
  
  static native void initFields();
  
  native void end(long paramLong, boolean paramBoolean);
  
  native void reset(long paramLong, boolean paramBoolean);
  
  native long init(boolean paramBoolean, int paramInt);
  
  native int process(long paramLong1, long paramLong2, int paramInt1, long paramLong3, int paramInt2, boolean paramBoolean);
  
  static {
    initFields();
  }
}
