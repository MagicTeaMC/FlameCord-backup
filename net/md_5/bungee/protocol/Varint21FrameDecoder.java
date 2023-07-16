package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class Varint21FrameDecoder extends ByteToMessageDecoder {
  private static boolean DIRECT_WARNING;
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (!ctx.channel().isActive()) {
      in.skipBytes(in.readableBytes());
      return;
    } 
    in.markReaderIndex();
    for (int i = 0; i < 3; i++) {
      if (!in.isReadable()) {
        in.resetReaderIndex();
        return;
      } 
      byte read = in.readByte();
      if (read >= 0) {
        in.resetReaderIndex();
        int length = DefinedPacket.readVarInt(in);
        if (in.readableBytes() < length) {
          in.resetReaderIndex();
          return;
        } 
        out.add(in.readRetainedSlice(length));
        return;
      } 
    } 
    throw new CorruptedFrameException("length wider than 21-bit");
  }
}
