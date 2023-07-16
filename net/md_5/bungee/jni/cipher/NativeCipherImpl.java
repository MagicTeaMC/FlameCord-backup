package net.md_5.bungee.jni.cipher;

class NativeCipherImpl {
  native long init(boolean paramBoolean, byte[] paramArrayOfbyte);
  
  native void free(long paramLong);
  
  native void cipher(long paramLong1, long paramLong2, long paramLong3, int paramInt);
}
