package dev._2lstudios.flamecord.natives;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class NativeEnvironmentDetector {
  private static final boolean IS_AMD64;
  
  private static final boolean IS_AARCH64;
  
  private static final boolean CAN_GET_MEMORYADDRESS;
  
  static {
    ByteBuf test = Unpooled.directBuffer();
    try {
      CAN_GET_MEMORYADDRESS = test.hasMemoryAddress();
    } finally {
      test.release();
    } 
    String osArch = System.getProperty("os.arch", "");
    IS_AMD64 = (osArch.equals("amd64") || osArch.equals("x86_64"));
    IS_AARCH64 = (osArch.equals("aarch64") || osArch.equals("arm64"));
  }
  
  public static boolean isLinux_X86_64() {
    return (CAN_GET_MEMORYADDRESS && System.getProperty("os.name", "").equalsIgnoreCase("Linux") && IS_AMD64);
  }
  
  public static boolean isLinux_AARCH64() {
    return (CAN_GET_MEMORYADDRESS && System.getProperty("os.name", "").equalsIgnoreCase("Linux") && IS_AARCH64);
  }
}
