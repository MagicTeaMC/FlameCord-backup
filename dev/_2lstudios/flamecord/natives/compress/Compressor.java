package dev._2lstudios.flamecord.natives.compress;

import io.netty.buffer.ByteBuf;
import java.io.Closeable;
import java.util.zip.DataFormatException;

public interface Compressor extends Closeable {
  void inflate(ByteBuf paramByteBuf1, ByteBuf paramByteBuf2, int paramInt) throws DataFormatException;
  
  void deflate(ByteBuf paramByteBuf1, ByteBuf paramByteBuf2) throws DataFormatException;
  
  boolean isNeedDirectBuffer();
}
