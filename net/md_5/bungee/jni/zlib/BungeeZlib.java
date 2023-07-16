package net.md_5.bungee.jni.zlib;

import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;

public interface BungeeZlib {
  void init(boolean paramBoolean, int paramInt);
  
  void free();
  
  void process(ByteBuf paramByteBuf1, ByteBuf paramByteBuf2) throws DataFormatException;
}
