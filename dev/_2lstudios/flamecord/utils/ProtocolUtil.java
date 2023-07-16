package dev._2lstudios.flamecord.utils;

import io.netty.buffer.ByteBuf;

public class ProtocolUtil {
  private static int SEGMENT_BITS = 127;
  
  private static int CONTINUE_BIT = 128;
  
  public static int readVarInt(ByteBuf byteBuf) {
    int value = 0;
    int position = 0;
    while (byteBuf.isReadable()) {
      byte currentByte = byteBuf.readByte();
      value |= (currentByte & SEGMENT_BITS) << position;
      if ((currentByte & CONTINUE_BIT) == 0)
        break; 
      position += 7;
      if (position >= 32)
        throw new RuntimeException("VarInt is too big"); 
    } 
    return value;
  }
}
