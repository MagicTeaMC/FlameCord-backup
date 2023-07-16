package dev._2lstudios.flamecord.natives;

import dev._2lstudios.flamecord.natives.compress.Compressor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class MoreByteBufUtils {
  public static ByteBuf ensureCompatible(ByteBufAllocator alloc, Compressor nativeStuff, ByteBuf buf) {
    if (isCompatible(nativeStuff, buf))
      return buf.retain(); 
    ByteBuf newBuf = alloc.directBuffer(buf.readableBytes());
    newBuf.writeBytes(buf);
    return newBuf;
  }
  
  private static boolean isCompatible(Compressor nativeStuff, ByteBuf buf) {
    if (nativeStuff.isNeedDirectBuffer())
      return buf.hasMemoryAddress(); 
    return true;
  }
}
